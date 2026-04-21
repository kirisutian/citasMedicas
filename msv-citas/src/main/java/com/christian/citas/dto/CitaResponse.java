package com.christian.citas.dto;

import java.time.LocalDateTime;

import com.christian.commons.dto.DatosMedico;
import com.christian.commons.dto.DatosPaciente;
import com.fasterxml.jackson.annotation.JsonFormat;

public record CitaResponse(
		Long id,
		DatosPaciente paciente,
		DatosMedico medico,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
		LocalDateTime fechaCita,
		String sintomas,
		String estadoCita
) {}