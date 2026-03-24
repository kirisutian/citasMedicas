package com.christian.citas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.citas.services.CitaService;
import com.christian.commons.controllers.CommonController;

import jakarta.validation.constraints.Positive;

@RestController
@Validated
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService> {

	public CitaController(CitaService service) {
		super(service);
	}
	
	@GetMapping("/id-cita/{id}")
	public ResponseEntity<CitaResponse> obtenerCitaPorIdSinEstado(
			@PathVariable
			@Positive(message = "El ID debe ser positivo") Long id) {
		return ResponseEntity.ok(service.obtenerCitaPorIdSinEstado(id));
	}

}
