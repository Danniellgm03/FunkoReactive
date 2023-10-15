package org.funkoReactivo.services.storage;

import org.funkoReactivo.models.Funko;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface StorageService<T> {

    Mono<Boolean> exportToJsonAsync(List<T> data) throws ExecutionException, InterruptedException;

    Flux<Funko> importFromCsvAsync(String filePath) throws ExecutionException, InterruptedException;


}
