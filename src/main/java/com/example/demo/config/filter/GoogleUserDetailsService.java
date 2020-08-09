package com.example.demo.config.filter;

public interface GoogleUserDetailsService {

    GoogleUserDetails loadGoogleUserByUsername(String username);
}
