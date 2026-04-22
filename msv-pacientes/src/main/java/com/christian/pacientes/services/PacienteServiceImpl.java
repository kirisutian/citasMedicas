package com.christian.pacientes.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.christian.commons.clients.CitaClient;
import com.christian.commons.dto.PacienteRequest;
import com.christian.commons.dto.PacienteResponse;
import com.christian.commons.enums.EstadoRegistro;
import com.christian.commons.exceptions.RecursoNoEncontradoException;
import com.christian.pacientes.entities.Paciente;
import com.christian.pacientes.mappers.PacienteMapper;
import com.christian.pacientes.repositories.PacienteRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PacienteServiceImpl implements PacienteService{
	
	private final PacienteRepository pacienteRepository;
	
	private final PacienteMapper pacienteMapper;
	
	private final CitaClient citaClient;
	
	@Override
	@Transactional(readOnly = true)
	public List<PacienteResponse> listar() {
		log.info("Listado de todos los pacientes activos solicitado");
		return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(pacienteMapper::entidadAResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPorId(Long id) {
		return pacienteMapper.entidadAResponse(obtenerPacienteActivoOException(id));
	}
	
	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
		log.info("Buscando Paciente sin estado con id: {}", id);
		return pacienteMapper.entidadAResponse(
				pacienteRepository.findById(id).orElseThrow(() ->
				new RecursoNoEncontradoException("Paciente no encontrado con el id: " + id)));
	}

	@Override
	public PacienteResponse registrar(PacienteRequest request) {
		log.info("Registrando nuevo Paciente: {}", request.nombre());
		
		validarEmailUnico(request.email());
		validarTelefonoUnico(request.telefono());
		
		Paciente paciente = pacienteMapper.requestAEntidad(request);
		
		paciente.setNumExpediente(paciente.generarNumExpediente());
		paciente.setImc(paciente.calcularImc());
		
		pacienteRepository.save(paciente);
		log.info("Paciente registrado con éxito: {}", paciente.getNombre());
		return pacienteMapper.entidadAResponse(paciente);
	}

	@Override
	public PacienteResponse actualizar(PacienteRequest request, Long id) {
		Paciente paciente = obtenerPacienteActivoOException(id);
		
		pacienteTieneCitasAsignadas(id);
		
		log.info("Actualizando Paciente: {}", paciente.getNombre());
		
		validarCambiosUnicos(request, id);
		
		paciente.actualizar(
				request.nombre(),
				request.apellidoPaterno(),
				request.apellidoMaterno(),
				request.edad(),
				request.peso(),
				request.estatura(),
				request.email(),
				request.telefono(),
				request.direccion());
		
		paciente.setNumExpediente(paciente.generarNumExpediente());
		paciente.setImc(paciente.calcularImc());
		
		log.info("Paciente actualizado con éxito: {}", paciente.getNombre());
		return pacienteMapper.entidadAResponse(paciente);
	}

	@Override
	public void eliminar(Long id) {
		Paciente paciente = obtenerPacienteActivoOException(id);
		
		log.info("Eliminando Paciente con id: {}", id);
		
		pacienteTieneCitasAsignadas(id);
		
		paciente.eliminar();
		
		log.info("Paciente con id: {} ha sido marcado como eliminado", id);
	}
	
	private Paciente obtenerPacienteActivoOException(Long id) {
        log.info("Buscando Paciente activo con id: {}", id);
        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
                        new RecursoNoEncontradoException("Paciente activo no encontrado con el id: " + id));
    }
	
	private void validarEmailUnico(String email) {
		if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(email, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el email: " + email);
		}
	}
	
	private void validarTelefonoUnico(String telefono) {
		if (pacienteRepository.existsByTelefonoAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el teléfono: " + telefono);
		}
	}
	
	private void validarCambiosUnicos(PacienteRequest request, Long id) {
		
		if (pacienteRepository.existsByEmailIgnoreCaseAndIdNotAndEstadoRegistro(
				request.email(), id, EstadoRegistro.ACTIVO)) {
			
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el email: " + request.email());
		}
		
		if (pacienteRepository.existsByTelefonoAndIdNotAndEstadoRegistro(
				request.telefono(), id, EstadoRegistro.ACTIVO)) {
			
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el teléfono: " + request.telefono());
		}
	}
	
	private void pacienteTieneCitasAsignadas(Long id) {
    	citaClient.pacienteTieneCitasAsignadas(id);
    }

}
