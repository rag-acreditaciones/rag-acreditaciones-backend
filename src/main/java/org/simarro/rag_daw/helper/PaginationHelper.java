package org.simarro.rag_daw.helper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;

import java.util.ArrayList;
import java.util.List;

public class PaginationHelper {

    private PaginationHelper() {
        // Constructor privado para prevenir instanciación
    }

    /**
     * Crea un objeto Pageable a partir de los parámetros dados.
     *
     * @param page Número de página (empieza en 0)
     * @param size Tamaño de la página
     * @param sort Array de criterios de ordenación (ej. "campo,asc")
     * @return Objeto Pageable con la paginación y ordenación configurada
     */
    public static Pageable createPageable(int page, int size, String[] sort) {        
         // Crear sorts (ordenación de los datos)
       List<Order> criteriosOrdenacion= new ArrayList<Order>();
       //El primer criterio de ordenacion siempre deberá de contener el orden(asc,desc)
       if(sort[0].contains(",")){ //Hay más de un criterio de ordenación
        //Tenemos un vector de ordenaciones en 'sort' y debemos leerlos
        for (String criterioOrdenacion : sort) {
          String[] orden = criterioOrdenacion.split(",");
          if (orden.length > 1)
            criteriosOrdenacion.add(new Order(Direction.fromString(orden[1]), orden[0]));
          else // por defecto asc si no se dice nada
            criteriosOrdenacion.add(new Order(Direction.fromString("asc"), orden[0]));          
         }
       } else{ //Solo hay un criterio de ordenación
            //El primer elemento del vector de sort es la dirección y el segundo el campo
            criteriosOrdenacion.add(new Order(Direction.fromString(sort[1]), sort[0]));        
       }       
       Sort sorts= Sort.by(criteriosOrdenacion);
        
        return PageRequest.of(page, size, sorts);
    }

    /**
     * Crea un objeto Pageable a partir de los parámetros dados.
     *
     * @param page Número de página (empieza en 0)
     * @param size Tamaño de la página
     * @param sort Array de criterios de ordenación (ej. "campo,asc")
     * @return Objeto Pageable con la paginación y ordenación configurada
     */
    public static Pageable createPageable(Integer page, Integer size, List<String> sort) {        
      // Crear sorts (ordenación de los datos)
    List<Order> criteriosOrdenacion= new ArrayList<Order>();
    //El primer criterio de ordenacion siempre deberá de contener el orden(asc,desc)
     //Tenemos un vector de ordenaciones en 'sort' y debemos leerlos
     for (String criterioOrdenacion : sort) {
       String[] orden = criterioOrdenacion.split(",");
       if (orden.length > 1)
         criteriosOrdenacion.add(new Order(Direction.fromString(orden[1]), orden[0]));
       else // por defecto asc si no se dice nada
         criteriosOrdenacion.add(new Order(Direction.fromString("asc"), orden[0]));          
      }
           
    Sort sorts= Sort.by(criteriosOrdenacion);
     return PageRequest.of(page.intValue(), size.intValue(), sorts);
 }

}
