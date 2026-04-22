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
import com.christian.commons.enums.EstadoRegistro;
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
	public CitaResponse registrar(CitaRequest request) {
		log.info("Registrando nueva Cita: {}", request);
		
		MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
		
		PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());
		
		Cita cita = citaRepository.save(citaMapper.requestAEntidad(request));
		
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
		}
		
		log.info("Cita actualizada con id: {}", cita.getId());
		return citaMapper.entidadAResponse(cita, paciente, medico);
	}

	@Override
	public void eliminar(Long id) {
		Cita cita = obtenerCitaActivaOException(id);
		log.info("Eliminando Cita con id: {}", id);
		
		cita.eliminar();
		log.info("Cita con id {} ha sido marcada como eliminada", id);
	}
	
	@Override
	public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
		Cita cita = obtenerCitaActivaOException(idCita);
		log.info("Actualizando estado de la cita: {}", cita.getId());
		
		EstadoCita estadoCita = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);
		cita.actualizarEstadoCita(estadoCita);
		
		log.info("Estado de la cita {} actualizado correctamente", cita.getId());
	}
	
	private Cita obtenerCitaActivaOException(Long id) {
		log.info("Buscando Cita activa con id: {}", id);
		return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(
				() -> new RecursoNoEncontradoException("Cita activa no encontrada con el id: " + id));
	}
	
	private MedicoResponse obtenerMedicoActivo(Long id) {
		log.info("Buscando médico activo con id {} en el servicio remoto...", id);
		return medicoClient.obtenerMedicoActivoPorId(id);
	}
	
	private MedicoResponse obtenerMedicoSinEstado(Long id) {
		log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);
		return medicoClient.obtenerMedicoPorIdSinEstado(id);
	}
	
	private PacienteResponse obtenerPacienteActivo(Long id) {
		log.info("Buscando médico activo con id {} en el servicio remoto...", id);
		return pacienteClient.obtenerPacienteActivoPorId(id);
	}
	
	private PacienteResponse obtenerPacienteSinEstado(Long id) {
		log.info("Buscando médico sin estado con id {} en el servicio remoto...", id);
		return pacienteClient.obtenerPacientePorIdSinEstado(id);
	}
}
