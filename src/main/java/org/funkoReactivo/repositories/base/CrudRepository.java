package org.funkoReactivo.repositories.base;

import org.funkoReactivo.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;

public interface CrudRepository<T, ID, EX extends Exception> {

    Mono<T> save(T t) throws SQLException, EX;

    Mono<T> update(T t) throws SQLException, EX;

    Mono<Funko> findById(ID id) throws SQLException;

    Flux<T> findAll() throws SQLException;

    Mono<Boolean> deleteById(ID id) throws SQLException;

    Mono<Void> deleteAll() throws SQLException;

}
