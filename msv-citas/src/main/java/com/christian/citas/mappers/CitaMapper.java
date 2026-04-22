package com.christian.citas.mappers;

import org.springframework.stereotype.Component;

import com.christian.citas.dto.CitaRequest;
import com.christian.citas.dto.CitaResponse;
import com.christian.citas.entities.Cita;
import com.christian.citas.enums.EstadoCita;
import com.christian.commons.dto.DatosMedico;
import com.christian.commons.dto.DatosPaciente;
import com.christian.commons.dto.MedicoResponse;
import com.christian.commons.dto.PacienteResponse;
import com.christian.commons.enums.EstadoRegistro;
import com.christian.commons.mappers.CommonMapper;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {

	@Override
	public Cita requestAEntidad(CitaRequest request) {
		if(request == null) return null;

        return Cita.builder()
                .idPaciente(request.idPaciente())
                .idMedico(request.idMedico())
                .fechaCita(request.fechaCita())
                .sintomas(request.sintomas())
                .estadoCita(EstadoCita.PENDIENTE)
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
	}

	@Override
	public CitaResponse entidadAResponse(Cita entidad) {
		if(entidad == null) return null;

        return new CitaResponse(
        		entidad.getId(),
                null,
                null,
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
	}
	
	public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
		if(entidad == null) return null;

        return new CitaResponse(
        		entidad.getId(),
        		this.pacienteResponseADatosPaciente(paciente),
                this.medicoResponseADatosMedico(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion());
	}

	private DatosPaciente pacienteResponseADatosPaciente(PacienteResponse paciente) {
		if (paciente == null) return null;
		
		return new DatosPaciente(
				paciente.nombre(),
				paciente.numExpediente(),
				paciente.edad() + " años",
				paciente.peso() + " kg.",
				paciente.estatura() + " m.",
				String.join(" ",
						Math.round(paciente.imc() * 100.0) / 100.0 + "",
						this.clasificacionIMC(paciente.imc())),
				paciente.telefono()
		);
	}
	
	private DatosMedico medicoResponseADatosMedico(MedicoResponse medico) {
		if (medico == null) return null;
		
		return new DatosMedico(
				medico.nombre(),
				medico.cedulaProfesional(),
				medico.especialidad()
		);
	}
	
	private String clasificacionIMC(double imc) {
		if(imc < 18.5) return "Bajo peso";
		if(imc < 25) return "Peso normal";
		if(imc < 30) return "Sobrepeso";
		if(imc < 35) return "Obesidad grado I";
		if(imc < 40) return "Obesidad grado II";
		return "Obesidad grado III";
	}
}
