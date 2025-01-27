package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.DividendDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.DividendService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DividendController.class)
@EnableMethodSecurity
class DividendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DividendService dividendService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateDividend_thenReturnCreated() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto responseDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendService.createDividend(any(DividendDto.class))).thenReturn(responseDividendDto);
        mockMvc.perform(post("/dividends/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.exDate").value("2023-08-17"))
                .andExpect(jsonPath("$.payDate").value("2023-08-30"))
                .andExpect(jsonPath("$.payout").value("0.0305"));
        verify(dividendService, times(1)).createDividend(any(DividendDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateDividend_thenReturnBadRequest() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "", "2023-08-17", "2023-08-30", "0.0305");
        mockMvc.perform(post("/dividends/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenCreateDividend_thenReturnForbidden() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        mockMvc.perform(post("/dividends/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenCreateDividend_thenReturnUnauthorized() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        mockMvc.perform(post("/dividends/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllDividends_thenReturnDividends() throws Exception {
        DividendDto responseDividendDto1 = new DividendDto("D05", "SGX", "2024-04-05", "2024-04-19", "0.54");
        DividendDto responseDividendDto2 = new DividendDto("D05", "SGX", "2023-11-14", "2023-11-27", "0.48");
        List<DividendDto> responseDividendDtoList = Arrays.asList(responseDividendDto1, responseDividendDto2);
        when(dividendService.getAllDividends()).thenReturn(responseDividendDtoList);
        mockMvc.perform(get("/dividends/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].exDate").value("2024-04-05"))
                .andExpect(jsonPath("$[0].payDate").value("2024-04-19"))
                .andExpect(jsonPath("$[0].payout").value("0.54"))
                .andExpect(jsonPath("$[1].stockTicker").value("D05"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].exDate").value("2023-11-14"))
                .andExpect(jsonPath("$[1].payDate").value("2023-11-27"))
                .andExpect(jsonPath("$[1].payout").value("0.48"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllDividends_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/dividends/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllDividends_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/dividends/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetDividendById_thenReturnDividend() throws Exception {
        DividendDto responseDividendDto = new DividendDto("D05", "SGX", "2024-04-05", "2024-04-19", "0.54");
        when(dividendService.getDividendById(any())).thenReturn(responseDividendDto);
        mockMvc.perform(get("/dividends/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.exDate").value("2024-04-05"))
                .andExpect(jsonPath("$.payDate").value("2024-04-19"))
                .andExpect(jsonPath("$.payout").value("0.54"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetDividendById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/dividends/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetDividendById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/dividends/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetDividendsByStockId_thenReturnDividends() throws Exception {
        DividendDto responseDividendDto1 = new DividendDto("D05", "SGX", "2024-04-05", "2024-04-19", "0.54");
        DividendDto responseDividendDto2 = new DividendDto("D05", "SGX", "2023-11-14", "2023-11-27", "0.48");
        List<DividendDto> responseDividendDtoList = Arrays.asList(responseDividendDto1, responseDividendDto2);
        when(dividendService.getDividendsByStockId(any())).thenReturn(responseDividendDtoList);
        mockMvc.perform(get("/dividends/stockId/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].exDate").value("2024-04-05"))
                .andExpect(jsonPath("$[0].payDate").value("2024-04-19"))
                .andExpect(jsonPath("$[0].payout").value("0.54"))
                .andExpect(jsonPath("$[1].stockTicker").value("D05"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].exDate").value("2023-11-14"))
                .andExpect(jsonPath("$[1].payDate").value("2023-11-27"))
                .andExpect(jsonPath("$[1].payout").value("0.48"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetDividendsByStockId_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/dividends/stockId/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetDividendsByStockId_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/dividends/stockId/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetDividendsByExchangeId_thenReturnDividends() throws Exception {
        DividendDto responseDividendDto1 = new DividendDto("D05", "SGX", "2024-04-05", "2024-04-19", "0.54");
        DividendDto responseDividendDto2 = new DividendDto("D05", "SGX", "2023-11-14", "2023-11-27", "0.48");
        List<DividendDto> responseDividendDtoList = Arrays.asList(responseDividendDto1, responseDividendDto2);
        when(dividendService.getDividendsByExchangeId(any())).thenReturn(responseDividendDtoList);
        mockMvc.perform(get("/dividends/exchangeId/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].exDate").value("2024-04-05"))
                .andExpect(jsonPath("$[0].payDate").value("2024-04-19"))
                .andExpect(jsonPath("$[0].payout").value("0.54"))
                .andExpect(jsonPath("$[1].stockTicker").value("D05"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].exDate").value("2023-11-14"))
                .andExpect(jsonPath("$[1].payDate").value("2023-11-27"))
                .andExpect(jsonPath("$[1].payout").value("0.48"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetDividendsByExchangeId_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/dividends/exchangeId/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetDividendsByExchangeId_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/dividends/exchangeId/{exchangeId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateDividendById_thenReturnOk() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        DividendDto responseDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        when(dividendService.updateDividendById(any(), any())).thenReturn(responseDividendDto);
        mockMvc.perform(post("/dividends/update/id/{dividendId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.exDate").value("2023-08-17"))
                .andExpect(jsonPath("$.payDate").value("2023-08-30"))
                .andExpect(jsonPath("$.payout").value("0.0305"));
        verify(dividendService, times(1)).updateDividendById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateDividendById_thenReturnBadRequest() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "", "2023-08-17", "2023-08-30", "0.0305");
        mockMvc.perform(post("/dividends/update/id/{dividendId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateDividendById_thenReturnForbidden() throws Exception {
        DividendDto requestDividendDto = new DividendDto("D05", "SGX", "2023-08-17", "2023-08-30", "0.0305");
        mockMvc.perform(post("/dividends/update/id/{dividendId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDividendDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateDividendById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/dividends/update/id/{dividendId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllDividends_thenDeleteAllDividends() throws Exception {
        mockMvc.perform(delete("/dividends/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all dividends."));
        verify(dividendService, times(1)).deleteAllDividends();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllDividends_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/dividends/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllDividends_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/dividends/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteDividendById_thenDeleteDividend() throws Exception {
        mockMvc.perform(delete("/dividends/delete/id/{dividendId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted dividend with id: 1"));
        verify(dividendService, times(1)).deleteDividendById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteDividendById_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/dividends/delete/id/{dividendId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteDividendById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/dividends/delete/id/{dividendId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}