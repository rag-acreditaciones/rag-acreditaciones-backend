package org.simarro.rag_daw.srv.specification.operacion;

import org.simarro.rag_daw.model.dto.FiltroBusqueda;
import org.simarro.rag_daw.model.enums.TipoOperacionBusqueda;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


public interface OperacionBusquedaStrategy {
     /**
     * Crea un predicado para una operación de búsqueda específica.
     * 
     * @param root La raíz de la consulta
     * @param criteriaBuilder Constructor de criterios
     * @param filtro Filtro de búsqueda con atributo, operación y valor
     * @return Predicado para la operación de búsqueda
     */
    Predicate crearPredicado(
        Root<?> root, 
        CriteriaBuilder criteriaBuilder, 
        FiltroBusqueda filtro
    );


    /**
     * Indica si la estrategia soporta el tipo de operación
     * 
     * @param operacion Operación a verificar
     * @return true si soporta la operación, false en caso contrario
     */
    boolean soportaOperacion(TipoOperacionBusqueda operacion);
}