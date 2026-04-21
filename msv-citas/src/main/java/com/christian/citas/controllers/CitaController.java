package com.christian.citas.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.citas.services.CitaService;
import com.christian.commons.controllers.CommonController;

@RestController
public class CitaController extends CommonController<CitaRequest, CitaResponse, CitaService> {

	public CitaController(CitaService service) {
		super(service);
	}

}
