package org.simarro.rag_daw.srv.specification;

import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.srv.specification.operacion.ContieneOperacionStrategy;
import org.simarro.rag_daw.srv.specification.operacion.IgualOperacionStrategy;
import org.simarro.rag_daw.srv.specification.operacion.MayorQueOperacionStrategy;
import org.simarro.rag_daw.srv.specification.operacion.MenorQueOperacionStrategy;
import org.simarro.rag_daw.srv.specification.operacion.OperacionBusquedaStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class FiltroBusquedaSpecification<T> implements Specification<T> {

    private List<FiltroBusqueda> filtrosBusqueda;
    private final List<OperacionBusquedaStrategy> estrategias;

    private List<OperacionBusquedaStrategy> getDefaultStrategies() {
        return List.of(
                new IgualOperacionStrategy(),
                new ContieneOperacionStrategy(),
                new MayorQueOperacionStrategy(),
                new MenorQueOperacionStrategy()
        // Añadir nuevas estrategias aquí sin modificar otras clases
        );
    }

    public FiltroBusquedaSpecification(List<FiltroBusqueda> filtrosBusqueda) {
        this.filtrosBusqueda = filtrosBusqueda;
        // Cuando no se especifican estrategias asignamos las estrategias de las
        // operaciones por defecto
        this.estrategias = getDefaultStrategies();
    }

    public FiltroBusquedaSpecification(List<FiltroBusqueda> filtrosBusqueda,
            List<OperacionBusquedaStrategy> estrategias) {
        this.filtrosBusqueda = filtrosBusqueda;
        this.estrategias = estrategias;
    }

    // Método Builder
    public Specification<T> build(List<FiltroBusqueda> filtros) {
        if (filtros == null || filtros.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicados = filtros.stream()
                    .map(filtro -> crearPredicado(root, criteriaBuilder, filtro))
                    .collect(Collectors.toList());

            return predicados.isEmpty()
                    ? criteriaBuilder.conjunction()
                    : criteriaBuilder.and(predicados.toArray(new Predicate[0]));
        };
    }

    private Predicate crearPredicado(Root<T> root, CriteriaBuilder criteriaBuilder, FiltroBusqueda filtro) {
        return estrategias.stream()
                .filter(estrategia -> estrategia.soportaOperacion(filtro.getOperacion()))
                .findFirst()
                .map(estrategia -> estrategia.crearPredicado(root, criteriaBuilder, filtro))
                .orElseThrow(() -> new UnsupportedOperationException(
                        "Operador de filtro no permitido: " + filtro.getOperacion()));
    }

    @Override
    public Predicate toPredicate(@NonNull Root<T> root, @Nullable CriteriaQuery<?> query,
            @NonNull CriteriaBuilder criteriaBuilder) {
        // No hay filtros que aplicar
        if (filtrosBusqueda == null || filtrosBusqueda.isEmpty()) {
            return criteriaBuilder.conjunction();
        }
        // Hay filtros que aplicar
        List<Predicate> predicados = filtrosBusqueda.stream()
                .map(filtro -> crearPredicado(root, criteriaBuilder, filtro))
                .collect(Collectors.toList());

        // Si no hay predicados, devolvemos una conjunción vacía para evitar errores
        return predicados.isEmpty()
                ? criteriaBuilder.conjunction()
                : criteriaBuilder.and(predicados.toArray(new Predicate[0]));
    }
}
