package com.dataquadinc.exceptions;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private String message;
    private LocalDateTime timestamp;


}