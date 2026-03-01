package org.simarro.rag_daw.srv.specification.operacion;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BusquedaStrategyConfig {
    @Bean
    public List<OperacionBusquedaStrategy> operacionBusquedaStrategies() {
        return List.of(
            new IgualOperacionStrategy(),
            new ContieneOperacionStrategy(),
            new MayorQueOperacionStrategy(),
            new MenorQueOperacionStrategy()
            // Añadir nuevas estrategias aquí sin modificar otras clases
        );
    }
}