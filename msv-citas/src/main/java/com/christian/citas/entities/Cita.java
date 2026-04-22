package com.christian.citas.entities;

import java.time.LocalDateTime;

import com.christian.citas.enums.EstadoCita;
import com.christian.commons.enums.EstadoRegistro;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "CITAS")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Cita {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID_CITA")
    private Long id;
	
	@Column(name = "ID_PACIENTE", nullable = false)
	private Long idPaciente;
	
	@Column(name = "ID_MEDICO", nullable = false)
	private Long idMedico;
	
	@Column(name = "FECHA_CITA", nullable = false)
	private LocalDateTime fechaCita;
	
	@Column(name = "SINTOMAS", nullable = false, length = 500)
	private String sintomas;
	
    @Column(name = "ESTADO_CITA", nullable = false)
    @Enumerated(EnumType.STRING)
	private EstadoCita estadoCita;
	
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    @Enumerated(EnumType.STRING)
	private EstadoRegistro estadoRegistro;
    
    public void eliminar() {
    	this.puedeEliminar();
    	this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }
    
    public void actualizar(Long idPaciente, Long idMedico,
    		LocalDateTime fechaCita, String sintomas) {
    	this.puedeActualizar();
		this.idPaciente = idPaciente;
		this.idMedico = idMedico;
		this.fechaCita = fechaCita;
		this.sintomas = sintomas;
	}
    
    public void actualizarEstadoCita(EstadoCita nuevoEstadoCita) {
    	switch(this.estadoCita) {
    	
    		case PENDIENTE -> {
    			if( !(nuevoEstadoCita == EstadoCita.CONFIRMADA ||
    					nuevoEstadoCita == (EstadoCita.CANCELADA) ||
    					nuevoEstadoCita == (EstadoCita.PENDIENTE)))
    				
    				throw new IllegalStateException("La cita con estado " + this.estadoCita +
    						" solo puede cambiar a " + EstadoCita.CONFIRMADA + " o " + EstadoCita.CANCELADA);
    		}
    		
    		case CONFIRMADA -> {
    			if( !(nuevoEstadoCita == (EstadoCita.EN_CURSO) ||
    					nuevoEstadoCita == (EstadoCita.CANCELADA) ||
    					nuevoEstadoCita == (EstadoCita.CONFIRMADA)))
    				
    				throw new IllegalStateException("La cita con estado " + this.estadoCita +
    						" solo puede cambiar a " + EstadoCita.EN_CURSO + " o " + EstadoCita.CANCELADA);
    		}
    		
    		case EN_CURSO -> {
    			if(!(nuevoEstadoCita == EstadoCita.FINALIZADA))
    				
    				throw new IllegalStateException("La cita con estado " + this.estadoCita +
    						" solo puede cambiar a " + EstadoCita.FINALIZADA);
    		}
    		
    		case FINALIZADA, CANCELADA -> {
    			if( !(nuevoEstadoCita == this.estadoCita))
    				
    				throw new IllegalStateException("La cita con estado " + this.estadoCita
    						+ " no puede cambiar de estado");
    		}
    	}
    	this.estadoCita = nuevoEstadoCita;
    }
    
    private void puedeEliminar() {
    	if (this.estadoCita == (EstadoCita.CONFIRMADA) ||
    		this.estadoCita == (EstadoCita.EN_CURSO)) {
			
    		throw new IllegalStateException("Una cita con estado " +
    		EstadoCita.CONFIRMADA + " o " + EstadoCita.EN_CURSO +
    		" no se puede eliminar");
		}
    }
    
    private void puedeActualizar() {
    	if (!(this.estadoCita == EstadoCita.PENDIENTE
    			|| this.estadoCita == EstadoCita.CONFIRMADA)) {
			
    		throw new IllegalStateException(
					"La cita solo puede actualizarse si está en estado de: "
			+ EstadoCita.PENDIENTE + " o " + EstadoCita.CONFIRMADA);
		}
    }

}
