package com.example.demo.config.filter;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface GoogleUserDetailsService {

    GoogleUserDetails loadGoogleUserByUsername(String username) throws UsernameNotFoundException;
}
