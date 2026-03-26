package com.christian.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.christian.commons.configuration.FeignClientConfig;
import com.christian.commons.dto.MedicoResponse;

@FeignClient(name = "medicos-msv", configuration = FeignClientConfig.class)
public interface MedicoClient {
	
	@GetMapping("/{id}")
	MedicoResponse obtenerMedicoPorId(@PathVariable Long id);
	
	@GetMapping("/id-medico/{id}")
	MedicoResponse obtenerMedicoPorIdSinEstado(@PathVariable Long id);

}
