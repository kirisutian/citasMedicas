package com.christian.citas.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.citas.mappers.CitaMapper;
import com.christian.citas.repositories.CitaRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class CitaServiceImpl implements CitaService {
	
	private final CitaRepository citaRepository;
	
	private final CitaMapper citaMapper;
	
	@Override
	public List<CitaResponse> listar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CitaResponse obtenerPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CitaResponse registrar(CitaRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CitaResponse actualizar(CitaRequest request, Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void eliminar(Long id) {
		// TODO Auto-generated method stub
		
	}

}
