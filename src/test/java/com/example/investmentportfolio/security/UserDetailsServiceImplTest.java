package com.example.investmentportfolio.security;

import com.example.investmentportfolio.model.User;
import com.example.investmentportfolio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Test
    void givenValidUsername_whenLoadUserByUsername_thenLoadUser() {
        String username = "testUser";
        String password = "testPassword";
        Set<String> roles = Set.of("ROLE_USER");
        Set<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(Set.of("USER"));
        when(userRepository.findByUsernameIgnoreCase(any())).thenReturn(Optional.of(user));
        UserDetails userResponse = userDetailsService.loadUserByUsername(username);
        assertEquals(username, userResponse.getUsername());
        assertEquals(password, userResponse.getPassword());
        assertEquals(authorities, userResponse.getAuthorities());
        verify(userRepository, times(1)).findByUsernameIgnoreCase((any()));
    }

    @Test
    void givenInvalidUsername_whenLoadUserByUsername_thenThrowUsernameNotFoundException() {
        String username = "testUser".toUpperCase();
        when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
        verify(userRepository, times(1)).findByUsernameIgnoreCase((any()));
    }
}