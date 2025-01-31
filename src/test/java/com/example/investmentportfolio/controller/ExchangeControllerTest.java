package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.ExchangeDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.ExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExchangeController.class)
@EnableMethodSecurity
class ExchangeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeService exchangeService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateExchange_thenReturnCreated() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeService.createExchange(any())).thenReturn(responseExchangeDto);
        mockMvc.perform(post("/exchanges/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exchange").value("SEHK"))
                .andExpect(jsonPath("$.countryCode").value("HK"))
                .andExpect(jsonPath("$.suffix").value(".HK"));
        verify(exchangeService, times(1)).createExchange(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateExchange_thenReturnBadRequest() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "", ".HK");
        mockMvc.perform(post("/exchanges/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Country code must be exactly 2 characters and contain only letters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenCreateExchange_thenReturnForbidden() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        mockMvc.perform(post("/exchanges/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllExchanges_thenReturnExchanges() throws Exception {
        ExchangeDto responseExchangeDto1 = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto2 = new ExchangeDto("NASDAQ", "US", null);
        List<ExchangeDto> responseExchangeDtoList = Arrays.asList(responseExchangeDto1, responseExchangeDto2);
        when(exchangeService.getAllExchanges()).thenReturn(responseExchangeDtoList);
        mockMvc.perform(get("/exchanges/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]exchange").value("SEHK"))
                .andExpect(jsonPath("$.[0]countryCode").value("HK"))
                .andExpect(jsonPath("$.[0]suffix").value(".HK"))
                .andExpect(jsonPath("$.[1]exchange").value("NASDAQ"))
                .andExpect(jsonPath("$.[1]countryCode").value("US"))
                .andExpect(jsonPath("$.[1]suffix").value(nullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllExchanges_thenReturnExchanges() throws Exception {
        ExchangeDto responseExchangeDto1 = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto2 = new ExchangeDto("SGX", "SG", ".SI");
        List<ExchangeDto> responseExchangeDtoList = Arrays.asList(responseExchangeDto1, responseExchangeDto2);
        when(exchangeService.getAllExchanges()).thenReturn(responseExchangeDtoList);
        mockMvc.perform(get("/exchanges/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]exchange").value("SEHK"))
                .andExpect(jsonPath("$.[0]countryCode").value("HK"))
                .andExpect(jsonPath("$.[0]suffix").value(".HK"))
                .andExpect(jsonPath("$.[1]exchange").value("SGX"))
                .andExpect(jsonPath("$.[1]countryCode").value("SG"))
                .andExpect(jsonPath("$.[1]suffix").value(".SI"));
    }

    @Test
    void givenNoRole_whenGetAllExchanges_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/exchanges/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetExchangeById_thenReturnExchange() throws Exception {
        ExchangeDto responseExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeService.getExchangeById(any())).thenReturn(responseExchangeDto);
        mockMvc.perform(get("/exchanges/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exchange").value("SEHK"))
                .andExpect(jsonPath("$.countryCode").value("HK"))
                .andExpect(jsonPath("$.suffix").value(".HK"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetExchangeById_thenReturnExchange() throws Exception {
        ExchangeDto responseExchangeDto = new ExchangeDto("SGX", "SG", ".SI");
        when(exchangeService.getExchangeById(any())).thenReturn(responseExchangeDto);
        mockMvc.perform(get("/exchanges/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.countryCode").value("SG"))
                .andExpect(jsonPath("$.suffix").value(".SI"));
    }

    @Test
    void givenNoRole_whenGetExchangeById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/exchanges/id/{exchangeId", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetExchangesByCountryCode_thenReturnExchanges() throws Exception {
        ExchangeDto responseExchangeDto1 = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto2 = new ExchangeDto("HKFE", "HK", null);
        List<ExchangeDto> responseExchangeDtoList = Arrays.asList(responseExchangeDto1, responseExchangeDto2);
        when(exchangeService.getExchangesByCountryCode(any())).thenReturn(responseExchangeDtoList);
        mockMvc.perform(get("/exchanges")
                        .param("countryCode", "HK")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]exchange").value("SEHK"))
                .andExpect(jsonPath("$.[0]countryCode").value("HK"))
                .andExpect(jsonPath("$.[0]suffix").value(".HK"))
                .andExpect(jsonPath("$.[1]exchange").value("HKFE"))
                .andExpect(jsonPath("$.[1]countryCode").value("HK"))
                .andExpect(jsonPath("$.[1]suffix").value(nullValue()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetExchangesByCountryCode_thenReturnExchanges() throws Exception {
        ExchangeDto responseExchangeDto1 = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto2 = new ExchangeDto("CGSE", "HK", null);
        List<ExchangeDto> responseExchangeDtoList = Arrays.asList(responseExchangeDto1, responseExchangeDto2);
        when(exchangeService.getExchangesByCountryCode(any())).thenReturn(responseExchangeDtoList);
        mockMvc.perform(get("/exchanges")
                        .param("countryCode", "HK")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]exchange").value("SEHK"))
                .andExpect(jsonPath("$.[0]countryCode").value("HK"))
                .andExpect(jsonPath("$.[0]suffix").value(".HK"))
                .andExpect(jsonPath("$.[1]exchange").value("CGSE"))
                .andExpect(jsonPath("$.[1]countryCode").value("HK"))
                .andExpect(jsonPath("$.[1]suffix").value(nullValue()));
    }

    @Test
    void givenNoRole_whenGetExchangesByCountryCode_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/exchanges/country/{countryCode}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetExchangeBySuffix_thenReturnExchange() throws Exception {
        ExchangeDto responseExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeService.getExchangeBySuffix(any())).thenReturn(responseExchangeDto);
        mockMvc.perform(get("/exchanges/suffix/{suffix}", ".HK")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exchange").value("SEHK"))
                .andExpect(jsonPath("$.countryCode").value("HK"))
                .andExpect(jsonPath("$.suffix").value(".HK"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetExchangeBySuffix_thenReturnExchange() throws Exception {
        ExchangeDto responseExchangeDto = new ExchangeDto("SGX", "SG", ".SI");
        when(exchangeService.getExchangeBySuffix(any())).thenReturn(responseExchangeDto);
        mockMvc.perform(get("/exchanges/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.countryCode").value("SG"))
                .andExpect(jsonPath("$.suffix").value(".SI"));
    }

    @Test
    void givenNoRole_whenGetExchangeBySuffix_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/exchanges/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateExchangeById_thenReturnOk() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeService.updateExchangeById(any(), any())).thenReturn(responseExchangeDto);
        mockMvc.perform(post("/exchanges/update/id/{exchangeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchange").value("SEHK"))
                .andExpect(jsonPath("$.countryCode").value("HK"))
                .andExpect(jsonPath("$.suffix").value(".HK"));
        verify(exchangeService, times(1)).updateExchangeById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateExchangeById_thenReturnBadRequest() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", "");
        mockMvc.perform(post("/exchanges/update/id/{exchangeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Suffix must start with a . followed by up to 4 letters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateExchangeById_thenReturnForbidden() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        mockMvc.perform(post("/exchanges/update/id/{exchangeId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateExchangeById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/exchanges/update/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateExchangeBySuffix_thenReturnOk() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        ExchangeDto responseExchangeDto = new ExchangeDto("SEHK", "HK", ".HK");
        when(exchangeService.updateExchangeBySuffix(any(), any())).thenReturn(responseExchangeDto);
        mockMvc.perform(post("/exchanges/update/suffix/{suffix}", ".HK")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exchange").value("SEHK"))
                .andExpect(jsonPath("$.countryCode").value("HK"))
                .andExpect(jsonPath("$.suffix").value(".HK"));
        verify(exchangeService, times(1)).updateExchangeBySuffix(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateExchangeBySuffix_thenReturnBadRequest() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("", "SG", ".SI");
        mockMvc.perform(post("/exchanges/update/suffix/{suffix}", ".SI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateExchangeBySuffix_thenReturnForbidden() throws Exception {
        ExchangeDto requestExchangeDto = new ExchangeDto("SEHK", "SG", ".SI");
        mockMvc.perform(post("/exchanges/update/suffix/{suffix}", ".SI")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestExchangeDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateExchangeBySuffix_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/exchanges/update/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllExchanges_thenDeleteAllExchanges() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all exchanges."));
        verify(exchangeService, times(1)).deleteAllExchanges();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllExchanges_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllExchanges_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteExchangeById_thenDeleteExchange() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted exchange with id: 1"));
        verify(exchangeService, times(1)).deleteExchangeById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteExchangeById_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteExchangeById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/id/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteExchangeBySuffix_thenDeleteExchange() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted exchange with suffix: .SI"));
        verify(exchangeService, times(1)).deleteExchangeBySuffix(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteExchangeBySuffix_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteExchangeBySuffix_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/exchanges/delete/suffix/{suffix}", ".SI")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}