package com.christian.medicos.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.christian.medicos.entities.Medico;
import java.util.List;
import java.util.Optional;

import com.christian.commons.enums.EstadoRegistro;


@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
	
	List<Medico> findByEstadoRegistro(EstadoRegistro estadoRegistro);
	
	Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByEmailAndEstadoRegistro(String email, EstadoRegistro estadoRegistro);
	
	boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estadoRegistro);
	
	boolean existsByCedulaProfesionalAndEstadoRegistro(String cedulaProfesional, EstadoRegistro estadoRegistro);
	
	boolean existsByEmailAndIdNotAndEstadoRegistro(String email, Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByTelefonoAndIdNotAndEstadoRegistro(String telefono, Long id, EstadoRegistro estadoRegistro);
	
	boolean existsByCedulaProfesionalAndIdNotAndEstadoRegistro(String cedulaProfesional, Long id, EstadoRegistro estadoRegistro);

}
