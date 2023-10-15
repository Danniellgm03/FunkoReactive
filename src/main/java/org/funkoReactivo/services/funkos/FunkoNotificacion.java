package org.funkoReactivo.services.funkos;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.models.Notificacion;
import reactor.core.publisher.Flux;

public interface FunkoNotificacion {
    Flux<Notificacion<Funko>> getNotificacion();

    void notify(Notificacion<Funko> notify);
}
