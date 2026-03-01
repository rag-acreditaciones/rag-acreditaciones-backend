package org.simarro.rag_daw.srv.specification.operacion;
import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.enums.TipoOperacionBusqueda;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class MenorQueOperacionStrategy implements OperacionBusquedaStrategy {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Predicate crearPredicado(
        Root<?> root, 
        CriteriaBuilder criteriaBuilder, 
        FiltroBusqueda filtro
    ) {
        return criteriaBuilder.lessThan(
            root.get(filtro.getAtributo()), 
            (Comparable) filtro.getValor()
        );
    }

    @Override
    public boolean soportaOperacion(TipoOperacionBusqueda operacion) {
        return operacion == TipoOperacionBusqueda.MENOR_QUE;
    }
}
