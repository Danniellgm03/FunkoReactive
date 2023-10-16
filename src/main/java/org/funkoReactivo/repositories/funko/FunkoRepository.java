package org.funkoReactivo.repositories.funko;

import org.funkoReactivo.models.Funko;
import org.funkoReactivo.repositories.base.CrudRepository;
import reactor.core.publisher.Flux;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz para el repositorio de Funko
 * @see CrudRepository
 * @see Funko
 * @author daniel
 */

public interface FunkoRepository extends CrudRepository<Funko, Integer, SQLException> {

    /**
     * Busca un Funko por su nombre
     * @param name
     */
    Flux<Funko> findByNombre(String name) throws SQLException;

}
