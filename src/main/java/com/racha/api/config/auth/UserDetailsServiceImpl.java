package com.racha.api.config.auth;

import com.racha.api.domain.repository.UserRepository;
import com.racha.api.domain.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Optional<User> user = userRepository.findByEmail(email);

            return buildUserDetails(user.orElse(null));
        } catch (Exception e) {
            throw new UsernameNotFoundException(
                    "Usuário não encontrado com email: " + email
            );
        }
    }

    public UserDetails loadUserByUserId(UUID userId) throws UsernameNotFoundException {
        try {
            Optional<User> user = userRepository.findById(userId);

            return buildUserDetails(user.orElse(null));

        } catch (Exception e) {
            throw new UsernameNotFoundException(
                    "Usuário não encontrado com ID: " + userId
            );
        }
    }

    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.emptyList())
                .build();
    }
}
