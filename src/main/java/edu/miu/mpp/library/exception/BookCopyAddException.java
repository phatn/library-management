package edu.miu.mpp.library.exception;

import java.io.Serializable;

public class BookCopyAddException extends RuntimeException implements Serializable {
    public BookCopyAddException() {
        super();
    }

    public BookCopyAddException(String msg) {
        super(msg);
    }

    public BookCopyAddException(Throwable t) {
        super(t);
    }
}
