package com.christian.citas.enums;

import com.christian.commons.exceptions.RecursoNoEncontradoException;
import com.christian.commons.utils.StringCustomUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EstadoCita {
	PENDIENTE(1L, "Pendiente de confirmar"),
    CONFIRMADA(2L, "Confirmada por el paciente"),
    EN_CURSO(3L, "Paciente llegó a su cita"),
    FINALIZADA(4L, "Cita finalizada"),
	CANCELADA(5L, "Cita cancelada");
    
    private final Long codigo;
	private final String descripcion;
	
	public static EstadoCita obtenerEstadoCitaPorCodigo(Long codigo) {
        for (EstadoCita e : values()) {
            if (e.codigo == codigo) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Código de cita no válido: " + codigo);
    }
	
	public static EstadoCita obtenerEstadoCitaPorDescripcion(String descripcion) {
        for (EstadoCita e : values()) {
        	String descEstadoCita= StringCustomUtils.quitarAcentos(e.descripcion);
            if (descEstadoCita.equalsIgnoreCase(StringCustomUtils.quitarAcentos(descripcion))) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Descripción de cita no válida: " + descripcion);
    }
}