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
	
	private final PacienteClient pacienteClient;
	
	private final MedicoClient medicoClient;
	
	@Override
	@Transactional(readOnly = true)
	public List<CitaResponse> listar() {
		log.info("Listado de todas las citas activas solicitado");
		return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(cita ->
					citaMapper.entityToResponse(
							cita,
							obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
							obtenerMedicoResponseSinEstado(cita.getIdMedico()))
				).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public CitaResponse obtenerPorId(Long id) {
		Cita cita = obtenerCitaOException(id);
		return citaMapper.entityToResponse(
				cita,
				obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
				obtenerMedicoResponseSinEstado(cita.getIdMedico()));
	}
	
	@Override
	@Transactional(readOnly = true)
	public CitaResponse obtenerCitaPorIdSinEstado(Long id) {
		log.info("Buscando Cita sin estado con id: {}", id);
		
		Cita cita = citaRepository.findById(id).orElseThrow(() -> 
			new RecursoNoEncontradoException("Cita sin estado no encontrada con id: " + id));
		
		return citaMapper.entityToResponse(
				cita,
				obtenerPacienteResponseSinEstado(cita.getIdPaciente()),
				obtenerMedicoResponseSinEstado(cita.getIdMedico()));
	}

	@Override
	public CitaResponse registrar(CitaRequest request) {
		log.info("Registrando nueva Cita: {}", request);
		
		//Validar que existan Paciente y Médico
		PacienteResponse paciente = obtenerPacienteResponse(request.idPaciente());
		MedicoResponse medico = obtenerMedicoResponse(request.idMedico());
		
		Cita cita = citaRepository.save(citaMapper.requestToEntity(request));
		
		log.info("Cita registrada exitosamente: {}", cita);
        return citaMapper.entityToResponse(cita, paciente, medico);
	}

	@Override
	public CitaResponse actualizar(CitaRequest request, Long id) {
		Cita cita = obtenerCitaOException(id);
		
        log.info("Actualizando Cita con id: {}", id);
        
        //Validar que existan Paciente y Médico
        PacienteResponse paciente = obtenerPacienteResponse(request.idPaciente());
        MedicoResponse medico = obtenerMedicoResponse(request.idMedico());
        
        EstadoCita estadoNuevo = EstadoCita.fromCodigo(request.idEstadoCita());
        
        citaMapper.updateEntityFromRequest(request, cita, estadoNuevo);
        
        log.info("Cita actualizada con id: {}", id);
        return citaMapper.entityToResponse(cita, paciente, medico);
	}

	@Override
	public void eliminar(Long id) {
		Cita cita = obtenerCitaOException(id);
        log.info("Eliminando Cita con id: {}", id);
        
        validarEstadoCitaAlEliminar(cita);
        
        //Cambiar disponibilidad del Médico a DISPONIBLE solo si esta en PENDIENTE
        
        cita.setEstadoRegistro(EstadoRegistro.ELIMINADO);
        log.info("Cita con id {} ha sido marcada como eliminada", id);
	}
	
	private Cita obtenerCitaOException(Long id) {
		log.info("Buscando Cita activa con id: {}", id);
		
		return citaRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
				new RecursoNoEncontradoException("Cita activa no encontrado con id: " + id));
	}
	
	private void validarEstadoCitaAlEliminar(Cita cita) {
		if (cita.getEstadoCita() == EstadoCita.CONFIRMADA || cita.getEstadoCita() == EstadoCita.EN_CURSO) {
			throw new IllegalStateException("No se puede eliminar una cita " +
            		EstadoCita.CONFIRMADA.getDescripcion() + " o "
            		+ EstadoCita.EN_CURSO.getDescripcion());
		}
	}
	
	private PacienteResponse obtenerPacienteResponse(Long idPaciente) {
		return pacienteClient.obtenerPacientePorId(idPaciente);
	}
	
	private PacienteResponse obtenerPacienteResponseSinEstado(Long idPaciente) {
		return pacienteClient.obtenerPacientePorIdSinEstado(idPaciente);
	}
	
	private MedicoResponse obtenerMedicoResponse(Long idPaciente) {
		return medicoClient.obtenerMedicoPorId(idPaciente);
	}
	
	private MedicoResponse obtenerMedicoResponseSinEstado(Long idPaciente) {
		return medicoClient.obtenerMedicoPorIdSinEstado(idPaciente);
	}

}
