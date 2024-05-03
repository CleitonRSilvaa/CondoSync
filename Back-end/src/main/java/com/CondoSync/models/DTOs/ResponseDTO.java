package com.CondoSync.models.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private LocalDateTime timestamp;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private int status;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String error;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String message;

    private Object data;

    public ResponseDTO(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ResponseDTO(int status, String error, String message, Object data) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

}
