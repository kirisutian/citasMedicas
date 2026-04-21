package com.christian.citas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.christian.citas.entities.Cita;
import java.util.List;
import java.util.Optional;

import com.christian.commons.enums.EstadoRegistro;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
	
	List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);
	
	Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByIdMedicoAndEstadoRegistro(Long idMedico, EstadoRegistro estadoRegistro);
	
	boolean existsByIdPacienteAndEstadoRegistro(Long idPaciente, EstadoRegistro estadoRegistro);

}
