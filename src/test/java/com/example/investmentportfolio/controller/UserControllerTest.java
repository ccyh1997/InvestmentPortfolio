package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.UserDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@EnableMethodSecurity
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllUsers_thenReturnUsers() throws Exception {
        Set<String> roles1 = new HashSet<>();
        roles1.add("ADMIN");
        roles1.add("USER");
        UserDto responseUserDto1 = new UserDto("ccyh_97", null, roles1, "Caleb", "Chan", null, "SGD");
        Set<String> roles2 = new HashSet<>();
        roles2.add("USER");
        UserDto responseUserDto2 = new UserDto("bob_da_builderz", null, roles2, null, null, null, null);
        List<UserDto> responseUserDtoList = Arrays.asList(responseUserDto1, responseUserDto2);
        when(userService.getAllUsers()).thenReturn(responseUserDtoList);
        mockMvc.perform(get("/users/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("ccyh_97"))
                .andExpect(jsonPath("$[0].roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$[0].roles[1]").value("USER"))
                .andExpect(jsonPath("$[0].firstName").value("Caleb"))
                .andExpect(jsonPath("$[0].lastName").value("Chan"))
                .andExpect(jsonPath("$[0].imagePath").value(nullValue()))
                .andExpect(jsonPath("$[0].displayCurrency").value("SGD"))
                .andExpect(jsonPath("$[1].username").value("bob_da_builderz"))
                .andExpect(jsonPath("$[1].roles[0]").value("USER"))
                .andExpect(jsonPath("$[1].firstName").value(nullValue()))
                .andExpect(jsonPath("$[1].lastName").value(nullValue()))
                .andExpect(jsonPath("$[1].imagePath").value(nullValue()))
                .andExpect(jsonPath("$[1].displayCurrency").value(nullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllUsers_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/users/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllUsers_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetUserById_thenReturnUser() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        UserDto responseUserDto = new UserDto("ccyh_97", null, roles, "Caleb", "Chan", null, "SGD");
        when(userService.getUserById(any())).thenReturn(responseUserDto);
        mockMvc.perform(get("/users/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("USER"))
                .andExpect(jsonPath("$.firstName").value("Caleb"))
                .andExpect(jsonPath("$.lastName").value("Chan"))
                .andExpect(jsonPath("$.imagePath").value(nullValue()))
                .andExpect(jsonPath("$.displayCurrency").value("SGD"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetUserById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/users/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetUserById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/users/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateUserById_thenReturnOk() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        UserDto requestUserDto = new UserDto("ccyh_97", null, roles, "Caleb", "Chan", null, "SGD");
        UserDto responseUserDto = new UserDto("ccyh_97", null, roles, "Caleb", "Tan", null, "SGD");
        when(userService.updateUserById(any(), any())).thenReturn(responseUserDto);
        mockMvc.perform(post("/users/update/id/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.roles[0]").value("ADMIN"))
                .andExpect(jsonPath("$.roles[1]").value("USER"))
                .andExpect(jsonPath("$.firstName").value("Caleb"))
                .andExpect(jsonPath("$.lastName").value("Tan"))
                .andExpect(jsonPath("$.imagePath").value(nullValue()))
                .andExpect(jsonPath("$.displayCurrency").value("SGD"));
        verify(userService, times(1)).updateUserById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateUserById_thenReturnBadRequest() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        UserDto requestUserDto = new UserDto("", null, roles, "Caleb", "Chan", null, "SGD");
        mockMvc.perform(post("/users/update/id/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Username must be between 7 and 20 characters and contain only letters, numbers, or underscores."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateUserById_thenReturnForbidden() throws Exception {
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("USER");
        UserDto requestUserDto = new UserDto("ccyh_97", null, roles, "Caleb", "Chan", null, "SGD");        mockMvc.perform(post("/users/update/id/{userId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUserDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateUserById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/users/update/id/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllUsers_thenDeleteAllUsers() throws Exception {
        mockMvc.perform(delete("/users/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all users."));
        verify(userService, times(1)).deleteAllUsers();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllUsers_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/users/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllUsers_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/users/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteUserById_thenDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/delete/id/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted user with id: 1"));
        verify(userService, times(1)).deleteUserById(any());
    }

    @Test
    void givenUserRoleWithValidUserId_whenDeleteUserById_thenDeleteUser() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        mockMvc.perform(delete("/users/delete/id/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted user with id: 1"));
        verify(userService, times(1)).deleteUserById(any());
    }

    @Test
    void givenUserRoleWithValidUserId_whenDeleteUserById_thenForbidden() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                2L,
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        mockMvc.perform(delete("/users/delete/id/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteUserById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/users/delete/id/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}