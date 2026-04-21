package com.christian.medicos.services;

import com.christian.commons.dto.MedicoRequest;
import com.christian.commons.dto.MedicoResponse;
import com.christian.commons.services.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {
	
	MedicoResponse obtenerMedicoPorIdSinEstado(Long id);
	
	void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);

}
