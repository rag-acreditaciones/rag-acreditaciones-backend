package org.simarro.rag_daw.rag.srv.impl;

import java.util.List;

import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.helper.PaginationFactory;
import org.simarro.rag_daw.helper.PeticionListadoFiltradoConverter;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;
import org.simarro.rag_daw.rag.model.db.RagConfiguracionDb;
import org.simarro.rag_daw.rag.model.dto.RagConfiguracionDTO;
import org.simarro.rag_daw.rag.repository.RagConfiguracionRepository;
import org.simarro.rag_daw.rag.srv.RagConfiguracionService;
import org.simarro.rag_daw.rag.srv.mapper.RagConfiguracionMapper;
import org.simarro.rag_daw.srv.specification.FiltroBusquedaSpecification;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

@Service
public class RagConfiguracionServiceImpl implements RagConfiguracionService {

    private final RagConfiguracionRepository repository;
    private final RagConfiguracionMapper mapper;
    private final PaginationFactory paginationFactory;
    private final PeticionListadoFiltradoConverter peticionConverter;

    public RagConfiguracionServiceImpl(
            RagConfiguracionRepository repository,
            RagConfiguracionMapper mapper,
            PaginationFactory paginationFactory,
            PeticionListadoFiltradoConverter peticionConverter) {
        this.repository = repository;
        this.mapper = mapper;
        this.paginationFactory = paginationFactory;
        this.peticionConverter = peticionConverter;
    }

    @Override
    public List<RagConfiguracionDTO> listarActivas() {
        return mapper.ragConfiguracionDbListToRagConfiguracionDTOList(
                repository.findByActivoTrue());
    }

    @Override
    public RagConfiguracionDTO obtenerPorId(Long id) {
        return repository.findById(id)
            .map(mapper::ragConfiguracionDbToRagConfiguracionDTO)
            .orElseThrow(() -> new EntityNotFoundException(
                "Configuración RAG no encontrada: " + id));
    }

    @Override
    public RagConfiguracionDTO obtenerPorDefecto() {
        return repository.findByPorDefectoTrue()
            .map(mapper::ragConfiguracionDbToRagConfiguracionDTO)
            .orElseThrow(() -> new IllegalStateException(
                "No hay configuración RAG por defecto"));
    }

    @Override
    public RagConfiguracionDTO obtenerPorNombre(String nombre) {
        return repository.findByNombre(nombre)
            .map(mapper::ragConfiguracionDbToRagConfiguracionDTO)
            .orElseThrow(() -> new EntityNotFoundException(
                "Configuración RAG no encontrada: " + nombre));
    }

    @Override
    public PaginaResponse<RagConfiguracionDTO> findAll(
            String[] filter, int page, int size, String[] sort) throws FiltroException {
        PeticionListadoFiltrado peticion =
            peticionConverter.convertFromParams(filter, page, size, sort);
        return findAll(peticion);
    }

    @SuppressWarnings("null")
    @Override
    public PaginaResponse<RagConfiguracionDTO> findAll(
            PeticionListadoFiltrado peticionListadoFiltrado) throws FiltroException {
        try {
            Pageable pageable = paginationFactory.createPageable(peticionListadoFiltrado);

            Specification<RagConfiguracionDb> spec =
                new FiltroBusquedaSpecification<>(
                    peticionListadoFiltrado.getListaFiltros());

            Page<RagConfiguracionDb> page = repository.findAll(spec, pageable);

            return mapper.pageToPaginaResponse(
                page,
                peticionListadoFiltrado.getListaFiltros(),
                peticionListadoFiltrado.getSort());

        } catch (JpaSystemException e) {
            String cause = (e.getRootCause() != null && e.getRootCause().getMessage() != null)
                ? e.getRootCause().getMessage() : "";
            throw new FiltroException("BAD_OPERATOR_FILTER",
                "Error: No se puede realizar esa operación sobre el atributo", cause);
        } catch (PropertyReferenceException e) {
            throw new FiltroException("BAD_ATTRIBUTE_ORDER",
                "Error: No existe el atributo de ordenación", e.getMessage());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new FiltroException("BAD_ATTRIBUTE_FILTER",
                "Error: Posiblemente no existe el atributo en la tabla", e.getMessage());
        }
    }
}
