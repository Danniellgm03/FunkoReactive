package org.funkoReactivo.services.funkos;

import org.funkoReactivo.exceptions.funko.FunkoNoEncontradoException;
import org.funkoReactivo.exceptions.funko.FunkoNoGuardado;
import org.funkoReactivo.models.Funko;
import org.funkoReactivo.models.Notificacion;
import org.funkoReactivo.repositories.funko.FunkoRepository;
import org.funkoReactivo.services.storage.FunkoStorageServ;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImpl implements FunkoService{

    private static FunkoServiceImpl instance;
    private Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);

    private final FunkoRepository repository;

    private final FunkoCache cache;

    private final FunkoNotificacionImpl notificacion;

    private final FunkoStorageServ storageFunko;

    private FunkoServiceImpl(FunkoRepository repositoryFunko, FunkoCache cache, FunkoNotificacionImpl notificacion,  FunkoStorageServ storageFunko){
        this.repository = repositoryFunko;
        this.cache = cache;
        this.notificacion = notificacion;
        this.storageFunko = storageFunko;
    }

    public static FunkoServiceImpl getInstance(FunkoRepository repositoryFunko, FunkoCache cache, FunkoNotificacionImpl notificacion,  FunkoStorageServ storageFunko){
        if(instance == null){
            instance = new FunkoServiceImpl(repositoryFunko, cache, notificacion, storageFunko);
        }
        return instance;
    }


    @Override
    public Flux<Funko> findAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo todos los funkos");
        return repository.findAll();
    }

    @Override
    public Flux<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Obteniendo funkos con nombre: {}", nombre);
        return repository.findByNombre(nombre);
    }

    @Override
    public Mono<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Buscando funko con id: {}", id);
        return cache.get(id).switchIfEmpty(repository.findById(id).flatMap(
                funko -> {
                    try {
                        return cache.put(funko.getId(), funko).then(Mono.justOrEmpty(funko));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        )).switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + id)));
    }

    public Mono<Funko> saveWithoutNotify(Funko funko) throws Exception {
        logger.debug("Guardando funko: {}", funko);
        return repository.save(funko).doOnSuccess(
        funkoSaved -> {
            try {
                cache.put(funkoSaved.getId(), funkoSaved);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public Mono<Funko> save(Funko funko) throws Exception {
        return saveWithoutNotify(funko).doOnSuccess(
        funkoSaved -> {
            try {
                notificacion.notify(new Notificacion<>(Notificacion.Tipo.NEW, funkoSaved));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).switchIfEmpty(Mono.error(new FunkoNoGuardado("No se ha podido guardar el funko con id cod: "+funko.getId())));
    }

    public Mono<Funko> updateWithoutNotify(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Actualizando funko: {}", funko);
        return this.findById(funko.getId())
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + funko.getId())))
                .flatMap(
                        funkoFound -> {
                            try {
                                return repository.update(funko).flatMap(updatedFunko ->
                                {
                                    try {
                                        return cache.put(updatedFunko.getId(), updatedFunko).then(Mono.just(updatedFunko)).doOnError(
                                                error -> logger.error(error.getMessage()));
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
    }

    @Override
    public Mono<Funko> update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        return updateWithoutNotify(funko).doOnSuccess(
                funkoUpdated -> {
                    try {
                        notificacion.notify(new Notificacion<>(Notificacion.Tipo.UPDATED, funkoUpdated));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    private Mono<Funko> deleteByIdWithoutNotification(Integer id) throws SQLException {
        logger.debug("Eliminando funko con id: {}", id);
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new FunkoNoEncontradoException("No existe el funko con id: " + id)))
                .flatMap(alumno -> {
                    try {
                        return cache.delete(alumno.getId())
                                .then(repository.deleteById(alumno.getId()))
                                .thenReturn(alumno);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public Mono<Boolean> deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        return  deleteByIdWithoutNotification(id).doOnSuccess(
                funkoDeleted -> {
                    try {
                        notificacion.notify(new Notificacion<>(Notificacion.Tipo.DELETED, funkoDeleted));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).map(funko -> true);
    }

    @Override
    public Mono<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando todos los funkos");
        cache.clear();
        return repository.deleteAll().then(Mono.empty());
    }

    @Override
    public Mono<Boolean> backup() throws SQLException, ExecutionException, InterruptedException, ExportException {
        logger.debug("Realizando backup de los funkos");
        return repository.findAll().collectList().flatMap(
                funkos -> {
                    try {
                        return storageFunko.exportToJsonAsync(funkos);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ).switchIfEmpty(Mono.just(false));
    }

    @Override
    public Flux<Funko> importCsv() throws ExecutionException, InterruptedException {
        logger.debug("Importando funkos desde csv");
        Path path = Path.of("");
        String path_directory = path.toAbsolutePath().toString() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "data" + File.separator;
        String file = path_directory + "funkos.csv";
        return storageFunko.importFromCsvAsync(file).flatMap(
            funko -> {
                try {
                    return save(funko);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
        });
    }

    public Flux<Notificacion<Funko>> getNotifications() {
        return notificacion.getNotificacion();
    }

    public void stopCleaner(){
        cache.shutdown();
    }
}
