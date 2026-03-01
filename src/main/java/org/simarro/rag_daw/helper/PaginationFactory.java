package org.simarro.rag_daw.helper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import org.simarro.rag_daw.model.dto.PeticionListadoFiltrado;

@Component
public class PaginationFactory {
    
    public Pageable createPageable(PeticionListadoFiltrado peticion) {
        return PageRequest.of(
            peticion.getPage(),
            peticion.getSize(),
            createSort(peticion.getSort())
        );
    }
    
    /**
     * Crea un objeto Sort a partir de los criterios de ordenación.
     * Si no hay criterios de ordenación, devuelve un Sort vacío.
     * 
     * @param sortCriteria Lista de criterios de ordenación en formato "campo,dirección"
     * @return Sort configurado con los criterios proporcionados o Sort vacío si no hay criterios
     */
    private Sort createSort(List<String> sortCriteria) {
        if (sortCriteria == null || sortCriteria.isEmpty()) {
            return Sort.unsorted();
        }
        
        List<Order> orders = sortCriteria.stream()
            .map(this::createOrder)
            .collect(Collectors.toList());
            
        return Sort.by(orders);
    }
    
    /**
     * Crea un objeto Order a partir de un criterio de ordenación.
     * 
     * @param criterion Criterio en formato "campo,dirección"
     * @return Order configurado con el campo y dirección especificados
     */
    private Order createOrder(String criterion) {
        String[] parts = criterion.split(",");
        Direction direction = parts.length > 1 
            ? Direction.fromString(parts[1]) 
            : Direction.ASC;
        return new Order(direction, parts[0]);
    }
}