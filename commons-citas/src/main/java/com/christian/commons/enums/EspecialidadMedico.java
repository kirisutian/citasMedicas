package com.christian.commons.enums;

import com.christian.commons.exceptions.RecursoNoEncontradoException;
import com.christian.commons.utils.StringCustomUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EspecialidadMedico {
	
	MEDICINA_GENERAL(1L, "Medicina General"),
	PEDIATRIA(2L, "Pediatría"),
	CARDIOLOGIA(3L, "Cardiología"),
	DERMATOLOGIA(4L, "Dermatología"),
	NEUROLOGIA(5L, "Neurología"),
	GINECOLOGIA(6L, "Ginecología y Obstetricia"),
	PSIQUIATRIA(7L, "Psiquiatría"),
	TRAUMATOLOGIA(8L, "Traumatología y Ortopedia"),
	ONCOLOGIA(9L, "Oncología"),
	OTORRINOLARINGOLOGIA(10L, "Otorrinolaringología"),
	OFTALMOLOGIA(11L, "Oftalmología"),
	ENDOCRINOLOGIA(12L, "Endocrinología"),
	NEFROLOGIA(13L, "Nefrología"),
	REUMATOLOGIA(14L, "Reumatología"),
	UROLOGIA(15L, "Urología");
	
	private final Long codigo;
	private final String descripcion;
	
	public static EspecialidadMedico obtenerEspecialidadPorCodigo(Long codigo) {
        for (EspecialidadMedico e : values()) {
            if (e.codigo == codigo) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Código de especialidad no válido: " + codigo);
    }
	
	public static EspecialidadMedico obtenerEspecialidadPorDescripcion(String descripcion) {
        for (EspecialidadMedico e : values()) {
        	String descEspecialidad = StringCustomUtils.quitarAcentos(e.descripcion);
            if (descEspecialidad.equalsIgnoreCase(StringCustomUtils.quitarAcentos(descripcion))) {
                return e;
            }
        }
        throw new RecursoNoEncontradoException("Descripción de especialidad no válida: " + descripcion);
    }
}
