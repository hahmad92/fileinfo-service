package com.hammad.file.service.exception;

import java.util.Date;

public record ErrorMessage(int statusCode, Date timestamp, String message) {

}