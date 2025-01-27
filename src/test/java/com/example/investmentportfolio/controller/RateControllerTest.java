package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.RateDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.RateService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RateController.class)
@EnableMethodSecurity
class RateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RateService rateService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateRate_thenReturnCreated() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        RateDto responseRateDto = new RateDto("SGD/USD", "0.74120251");
        when(rateService.createRate(any())).thenReturn(responseRateDto);
        mockMvc.perform(post("/rates/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rateName").value("SGD/USD"))
                .andExpect(jsonPath("$.rate").value("0.74120251"));
        verify(rateService, times(1)).createRate(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateRate_thenReturnBadRequest() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "");
        mockMvc.perform(post("/rates/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Rate should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenCreateRate_thenReturnForbidden() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        mockMvc.perform(post("/rates/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenCreateRate_thenReturnUnauthorized() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        mockMvc.perform(post("/rates/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllRates_thenReturnRates() throws Exception {
        RateDto responseRateDto1 = new RateDto("SGD/USD", "0.74120251");
        RateDto responseRateDto2 = new RateDto("USD/SGD", "1.3632");
        List<RateDto> responseRateDtoList = Arrays.asList(responseRateDto1, responseRateDto2);
        when(rateService.getAllRates()).thenReturn(responseRateDtoList);
        mockMvc.perform(get("/rates/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]rateName").value("SGD/USD"))
                .andExpect(jsonPath("$.[0]rate").value("0.74120251"))
                .andExpect(jsonPath("$.[1]rateName").value("USD/SGD"))
                .andExpect(jsonPath("$.[1]rate").value("1.3632"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllRates_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/rates/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllRates_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/rates/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetRateById_thenReturnRate() throws Exception {
        RateDto responseRateDto = new RateDto("SGD/USD", "0.74120251");
        when(rateService.getRateById(any())).thenReturn(responseRateDto);
        mockMvc.perform(get("/rates/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.rateName").value("SGD/USD"))
                .andExpect(jsonPath("$.rate").value("0.74120251"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetRateById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/rates/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetRateById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/rates/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateRateById_thenReturnOk() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        RateDto responseRateDto = new RateDto("SGD/USD", "0.74120251");
        when(rateService.updateRateById(any(), any())).thenReturn(responseRateDto);
        mockMvc.perform(post("/rates/update/id/{rateId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rateName").value("SGD/USD"))
                .andExpect(jsonPath("$.rate").value("0.74120251"));
        verify(rateService, times(1)).updateRateById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateRateById_thenReturnBadRequest() throws Exception {
        RateDto requestRateDto = new RateDto("", "0.74120251");
        mockMvc.perform(post("/rates/update/id/{rateId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Rate name should contain exactly 3 letters followed by a slash followed by another 3 letters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateRateById_thenReturnForbidden() throws Exception {
        RateDto requestRateDto = new RateDto("SGD/USD", "0.74120251");
        mockMvc.perform(post("/rates/update/id/{rateId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestRateDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateRateById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/rates/update/id/{rateId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllRates_thenDeleteAllRates() throws Exception {
        mockMvc.perform(delete("/rates/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all rates."));
        verify(rateService, times(1)).deleteAllRates();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllRates_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/rates/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllDividends_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/rates/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteDividendById_thenDeleteDividend() throws Exception {
        mockMvc.perform(delete("/rates/delete/id/{rateId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted rate with id: 1"));
        verify(rateService, times(1)).deleteRateById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteDividendById_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/rates/delete/id/{rateId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteDividendById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/rates/delete/id/{rateId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}