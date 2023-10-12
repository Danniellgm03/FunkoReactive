package org.funkoReactivo.repositories.funko;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.repositories.base.CrudRepository;
import reactor.core.publisher.Flux;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FunkoRepository extends CrudRepository<Funko, Integer, SQLException> {

    Flux<Funko> findByNombre(String name) throws SQLException;

}
