package com.HCMUS.PHON.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseDTO<T> {
    private int code;
    private String message;
    private T data;

    public ApiResponseDTO() {}

    public ApiResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
