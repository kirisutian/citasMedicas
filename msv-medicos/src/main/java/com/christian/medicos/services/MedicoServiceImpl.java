package com.christian.medicos.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.christian.commons.clients.CitaClient;
import com.christian.commons.dto.MedicoRequest;
import com.christian.commons.dto.MedicoResponse;
import com.christian.commons.enums.DisponibilidadMedico;
import com.christian.commons.enums.EspecialidadMedico;
import com.christian.commons.enums.EstadoRegistro;
import com.christian.commons.exceptions.RecursoNoEncontradoException;
import com.christian.medicos.entities.Medico;
import com.christian.medicos.mappers.MedicoMapper;
import com.christian.medicos.repositories.MedicoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class MedicoServiceImpl implements MedicoService {
	
	private final MedicoRepository medicoRepository;
	
	private final MedicoMapper medicoMapper;
	
	private final CitaClient citaClient;
	
	@Override
	@Transactional(readOnly = true)
	public List<MedicoResponse> listar() {
		log.info("Listado de todos los médicos activos solicitado");
		return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
				.map(medicoMapper::entidadAResponse).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public MedicoResponse obtenerPorId(Long id) {
		return medicoMapper.entidadAResponse(obtenerMedicoActivoOException(id));
	}
	
	@Override
	@Transactional(readOnly = true)
	public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
		log.info("Buscando Médico sin estado con id: {}", id);
		return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(() ->
				new RecursoNoEncontradoException("Médico sin estado no encontrado con el id: " + id)));
	}

	@Override
	public MedicoResponse registrar(MedicoRequest request) {
		
		log.info("Registrando nuevo Médico: {}", request.nombre());
		
		validarEmailUnico(request.email());
        validarTelefonoUnico(request.telefono());
        validarCedulaUnica(request.cedulaProfesional());
        
        Medico medico = medicoMapper.requestAEntidad(request);
		
        medico.setEspecialidad(
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad())
        );
        
        medico.setDisponibilidad(DisponibilidadMedico.DISPONIBLE);
        
        medicoRepository.save(medico);
		
		log.info("Médico registrado con éxito: {}", medico.getNombre());
		return medicoMapper.entidadAResponse(medico);
	}

	@Override
	public MedicoResponse actualizar(MedicoRequest request, Long id) {
		Medico medico = obtenerMedicoActivoOException(id);
		log.info("Actualizando Médico con id: {}", id);
		
		medicoTieneCitasAsignadas(id);
		
		validarCambiosUnicos(request, medico);
		
		medico.actualizar(
				request.nombre(),
				request.apellidoPaterno(),
				request.apellidoMaterno(),
				request.edad(),
				request.email(),
				request.telefono(),
				request.cedulaProfesional(),
				EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));
		
		log.info("Médico actualizado con éxito: {}", id);
		
		return medicoMapper.entidadAResponse(medico);
	}

	@Override
	public void eliminar(Long id) {
		Medico medico = obtenerMedicoActivoOException(id);
		log.info("Eliminando Médico con id: {}", id);
		
		medicoTieneCitasAsignadas(id);
		
		medico.eliminar();
		log.info("Médico con id {} ha sido eliminado", id);
	}

	@Override
	public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
		Medico medico = obtenerMedicoActivoOException(idMedico);
		
		DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.
				obtenerDisponibilidadPorCodigo(idDisponibilidad);
		
		if (medico.getDisponibilidad() == nuevaDisponibilidad) return;
		
		DisponibilidadMedico anteriorDisponibilidad = medico.getDisponibilidad();
		
		medico.setDisponibilidad(nuevaDisponibilidad);
		
		log.info("Disponibilidad del médico con id {} cambió de {} a {}",
				idMedico, anteriorDisponibilidad, nuevaDisponibilidad);
	}
	
	private Medico obtenerMedicoActivoOException(Long id) {
		log.info("Buscando Médico con estado {} con id: {}", EstadoRegistro.ACTIVO, id);
		return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO).orElseThrow(() ->
				new RecursoNoEncontradoException("Médico activo no encontrado con el id: " + id));
	}
	
	private void validarEmailUnico(String email) {
        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                email, EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con el email: " + email);
        }
    }

    private void validarTelefonoUnico(String telefono) {
        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                telefono, EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con el teléfono: " + telefono);
        }
    }

    private void validarCedulaUnica(String cedula) {
        if (medicoRepository.existsByCedulaProfesionalAndEstadoRegistro(
                cedula, EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con la cédula: " + cedula);
        }
    }
    
    private void validarCambiosUnicos(MedicoRequest request, Medico medico) {

        if (!medico.getEmail().equalsIgnoreCase(request.email()) &&
            medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                    request.email(), EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con el email: " + request.email());
        }

        if (!medico.getTelefono().equals(request.telefono()) &&
            medicoRepository.existsByTelefonoAndEstadoRegistro(
                    request.telefono(), EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con el teléfono: " + request.telefono());
        }

        if (!medico.getCedulaProfesional().equals(request.cedulaProfesional()) &&
            medicoRepository.existsByCedulaProfesionalAndEstadoRegistro(
                    request.cedulaProfesional(), EstadoRegistro.ACTIVO)) {

            throw new IllegalArgumentException(
                    "Ya existe un Médico registrado con la cédula: " +
                            request.cedulaProfesional());
        }
    }
    
    private void medicoTieneCitasAsignadas(Long id) {
    	citaClient.medicoTieneCitasAsignadas(id);
    }

}
