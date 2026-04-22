package com.christian.commons.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "msv-citas")
public interface CitaClient {
	
	@GetMapping("/id-medico/{idMedico}/citas-asignadas")
	void medicoTieneCitasAsignadas(@PathVariable Long idMedico);
	
	@GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
	void pacienteTieneCitasAsignadas(@PathVariable Long idPaciente);

}
