package org.funkoReactivo.services.funkos;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.repositories.funko.FunkoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class FunkoServiceImpl implements FunkoService{

    private static FunkoServiceImpl instance;
    private Logger logger = LoggerFactory.getLogger(FunkoServiceImpl.class);

    private final FunkoRepository repository;

    private final FunkoCache cache;

    //private final FunkoStorageServ storageFunko;

    private FunkoServiceImpl(FunkoRepository repositoryFunko, FunkoCache cache){
        this.repository = repositoryFunko;
        this.cache = cache;
        //this.storageFunko = storageFunko;
    }

    public static FunkoServiceImpl getInstance(FunkoRepository repositoryFunko, FunkoCache cache){
        if(instance == null){
            instance = new FunkoServiceImpl(repositoryFunko, cache);
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
                        return cache.put(funko.getId(), funko).then(Mono.just(funko));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        ));
    }

    @Override
    public Mono<Funko> save(Funko funko) throws Exception {
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
    public Mono<Funko> update(Funko funko) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Actualizando funko: {}", funko);
        return repository.update(funko).doOnSuccess(
                funkoUpdated -> {
                    try {
                        cache.put(funkoUpdated.getId(), funkoUpdated);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public Mono<Boolean> deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando funko con id: {}", id);
        return repository.deleteById(id).doOnSuccess(
                funkoDeleted -> {
                    try {
                        cache.delete(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
    }

    @Override
    public Mono<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException {
        logger.debug("Eliminando todos los funkos");
        cache.clear();
        return repository.deleteAll().then(Mono.empty());
    }

    @Override
    public Mono<Void> backup() throws SQLException, ExecutionException, InterruptedException, ExportException {
        return null;
    }

    @Override
    public Flux<Funko> importCsv() throws ExecutionException, InterruptedException {
        return null;
    }

    public void stopCleaner(){
        cache.shutdown();
    }
}
