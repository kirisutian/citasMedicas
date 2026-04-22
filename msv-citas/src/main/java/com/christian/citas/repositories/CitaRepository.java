package com.christian.citas.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.christian.citas.entities.Cita;
import com.christian.citas.enums.EstadoCita;

import java.util.List;
import java.util.Optional;

import com.christian.commons.enums.EstadoRegistro;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
	
	List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);
	
	Optional<Cita> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
			Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estadosCita);
	
	boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
			Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estadosCita);

}
