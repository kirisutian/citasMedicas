package com.christian.citas.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.citas.entities.Cita;
import com.christian.citas.enums.EstadoCita;
import com.christian.citas.mappers.CitaMapper;
import com.christian.citas.repositories.CitaRepository;
import com.christian.commons.clients.MedicoClient;
import com.christian.commons.clients.PacienteClient;
import com.christian.commons.dto.MedicoResponse;
import com.christian.commons.dto.PacienteResponse;
import com.christian.commons.enums.DisponibilidadMedico;
import com.christian.commons.enums.EstadoRegistro;
import com.christian.commons.exceptions.EntidadRelacionadaException;
import com.christian.commons.exceptions.RecursoNoEncontradoException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CitaServiceImpl implements CitaService {
	
	private final CitaRepository citaRepository;
	
	private final CitaMapper citaMapper;
	
	private final MedicoClient medicoClient;
	
	private final PacienteClient pacienteClient;
	
	private final List<EstadoCita> ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS =
			List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);
	
	@Override
	@Transactional(readOnly = true)
	public List<CitaResponse> listar() {
		
		log.info("Listado de todas las Citas activas solicitado");
		return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(cita ->
					citaMapper.entidadAResponse(
							cita,
							obtenerPacienteSinEstado(cita.getIdPaciente()),
							obtenerMedicoSinEstado(cita.getIdMedico()))
				).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CitaResponse obtenerPorId(Long id) {
		
		Cita cita = obtenerCitaActivaOException(id);
		return citaMapper.entidadAResponse(
				cita,
				obtenerPacienteSinEstado(cita.getIdPaciente()),
				obtenerMedicoSinEstado(cita.getIdMedico()));
	}
	
	@Override
	@Transactional(readOnly = true)
	public void medicoTieneCitasAsignadas(Long idMedico) {
		log.info("Validando citas asignadas con estados {} para el médico con id: {}",
				ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS, idMedico);
		
		boolean tieneCitas = citaRepository
				.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
						idMedico,
						EstadoRegistro.ACTIVO,
						ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
		
		if (tieneCitas)
			throw new EntidadRelacionadaException(
					"No se puede modificar el médico ya que tiene citas con estados: "
							+ ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
	}

	@Override
	@Transactional(readOnly = true)
	public void pacienteTieneCitasAsignadas(Long idPaciente) {
		log.info("Validando citas asignadas con estados {} para el paciente con id: {}",
				ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS, idPaciente);
		
		boolean tieneCitas = citaRepository
				.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
						idPaciente,
						EstadoRegistro.ACTIVO,
						ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
		
		if (tieneCitas)
			throw new EntidadRelacionadaException(
					"No se puede modificar el paciente ya que tiene citas con estados: "
							+ ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
	}

	@Override
	public CitaResponse registrar(CitaRequest request) {
		log.info("Registrando nueva Cita: {}", request);
		
		MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
		
		validarDisponibilidadMedico(medico);
		
		PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
		
		validarPacienteTieneRegistrosAsignados(request.idPaciente());
		
		validarMedicoTieneRegistrosAsignados(request.idMedico());
		
		Cita cita = citaRepository.save(citaMapper.requestAEntidad(request));
		
		cambiarDisponibilidadSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());
		
		log.info("Cita registrada exitosamente: {}", cita.getId());
		return citaMapper.entidadAResponse(cita, paciente, medico);
	}

	@Override
	public CitaResponse actualizar(CitaRequest request, Long id) {
		Cita cita = obtenerCitaActivaOException(id);
		log.info("Actualizando Cita con id: {}", cita.getId());
		
		MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
		
		PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
		
		cita.actualizar(request.idPaciente(), request.idMedico(),
				request.fechaCita(), request.sintomas());
		
		if (request.idEstadoCita() != null) {
			EstadoCita estadoCita = EstadoCita.obtenerEstadoCitaPorCodigo(request.idEstadoCita());
			cita.actualizarEstadoCita(estadoCita);
			cambiarDisponibilidadSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());
		}
		
		log.info("Cita actualizada con id: {}", cita.getId());
		return citaMapper.entidadAResponse(cita, paciente, medico);
	}

	@Override
	public void eliminar(Long id) {
		Cita cita = obtenerCitaActivaOException(id);
		log.info("Eliminando Cita con id: {}", id);
		
		cita.eliminar();
		
		if(cita.getEstadoCita() == EstadoCita.PENDIENTE) {
			cambiarDisponibilidadMedico(cita.getIdMedico(),
					DisponibilidadMedico.DISPONIBLE.getCodigo());
		}
		
		log.info("Cita con id {} ha sido marcada como eliminada", id);
	}
	
	@Override
	public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
		Cita cita = obtenerCitaActivaOException(idCita);
		log.info("Actualizando estado de la cita: {}", cita.getId());
		
		EstadoCita estadoCita = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);
		cita.actualizarEstadoCita(estadoCita);
		
		cambiarDisponibilidadSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());
		
		log.info("Estado de la cita {} actualizado correctamente", cita.getId());
	}
	
	private Cita obtenerCitaActivaOException(Long id) {
		log.info("Buscando Cita activa con id: {}", id);
		return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(
				() -> new RecursoNoEncontradoException("Cita activa no encontrada con el id: " + id));
	}
	
	private MedicoResponse obtenerMedicoActivo(Long idMedico) {
		log.info("Buscando médico activo con id {} en el servicio remoto...", idMedico);
		return medicoClient.obtenerMedicoActivoPorId(idMedico);
	}
	
	private MedicoResponse obtenerMedicoSinEstado(Long idMedico) {
		log.info("Buscando médico sin estado con id {} en el servicio remoto...", idMedico);
		return medicoClient.obtenerMedicoPorIdSinEstado(idMedico);
	}
	
	private PacienteResponse obtenerPacienteActivo(Long idPaciente) {
		log.info("Buscando médico activo con id {} en el servicio remoto...", idPaciente);
		return pacienteClient.obtenerPacienteActivoPorId(idPaciente);
	}
	
	private PacienteResponse obtenerPacienteSinEstado(Long idPaciente) {
		log.info("Buscando médico sin estado con id {} en el servicio remoto...", idPaciente);
		return pacienteClient.obtenerPacientePorIdSinEstado(idPaciente);
	}
	
	private void validarPacienteTieneRegistrosAsignados(Long idPaciente) {
		
		log.info("Validando si el paciente tiene una cita activa con los estados: {}", ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
		
		if( citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
				idPaciente, EstadoRegistro.ACTIVO, ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS))
			
			throw new EntidadRelacionadaException(
					"No se puede regitrar la cita ya que el paciente solo puede tener una cita activa con los estados: "
							+ ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
	}
	
	private void validarMedicoTieneRegistrosAsignados(Long idMedico) {
		
		log.info("Validando si el médico tiene una cita activa con los estados: {}", ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
		
		if( citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
				idMedico, EstadoRegistro.ACTIVO, ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS))
			
			throw new EntidadRelacionadaException(
					"No se puede regitrar la cita ya que el médico solo puede tener una cita activa con los estados: "
							+ ESTADOS_INVALIDOS_REGISTROS_ASIGNADOS);
	}
	
	private void validarDisponibilidadMedico(MedicoResponse medico) {
		
		log.info("Validando si el médico se encuentra en estado: {}", DisponibilidadMedico.DISPONIBLE);
		
		if(!DisponibilidadMedico.DISPONIBLE.getDescripcion().equalsIgnoreCase(medico.disponibilidad()))
			throw new IllegalStateException("El médico no se encuentra en estado " + DisponibilidadMedico.DISPONIBLE);
	}
	
	private void cambiarDisponibilidadSegunEstadoCita(Long idMedico, EstadoCita estadoCita) {
		
		switch(estadoCita) {
			
			case PENDIENTE, CONFIRMADA ->
				cambiarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());
				
			case EN_CURSO ->
				cambiarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
			
			case FINALIZADA, CANCELADA ->
				cambiarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());
		}
	}
	
	private void cambiarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
		
		log.info("Actualizando disponibilidad del médico con id {} a {}",
				idMedico, DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad));
		
		medicoClient.actualizarDisponibilidadMedico(idMedico, idDisponibilidad);
	}
	
	
}
