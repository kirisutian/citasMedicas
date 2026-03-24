package com.christian.pacientes.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class PacienteServiceImpl implements PacienteService {
	
	private final PacienteRepository pacienteRepository;
	
	private final PacienteMapper pacienteMapper;
	
	@Override
	@Transactional(readOnly = true)
	public List<PacienteResponse> listar() {
		log.info("Listado de todos los pacientes activos solicitado");
		return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(pacienteMapper::entityToResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPorId(Long id) {
		return pacienteMapper.entityToResponse(obtenerPacienteOException(id));
	}
	
	@Override
	@Transactional(readOnly = true)
	public PacienteResponse obtenerPacientePorIdSinEstado(Long id) {
		log.info("Buscando Paciente sin estado con id: {}", id);
		return pacienteMapper.entityToResponse(pacienteRepository.findById(id).orElseThrow(() ->
				new RecursoNoEncontradoException("Paciente sin estado no encontrado con id: " + id)));
	}

	@Override
	public PacienteResponse registrar(PacienteRequest request) {
		log.info("Registrando nuevo Paciente: {}", request.nombre());
		
		validarEmailUnico(request.email());
		validarTelefonoUnico(request.telefono());
		
		Paciente paciente = pacienteMapper.requestToEntity(request);
		
		paciente.setImc(calcularImc(request.peso(), request.estatura()));
		paciente.setNumExpediente(generarExpediente(request.telefono()));
		
		pacienteRepository.save(paciente);
		
		log.info("Nuevo Paciente registrado: {}", request.nombre());
		return pacienteMapper.entityToResponse(paciente);
	}

	@Override
	public PacienteResponse actualizar(PacienteRequest request, Long id) {
		Paciente paciente = obtenerPacienteOException(id);
		
		log.info("Actualizando Paciente con id: {}", id);
		
		validarCambiosUnicos(request, id);
		
		boolean telefonoCambio = !paciente.getTelefono().equals(request.telefono());
		
		pacienteMapper.updateEntityFromRequest(request, paciente);
		
		if (telefonoCambio) {
			paciente.setTelefono(request.telefono());
			paciente.setNumExpediente(generarExpediente(request.telefono()));
		}
		
		boolean cambioImc = !paciente.getPeso().equals(request.peso()) || !paciente.getEstatura().equals(request.estatura());
		
		if (cambioImc) {
			paciente.setPeso(request.peso());
			paciente.setEstatura(request.estatura());
			paciente.setImc(calcularImc(request.peso(), request.estatura()));
		}
		
		log.info("Paciente actualizado con id: {}", id);
		return pacienteMapper.entityToResponse(paciente);
	}

	@Override
	public void eliminar(Long id) {
		Paciente paciente = obtenerPacienteOException(id);
		
		log.info("Eliminando Paciente con id: {}", id);
		paciente.setEstadoRegistro(EstadoRegistro.ELIMINADO);
		
		log.info("Paciente con id {} ha sido marcado como eliminado", id);
	}
	
	private Paciente obtenerPacienteOException(Long id) {
		log.info("Buscando Paciente activo con id: {}", id);
		
		return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
				new RecursoNoEncontradoException("Paciente activo no encontrado con id: " + id));
	}
	
	private void validarEmailUnico(String email) {
		log.info("Validando email único...");
		if(pacienteRepository.existsByEmailAndEstadoRegistro(email.toLowerCase(), EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el email: " + email);
		}
	}
	
	private void validarTelefonoUnico(String telefono) {
		log.info("Validando teléfono único...");
		if(pacienteRepository.existsByEmailAndEstadoRegistro(telefono, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el teléfiono: " + telefono);
		}
	}
	
	private void validarCambiosUnicos(PacienteRequest request, Long id) {
		
		if(pacienteRepository.existsByEmailAndIdNotAndEstadoRegistro(request.email().toLowerCase(), id, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el email: " + request.email());
		}
		
		if(pacienteRepository.existsByTelefonoAndIdNotAndEstadoRegistro(request.telefono().toLowerCase(), id, EstadoRegistro.ACTIVO)) {
			throw new IllegalArgumentException("Ya existe un Paciente registrado con el teléfono: " + request.telefono());
		}
	}
	
	private Double calcularImc(Double peso, Double estatura) {
		return peso / (estatura * estatura);
	}
	
	
	private String generarExpediente(String telefono) {
		StringBuilder expediente = new StringBuilder();
		
		for (char c : telefono.toCharArray()) {
			expediente.append(c).append("X");
		}
		
		return expediente.toString();
	}
	
}
