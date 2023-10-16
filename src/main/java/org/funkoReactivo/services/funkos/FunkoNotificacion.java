package org.funkoReactivo.services.funkos;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.models.Notificacion;
import reactor.core.publisher.Flux;

/**
 * Interfaz para notificar a los subscriptores
 */
public interface FunkoNotificacion {

    /**
     * Obtiene la notificacion
     */
    Flux<Notificacion<Funko>> getNotificacion();

    /**
     * Notifica a los subscriptores
     * @param notify
     */
    void notify(Notificacion<Funko> notify);
}
