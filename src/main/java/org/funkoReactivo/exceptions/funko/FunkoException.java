package org.funkoReactivo.exceptions.funko;

public abstract class FunkoException extends RuntimeException {
    public FunkoException(String msg){
        super(msg);
    }
}
