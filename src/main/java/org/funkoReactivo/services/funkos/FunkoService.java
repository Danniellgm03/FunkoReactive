package org.funkoReactivo.services.funkos;


import org.funkoReactivo.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.rmi.server.ExportException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface FunkoService {

    Flux<Funko> findAll() throws SQLException, ExecutionException, InterruptedException;

    Flux<Funko> findByNombre(String nombre) throws SQLException, ExecutionException, InterruptedException;

    Mono<Funko> findById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    Mono<Funko> save(Funko funko) throws Exception;

    Mono<Funko> update(Funko funko) throws SQLException, ExecutionException, InterruptedException;

    Mono<Boolean> deleteById(Integer id) throws SQLException, ExecutionException, InterruptedException;

    Mono<Void> deleteAll() throws SQLException, ExecutionException, InterruptedException;

    Mono<Boolean> backup() throws SQLException, ExecutionException, InterruptedException, ExportException;

    Flux<Funko> importCsv() throws ExecutionException, InterruptedException;

}
