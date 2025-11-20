package com.HCMUS.PHON.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDTO {
    private String token;

    public TokenDTO() {}

    public TokenDTO(String token) {
        this.token = token;
    }
}
