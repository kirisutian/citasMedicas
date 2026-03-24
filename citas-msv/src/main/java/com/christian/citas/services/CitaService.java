package com.christian.citas.services;

import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.commons.services.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse>{
	
	CitaResponse obtenerCitaPorIdSinEstado(Long id);

}
