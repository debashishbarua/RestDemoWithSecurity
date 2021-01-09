package com.example.demo.security;

import java.io.Serializable;

public class AuthenticationResponse implements Serializable {

    private  String token;
    
    public AuthenticationResponse() {
	}

    public AuthenticationResponse(String token) {
        this.token = token;
    }

   

	public String getToken() {
        return token;
    }
	
}
