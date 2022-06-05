package edu.miu.mpp.library.exception;

import java.io.Serializable;

public class BookAddException extends RuntimeException implements Serializable {
    public BookAddException() {
        super();
    }

    public BookAddException(String msg) {
        super(msg);
    }

    public BookAddException(Throwable t) {
        super(t);
    }
}
