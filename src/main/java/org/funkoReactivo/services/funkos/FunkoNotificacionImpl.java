package org.funkoReactivo.services.funkos;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.models.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import static java.lang.ref.Cleaner.create;

public class FunkoNotificacionImpl implements FunkoNotificacion{

    private static FunkoNotificacionImpl instance;

    private final Flux<Notificacion<Funko>> FunkoNotificationFlux;

    private FluxSink<Notificacion<Funko>> FunkoNotification;

    private FunkoNotificacionImpl(){
        this.FunkoNotificationFlux = Flux.<Notificacion<Funko>>create(emitter -> this.FunkoNotification = emitter).share();
    }

    public static FunkoNotificacionImpl getInstance(){
        if(instance == null){
            instance = new FunkoNotificacionImpl();
        }
        return instance;
    }

    @Override
    public Flux<Notificacion<Funko>> getNotificacion() {
        return FunkoNotificationFlux;
    }

    @Override
    public void notify(Notificacion<Funko> notify) {
        FunkoNotification.next(notify);
    }
}
