package edu.miu.mpp.library.exception;

import java.io.Serializable;

public class BookCheckoutException extends RuntimeException implements Serializable {

    public BookCheckoutException() {
        super();
    }

    public BookCheckoutException(String msg) {
        super(msg);
    }

    public BookCheckoutException(Throwable t) {
        super(t);
    }
}
