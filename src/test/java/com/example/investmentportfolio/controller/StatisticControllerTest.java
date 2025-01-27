package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StatisticDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.StatisticService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticController.class)
@EnableMethodSecurity
class StatisticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StatisticService statisticService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateStatistic_thenReturnCreated() throws Exception {
        StatisticDto requestStatisticDto = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        StatisticDto responseStatisticDto = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        when(statisticService.createStatistic(any())).thenReturn(responseStatisticDto);
        mockMvc.perform(post("/statistics/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStatisticDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("bob_da_builderz"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.totalUnits").value("546.88"))
                .andExpect(jsonPath("$.totalCost").value("103002.59210"))
                .andExpect(jsonPath("$.totalValue").value("240112.90900"))
                .andExpect(jsonPath("$.realizedProfits").value("30000.45010"))
                .andExpect(jsonPath("$.unrealizedProfits").value("70021.22040"))
                .andExpect(jsonPath("$.dividendsEarned").value("20100.110"))
                .andExpect(jsonPath("$.totalProfits").value("140001.8302"));
        verify(statisticService, times(1)).createStatistic(any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateStatistic_thenReturnBadRequest() throws Exception {
        StatisticDto requestStatisticDto = new StatisticDto("bob_da_builderz", "D05", "SGX", "", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        mockMvc.perform(post("/statistics/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStatisticDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Total units should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenCreateStatistic_thenReturnForbidden() throws Exception {
        StatisticDto requestStatisticDto = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        mockMvc.perform(post("/statistics/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStatisticDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllStatistics_thenReturnStatistics() throws Exception {
        StatisticDto responseStatisticDto1 = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        StatisticDto responseStatisticDto2 = new StatisticDto("ccyh_97", "CSPX", "LSE", "42.3585", "28671.68134377", "36790.443321408", "0", "8118.7619776379999909055", "0", "8118.7619776379999909055");
        List<StatisticDto> responseStatisticDtoList = Arrays.asList(responseStatisticDto1, responseStatisticDto2);
        when(statisticService.getAllStatistics()).thenReturn(responseStatisticDtoList);
        mockMvc.perform(get("/statistics/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]username").value("bob_da_builderz"))
                .andExpect(jsonPath("$.[0]stockTicker").value("D05"))
                .andExpect(jsonPath("$.[0]exchange").value("SGX"))
                .andExpect(jsonPath("$.[0]totalUnits").value("546.88"))
                .andExpect(jsonPath("$.[0]totalCost").value("103002.59210"))
                .andExpect(jsonPath("$.[0]totalValue").value("240112.90900"))
                .andExpect(jsonPath("$.[0]realizedProfits").value("30000.45010"))
                .andExpect(jsonPath("$.[0]unrealizedProfits").value("70021.22040"))
                .andExpect(jsonPath("$.[0]dividendsEarned").value("20100.110"))
                .andExpect(jsonPath("$.[0]totalProfits").value("140001.8302"))
                .andExpect(jsonPath("$.[1]username").value("ccyh_97"))
                .andExpect(jsonPath("$.[1]stockTicker").value("CSPX"))
                .andExpect(jsonPath("$.[1]exchange").value("LSE"))
                .andExpect(jsonPath("$.[1]totalUnits").value("42.3585"))
                .andExpect(jsonPath("$.[1]totalCost").value("28671.68134377"))
                .andExpect(jsonPath("$.[1]totalValue").value("36790.443321408"))
                .andExpect(jsonPath("$.[1]realizedProfits").value("0"))
                .andExpect(jsonPath("$.[1]unrealizedProfits").value("8118.7619776379999909055"))
                .andExpect(jsonPath("$.[1]dividendsEarned").value("0"))
                .andExpect(jsonPath("$.[1]totalProfits").value("8118.7619776379999909055"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllStatistics_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/statistics/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllStatistics_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/statistics/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetStatisticById_thenReturnStatistic() throws Exception {
        StatisticDto responseStatisticDto = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        when(statisticService.getStatisticById(any())).thenReturn(responseStatisticDto);
        mockMvc.perform(get("/statistics/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("bob_da_builderz"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.totalUnits").value("546.88"))
                .andExpect(jsonPath("$.totalCost").value("103002.59210"))
                .andExpect(jsonPath("$.totalValue").value("240112.90900"))
                .andExpect(jsonPath("$.realizedProfits").value("30000.45010"))
                .andExpect(jsonPath("$.unrealizedProfits").value("70021.22040"))
                .andExpect(jsonPath("$.dividendsEarned").value("20100.110"))
                .andExpect(jsonPath("$.totalProfits").value("140001.8302"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetStatisticById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/statistics/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetStatisticById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/statistics/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetStatisticsByStockId_thenReturnStatistics() throws Exception {
        StatisticDto responseStatisticDto1 = new StatisticDto("bob_da_builderz", "D05", "SGX", "546.88", "103002.59210", "240112.90900", "30000.45010", "70021.22040", "20100.110", "140001.8302");
        StatisticDto responseStatisticDto2 = new StatisticDto("bob_da_builderz", "CSPX", "LSE", "42.3585", "28671.68134377", "36790.443321408", "0", "8118.7619776379999909055", "0", "8118.7619776379999909055");
        List<StatisticDto> responseStatisticDtoList = Arrays.asList(responseStatisticDto1, responseStatisticDto2);
        when(statisticService.getStatisticsByUserId(any())).thenReturn(responseStatisticDtoList);
        mockMvc.perform(get("/statistics/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0]username").value("bob_da_builderz"))
                .andExpect(jsonPath("$.[0]stockTicker").value("D05"))
                .andExpect(jsonPath("$.[0]exchange").value("SGX"))
                .andExpect(jsonPath("$.[0]totalUnits").value("546.88"))
                .andExpect(jsonPath("$.[0]totalCost").value("103002.59210"))
                .andExpect(jsonPath("$.[0]totalValue").value("240112.90900"))
                .andExpect(jsonPath("$.[0]realizedProfits").value("30000.45010"))
                .andExpect(jsonPath("$.[0]unrealizedProfits").value("70021.22040"))
                .andExpect(jsonPath("$.[0]dividendsEarned").value("20100.110"))
                .andExpect(jsonPath("$.[0]totalProfits").value("140001.8302"))
                .andExpect(jsonPath("$.[1]username").value("bob_da_builderz"))
                .andExpect(jsonPath("$.[1]stockTicker").value("CSPX"))
                .andExpect(jsonPath("$.[1]exchange").value("LSE"))
                .andExpect(jsonPath("$.[1]totalUnits").value("42.3585"))
                .andExpect(jsonPath("$.[1]totalCost").value("28671.68134377"))
                .andExpect(jsonPath("$.[1]totalValue").value("36790.443321408"))
                .andExpect(jsonPath("$.[1]realizedProfits").value("0"))
                .andExpect(jsonPath("$.[1]unrealizedProfits").value("8118.7619776379999909055"))
                .andExpect(jsonPath("$.[1]dividendsEarned").value("0"))
                .andExpect(jsonPath("$.[1]totalProfits").value("8118.7619776379999909055"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetStatisticsByStockId_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/statistics/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetStatisticsByStockId_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/statistics/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}