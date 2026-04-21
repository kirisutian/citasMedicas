package com.christian.pacientes.mappers;

import org.springframework.stereotype.Component;

import com.christian.commons.dto.PacienteRequest;
import com.christian.commons.dto.PacienteResponse;
import com.christian.commons.enums.EstadoRegistro;
import com.christian.commons.mappers.CommonMapper;
import com.christian.pacientes.entities.Paciente;

@Component
public class PacienteMapper implements CommonMapper<PacienteRequest, PacienteResponse, Paciente> {

	@Override
	public PacienteResponse entidadAResponse(Paciente entity) {
		if(entity == null) return null;
		return new PacienteResponse(
				entity.getId(),
				String.join(" ",
						entity.getNombre(),
						entity.getApellidoPaterno(),
						entity.getApellidoMaterno()),
				entity.getEdad(),
				entity.getPeso(),
				entity.getEstatura(),
				entity.getImc(),
				entity.getEmail(),
				entity.getTelefono(),
				entity.getDireccion(),
				entity.getNumExpediente()
		);
	}

	@Override
	public Paciente requestAEntidad(PacienteRequest request) {
	    if (request == null) return null;

	    return Paciente.builder()
	            .nombre(request.nombre())
	            .apellidoPaterno(request.apellidoPaterno())
	            .apellidoMaterno(request.apellidoMaterno())
	            .email(request.email().toLowerCase())
	            .edad(request.edad())
	            .estatura(request.estatura())
	            .peso(request.peso())
	            .telefono(request.telefono())
	            .direccion(request.direccion())
	            .estadoRegistro(EstadoRegistro.ACTIVO)
	            .build();
	}
	
}