package com.hammad.file.service.exception;

import java.io.Serial;

public class ResourceAccessDeniedException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ResourceAccessDeniedException(String msg) {
        super(msg);
    }
}
