package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.StockDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.StockService;
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

@WebMvcTest(StockController.class)
@EnableMethodSecurity
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockService stockService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateStock_thenReturnCreated() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        StockDto responseStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        when(stockService.createStock(any(StockDto.class))).thenReturn(responseStockDto);
        mockMvc.perform(post("/stocks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.stockTicker").value("OV8"))
                .andExpect(jsonPath("$.stockName").value("Sheng Siong Group Ltd"))
                .andExpect(jsonPath("$.stockType").value("Equity"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.lastPrice").value("1.5306078"))
                .andExpect(jsonPath("$.baseCurrency").value("SGD"))
                .andExpect(jsonPath("$.divInd").value("Y"))
                .andExpect(jsonPath("$.delistInd").value("N"));
        verify(stockService, times(1)).createStock(any(StockDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateStock_thenReturnBadRequest() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Last price should contain at most one decimal point and 10 numbers before and after the decimal point, and no white spaces or special characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenCreateStock_thenReturnForbidden() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenCreateStock_thenReturnUnauthorized() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllStocks_thenReturnStocks() throws Exception {
        StockDto responseStockDto1 = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        StockDto responseStockDto2 = new StockDto("CSPX", "IShares Core S&P 500 ETF", "ETF", "LSE", "638.00", "USD", "N", "N");
        List<StockDto> responseStockDtoList = Arrays.asList(responseStockDto1, responseStockDto2);
        when(stockService.getAllStocks()).thenReturn(responseStockDtoList);
        mockMvc.perform(get("/stocks/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].stockName").value("DBS Group Holdings Ltd"))
                .andExpect(jsonPath("$[0].stockType").value("Equity"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].lastPrice").value("43.62"))
                .andExpect(jsonPath("$[0].baseCurrency").value("SGD"))
                .andExpect(jsonPath("$[0].divInd").value("Y"))
                .andExpect(jsonPath("$[0].delistInd").value("N"))
                .andExpect(jsonPath("$[1].stockTicker").value("CSPX"))
                .andExpect(jsonPath("$[1].stockName").value("IShares Core S&P 500 ETF"))
                .andExpect(jsonPath("$[1].stockType").value("ETF"))
                .andExpect(jsonPath("$[1].exchange").value("LSE"))
                .andExpect(jsonPath("$[1].lastPrice").value("638.00"))
                .andExpect(jsonPath("$[1].baseCurrency").value("USD"))
                .andExpect(jsonPath("$[1].divInd").value("N"))
                .andExpect(jsonPath("$[1].delistInd").value("N"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllStocks_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/stocks/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllStocks_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/stocks/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetStockById_thenReturnStock() throws Exception {
        StockDto responseStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        when(stockService.getStockById(any())).thenReturn(responseStockDto);
        mockMvc.perform(get("/stocks/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.stockName").value("DBS Group Holdings Ltd"))
                .andExpect(jsonPath("$.stockType").value("Equity"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.lastPrice").value("43.62"))
                .andExpect(jsonPath("$.baseCurrency").value("SGD"))
                .andExpect(jsonPath("$.divInd").value("Y"))
                .andExpect(jsonPath("$.delistInd").value("N"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetStockById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/stocks/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetStockById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/stocks/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetStockByTicker_thenReturnStock() throws Exception {
        StockDto responseStockDto = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        when(stockService.getStockByTicker(any())).thenReturn(responseStockDto);
        mockMvc.perform(get("/stocks/ticker/{stockTicker}", "D05")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.stockName").value("DBS Group Holdings Ltd"))
                .andExpect(jsonPath("$.stockType").value("Equity"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.lastPrice").value("43.62"))
                .andExpect(jsonPath("$.baseCurrency").value("SGD"))
                .andExpect(jsonPath("$.divInd").value("Y"))
                .andExpect(jsonPath("$.delistInd").value("N"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetStockByTicker_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/stocks/ticker/{stockTicker}", "D05")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetStockByTicker_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/stocks/ticker/{stockTicker}", "D05")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetStockByFilters_thenReturnStocks() throws Exception {
        StockDto responseStockDto1 = new StockDto("D05", "DBS Group Holdings Ltd", "Equity", "SGX", "43.62", "SGD", "Y", "N");
        StockDto responseStockDto2 = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.63", "SGD", "Y", "N");
        List<StockDto> responseStockDtoList = Arrays.asList(responseStockDto1, responseStockDto2);
        when(stockService.getStocksByFilters(any(), any(), any(), any())).thenReturn(responseStockDtoList);
        mockMvc.perform(get("/stocks")
                        .param("exchangeId", Long.toString(1L))
                        .param("stockType", "equity")
                        .param("divInd", "Y")
                        .param("delistInd", "N")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].stockName").value("DBS Group Holdings Ltd"))
                .andExpect(jsonPath("$[0].stockType").value("Equity"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].lastPrice").value("43.62"))
                .andExpect(jsonPath("$[0].baseCurrency").value("SGD"))
                .andExpect(jsonPath("$[0].divInd").value("Y"))
                .andExpect(jsonPath("$[0].delistInd").value("N"))
                .andExpect(jsonPath("$[1].stockTicker").value("OV8"))
                .andExpect(jsonPath("$[1].stockName").value("Sheng Siong Group Ltd"))
                .andExpect(jsonPath("$[1].stockType").value("Equity"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].lastPrice").value("1.63"))
                .andExpect(jsonPath("$[1].baseCurrency").value("SGD"))
                .andExpect(jsonPath("$[1].divInd").value("Y"))
                .andExpect(jsonPath("$[1].delistInd").value("N"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetStockByFilters_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/stocks")
                        .param("exchangeId", Long.toString(1L))
                        .param("stockType", "equity")
                        .param("divInd", "Y")
                        .param("delistInd", "N")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetStockByFilters_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/stocks")
                        .param("exchangeId", Long.toString(1L))
                        .param("stockType", "equity")
                        .param("divInd", "Y")
                        .param("delistInd", "N")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateStockById_thenReturnOk() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        StockDto responseStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        when(stockService.updateStockById(any(), any())).thenReturn(responseStockDto);
        mockMvc.perform(post("/stocks/update/id/{stockId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockTicker").value("OV8"))
                .andExpect(jsonPath("$.stockName").value("Sheng Siong Group Ltd"))
                .andExpect(jsonPath("$.stockType").value("Equity"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.lastPrice").value("1.5306078"))
                .andExpect(jsonPath("$.baseCurrency").value("SGD"))
                .andExpect(jsonPath("$.divInd").value("Y"))
                .andExpect(jsonPath("$.delistInd").value("N"));
        verify(stockService, times(1)).updateStockById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateStockById_thenReturnBadRequest() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/update/id/{stockId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateStockById_thenReturnForbidden() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/update/id/{stockId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateStockById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/stocks/update/id/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateStockByTicker_thenReturnOk() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        StockDto responseStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        when(stockService.updateStockByTicker(any(), any())).thenReturn(responseStockDto);
        mockMvc.perform(post("/stocks/update/ticker/{stockTicker}", "OV8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockTicker").value("OV8"))
                .andExpect(jsonPath("$.stockName").value("Sheng Siong Group Ltd"))
                .andExpect(jsonPath("$.stockType").value("Equity"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.lastPrice").value("1.5306078"))
                .andExpect(jsonPath("$.baseCurrency").value("SGD"))
                .andExpect(jsonPath("$.divInd").value("Y"))
                .andExpect(jsonPath("$.delistInd").value("N"));
        verify(stockService, times(1)).updateStockByTicker(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateStockByTicker_thenReturnBadRequest() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/update/ticker/{stockTicker}", "OV8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateStockByTicker_thenReturnForbidden() throws Exception {
        StockDto requestStockDto = new StockDto("OV8", "Sheng Siong Group Ltd", "Equity", "SGX", "1.5306078", "SGD", "Y", "N");
        mockMvc.perform(post("/stocks/update/ticker/{stockTicker}", "OV8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestStockDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateStockByTicker_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/stocks/update/ticker/{stockTicker}", "OV8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllStocks_thenDeleteAllStocks() throws Exception {
        mockMvc.perform(delete("/stocks/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all stocks"));
        verify(stockService, times(1)).deleteAllStocks();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllStocks_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/stocks/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllStocks_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/stocks/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteStockById_thenDeleteStock() throws Exception {
        mockMvc.perform(delete("/stocks/delete/id/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted stock with id: 1"));
        verify(stockService, times(1)).deleteStockById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteStockById_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/stocks/delete/id/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteStockById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/stocks/delete/id/{stockId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteStockByTicker_thenDeleteStock() throws Exception {
        mockMvc.perform(delete("/stocks/delete/ticker/{stockTicker}", "OV8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted stock with ticker: OV8"));
        verify(stockService, times(1)).deleteStockByTicker(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteStockByTicker_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/stocks/delete/ticker/{stockTicker}", "OV8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteStockByTicker_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/stocks/delete/ticker/{stockTicker}", "OV8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}