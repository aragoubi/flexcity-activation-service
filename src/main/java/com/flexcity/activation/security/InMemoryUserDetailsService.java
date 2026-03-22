package com.flexcity.activation.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InMemoryUserDetailsService implements UserDetailsService {

    // password is BCrypt hash of "password"
    private static final Map<String, User> USERS = Map.of(
        "operator", new User(
            "operator",
            new BCryptPasswordEncoder().encode("password"),
            List.of(new SimpleGrantedAuthority("ROLE_OPERATOR"))
        )
    );

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = USERS.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return user;
    }
}
