package com.christian.medicos.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.christian.commons.enums.EstadoRegistro;
import com.christian.medicos.entities.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long>{
	
	Optional<Medico> findByIdAndEstadoRegistro(Long id, EstadoRegistro estadoRegistro);
	
	List<Medico> findByEstadoRegistro(EstadoRegistro estado);
	
	boolean existsByEmailIgnoreCaseAndEstadoRegistro(String email, EstadoRegistro estado);

    boolean existsByTelefonoAndEstadoRegistro(String telefono, EstadoRegistro estado);

    boolean existsByCedulaProfesionalAndEstadoRegistro(String cedulaProfesional, EstadoRegistro estado);

    boolean existsByEmailIgnoreCaseAndIdNotAndEstadoRegistro(
            String email, Long id, EstadoRegistro estado);

    boolean existsByTelefonoAndIdNotAndEstadoRegistro(
            String telefono, Long id, EstadoRegistro estado);

    boolean existsByCedulaProfesionalAndIdNotAndEstadoRegistro(
            String cedulaProfesional, Long id, EstadoRegistro estado);

}
