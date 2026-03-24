package com.christian.pacientes.services;

import com.christian.commons.dto.PacienteRequest;
import com.christian.commons.dto.PacienteResponse;
import com.christian.commons.services.CrudService;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {
	
	PacienteResponse obtenerPacientePorIdSinEstado(Long id);

}
