package org.simarro.rag_daw.documentos.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

import org.simarro.rag_daw.documentos.model.db.DocumentoDb;
import org.simarro.rag_daw.documentos.model.db.SeccionTematicaDb;
import org.simarro.rag_daw.documentos.model.dto.DocumentoDetailDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoResponseDTO;
import org.simarro.rag_daw.documentos.model.dto.DocumentoUploadDTO;
import org.simarro.rag_daw.documentos.model.enums.EstadoDocumento;
import org.simarro.rag_daw.documentos.repository.DocumentoRepository;
import org.simarro.rag_daw.documentos.repository.SeccionTematicaRepository;
import org.simarro.rag_daw.documentos.service.DocumentoService;
import org.simarro.rag_daw.documentos.service.mapper.DocumentoMapper;
import org.simarro.rag_daw.exception.FiltroException;
import org.simarro.rag_daw.exception.ResourceNotFoundException;
import org.simarro.rag_daw.helper.PaginationFactory;
import org.simarro.rag_daw.helper.PeticionListadoFiltradoConverter;
import org.simarro.rag_daw.model.dto.PaginaResponse;
import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;
import org.simarro.rag_daw.rag.model.dto.DocumentoMetadataDTO;
import org.simarro.rag_daw.rag.srv.IngestaService;
import org.simarro.rag_daw.srv.specification.FiltroBusquedaSpecification;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

@Service
public class DocumentoServiceImpl implements DocumentoService {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

    private final DocumentoRepository documentoRepository;
    private final SeccionTematicaRepository seccionTematicaRepository;
    private final IngestaService ingestaService;
    private final DocumentoMapper mapper;
    private final PaginationFactory paginationFactory;
    private final PeticionListadoFiltradoConverter peticionConverter;

    public DocumentoServiceImpl(DocumentoRepository documentoRepository,
            SeccionTematicaRepository seccionTematicaRepository,
            IngestaService ingestaService,
            DocumentoMapper mapper,
            PaginationFactory paginationFactory,
            PeticionListadoFiltradoConverter peticionConverter) {
        this.documentoRepository = documentoRepository;
        this.seccionTematicaRepository = seccionTematicaRepository;
        this.ingestaService = ingestaService;
        this.mapper = mapper;
        this.paginationFactory = paginationFactory;
        this.peticionConverter = peticionConverter;
    }

    @Override
    @Transactional
    public DocumentoResponseDTO subirDocumento(MultipartFile file,
            DocumentoUploadDTO uploadDTO,
            String subidoPor) {
        validarFichero(file);

        SeccionTematicaDb seccion = seccionTematicaRepository.findById(uploadDTO.seccionTematicaId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Seccion tematica no encontrada con id: " + uploadDTO.seccionTematicaId()));

        byte[] pdfBytes;
        try {
            pdfBytes = file.getBytes();
        } catch (IOException e) {
            throw new IllegalStateException("Error al leer el fichero subido", e);
        }

        String base64 = Base64.getEncoder().encodeToString(pdfBytes);

        DocumentoDb documento = new DocumentoDb();
        documento.setNombreFichero(file.getOriginalFilename());
        documento.setContentType(file.getContentType());
        documento.setSizeBytes(file.getSize());
        documento.setDescripcion(uploadDTO.descripcion());
        documento.setBase64Contenido(base64);
        documento.setSeccionTematica(seccion);
        documento.setSubidoPor(subidoPor);
        documento.setEstado(EstadoDocumento.PENDIENTE);
        documento.setFechaSubida(LocalDateTime.now());

        DocumentoDb saved = documentoRepository.save(documento);

        procesarIngesta(saved, seccion, pdfBytes, subidoPor);

        return mapper.toResponseDTO(saved);
    }

    @SuppressWarnings("null")
    @Override
    public PaginaResponse<DocumentoResponseDTO> findAll(String[] filter, int page, int size, String[] sort)
            throws FiltroException {
        try {
            PeticionListadoFiltrado peticion = peticionConverter.convertFromParams(filter, page, size, sort);
            Pageable pageable = paginationFactory.createPageable(peticion);

            Specification<DocumentoDb> spec = new FiltroBusquedaSpecification<>(
                    peticion.getListaFiltros());

            Page<DocumentoDb> resultado = documentoRepository.findAll(spec, pageable);

            return mapper.pageToPaginaResponse(
                    resultado,
                    peticion.getListaFiltros(),
                    peticion.getSort());

        } catch (JpaSystemException e) {
            String cause = (e.getRootCause() != null && e.getRootCause().getMessage() != null)
                    ? e.getRootCause().getMessage()
                    : "";
            throw new FiltroException("BAD_OPERATOR_FILTER",
                    "Error: No se puede realizar esa operacion sobre el atributo", cause);
        } catch (PropertyReferenceException e) {
            throw new FiltroException("BAD_ATTRIBUTE_ORDER",
                    "Error: No existe el atributo de ordenacion", e.getMessage());
        } catch (InvalidDataAccessApiUsageException e) {
            throw new FiltroException("BAD_ATTRIBUTE_FILTER",
                    "Error: Posiblemente no existe el atributo en la tabla", e.getMessage());
        }
    }

    @Override
    public DocumentoResponseDTO findById(Long id) {
        DocumentoDb documento = findDocumentoOrThrow(id);
        return mapper.toResponseDTO(documento);
    }

    @Override
    public byte[] descargar(Long id) {
        DocumentoDb documento = findDocumentoOrThrow(id);
        return Base64.getDecoder().decode(documento.getBase64Contenido());
    }

    @Override
    public DocumentoDetailDTO preview(Long id) {
        DocumentoDb documento = findDocumentoOrThrow(id);
        return mapper.toDetailDTO(documento);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        DocumentoDb documento = findDocumentoOrThrow(id);
        documento.setEstado(EstadoDocumento.ELIMINADO);
        documentoRepository.save(documento);
    }

    private DocumentoDb findDocumentoOrThrow(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Documento no encontrado con id: " + id));
    }

    private void validarFichero(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El fichero es obligatorio y no puede estar vacio");
        }
        if (!CONTENT_TYPE_PDF.equals(file.getContentType())) {
            throw new IllegalArgumentException("Solo se aceptan ficheros PDF (application/pdf)");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El fichero supera el tamano maximo permitido (20 MB)");
        }
    }

    private void procesarIngesta(DocumentoDb saved, SeccionTematicaDb seccion,
            byte[] pdfBytes, String subidoPor) {
        try {
            saved.setEstado(EstadoDocumento.PROCESANDO);
            documentoRepository.save(saved);

            DocumentoMetadataDTO metadata = new DocumentoMetadataDTO(
                    saved.getId(),
                    saved.getNombreFichero(),
                    seccion.getNombre(),
                    subidoPor);

            ingestaService.procesarPdf(pdfBytes, metadata);

            saved.setEstado(EstadoDocumento.PROCESADO);
            documentoRepository.save(saved);
        } catch (Exception e) {
            saved.setEstado(EstadoDocumento.ERROR);
            documentoRepository.save(saved);
        }
    }
}
