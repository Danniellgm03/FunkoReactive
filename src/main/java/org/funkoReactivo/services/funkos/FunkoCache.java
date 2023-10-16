package org.funkoReactivo.services.funkos;


import org.funkoReactivo.models.Funko;
import org.funkoReactivo.services.cache.Cache;

/**
 * Interfaz para el cache de Funkos
 */
public interface FunkoCache extends Cache<Integer, Funko> {
}
