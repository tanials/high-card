package it.sara.demo.web.auth.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationResult {

    private String username;
    private String role;


    public AuthenticationResult(String username, String role) {
        this.username = username;
        this.role = role;

    }
}

