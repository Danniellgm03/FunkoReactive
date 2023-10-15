package org.funkoReactivo.repositories.funko;


import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.Result;
import org.funkoReactivo.enums.Modelo;
import org.funkoReactivo.models.Funko;
import org.funkoReactivo.services.database.DataBaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.r2dbc.spi.Connection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class FunkoRepositoryImpl  implements FunkoRepository{

    private static FunkoRepositoryImpl instance;

    private final ConnectionPool connectionFactory;

    private Logger logger = LoggerFactory.getLogger(FunkoRepositoryImpl.class);


    private FunkoRepositoryImpl(DataBaseManager dataBaseManager){
        this.connectionFactory = dataBaseManager.getConnectionPool();
    }

    public static FunkoRepositoryImpl getInstance(DataBaseManager dataBaseManager){
        if(instance == null){
            instance = new FunkoRepositoryImpl(dataBaseManager);
        }
        return instance;
    }

    @Override
    public Mono<Funko> save(Funko funko) throws SQLException, SQLException {
        logger.debug("Guardando funko: {}", funko);
        if(funko == null){
            return Mono.empty();
        }
        String query = "INSERT INTO funkos (cod, myId, name, model, price, release_date) VALUES(?,?,?,?,?,?)";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getCOD())
                        .bind(1, funko.getMyId())
                        .bind(2, funko.getNombre())
                        .bind(3, funko.getModelo().toString())
                        .bind(4, funko.getPrecio())
                        .bind(5, funko.getFecha())
                        .returnGeneratedValues("id")
                        .execute()
                ).flatMap(res -> Mono.from(res.map((row, rowMetadata) ->{
                            funko.setId(row.get("id", Integer.class));
                            return funko;
                        }
                ))).then(Mono.just(funko)),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> update(Funko funko) throws SQLException, SQLException {
        logger.debug("Actualizando funko: {}", funko);
        if(funko == null){
            return Mono.empty();
        }
        String query = "UPDATE funkos SET name = ?, model = ?, price = ?, updated_at = ? WHERE id = ?";

        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, funko.getNombre())
                        .bind(1, funko.getModelo().toString())
                        .bind(2, funko.getPrecio())
                        .bind(3, LocalDateTime.now())
                        .bind(4, funko.getId())
                        .execute()
                ).then(Mono.just(funko)),
                Connection::close
        );
    }

    @Override
    public Mono<Funko> findById(Integer integer) throws SQLException {
        logger.debug("Buscando funko por id: {}", integer);
        String query = "SELECT * FROM funkos WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .bind(0, integer)
                        .execute()
                ).flatMap(result -> Mono.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }

    @Override
    public Flux<Funko> findAll() throws SQLException {
        logger.debug("Buscando todos los funkos");
        String query = "SELECT * FROM funkos";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .execute()
                ).flatMap(result -> Flux.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }

    @Override
    public Mono<Boolean> deleteById(Integer integer) throws SQLException {
        logger.debug("Eliminando funko por id: {}", integer);
        if(this.findById(integer).block() == null){
            return Mono.just(false);
        }
        String query = "DELETE FROM funkos WHERE id = ?";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                                .bind(0, integer)
                                .execute()
                        ).flatMapMany(Result::getRowsUpdated)
                        .hasElements(),
                Connection::close
        );
    }

    @Override
    public Mono<Void> deleteAll() throws SQLException {
        logger.debug("Eliminando todos los funkos");
        String query = "DELETE FROM funkos";
        return Mono.usingWhen(
                connectionFactory.create(),
                connection -> Mono.from(connection.createStatement(query)
                        .execute()
                ).then(),
                Connection::close
        );
    }

    @Override
    public Flux<Funko> findByNombre(String name) throws SQLException {
        logger.debug("Buscando funko por nombre: {}", name);
        String query = "SELECT * FROM funkos WHERE name = ?";
        return Flux.usingWhen(
                connectionFactory.create(),
                connection -> Flux.from(connection.createStatement(query)
                        .bind(0, name)
                        .execute()
                ).flatMap(result -> Flux.from( result.map((row, rowMetadata) -> new Funko(
                        row.get("id", Integer.class),
                        row.get("cod", UUID.class),
                        row.get("myId", Long.class),
                        row.get("name", String.class),
                        Modelo.valueOf(row.get("model", String.class)),
                        row.get("price", Float.class).doubleValue(),
                        row.get("release_date", LocalDate.class),
                        row.get("created_at", LocalDateTime.class),
                        row.get("updated_at", LocalDateTime.class)
                )))),
                Connection::close
        );
    }
}
