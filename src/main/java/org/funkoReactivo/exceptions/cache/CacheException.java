package org.funkoReactivo.exceptions.cache;

public abstract class CacheException extends RuntimeException{
    CacheException(String msg){
        super(msg);
    }
}
