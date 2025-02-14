package com.flawlesscoders.ambigu.utils.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.flawlesscoders.ambigu.modules.user.base.User;
import com.flawlesscoders.ambigu.modules.user.base.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Implementación de UserDetailsService para la autenticación con Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())  
                .authorities(user.getRole().name()) 
                .build();
    }
}

