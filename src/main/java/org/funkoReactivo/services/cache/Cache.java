package org.funkoReactivo.services.cache;


import org.funkoReactivo.models.Funko;
import reactor.core.publisher.Mono;

public interface Cache<K, V> {

    Mono<Void> put(K key, V value) throws Exception;

    Mono<V> get(K key);

    Mono<V> delete(K key);

    void clear();

    void shutdown();

}
