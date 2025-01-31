package com.example.investmentportfolio.controller;

import com.example.investmentportfolio.dto.TransactionDto;
import com.example.investmentportfolio.security.JwtTokenProvider;
import com.example.investmentportfolio.service.TransactionService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@EnableMethodSecurity
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenCreateTransaction_thenReturnCreated() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Buy", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        TransactionDto responseTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Buy", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(responseTransactionDto);
        mockMvc.perform(post("/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.transactionDate").value("2024-01-01"))
                .andExpect(jsonPath("$.transactionType").value("Buy"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.units").value("100"))
                .andExpect(jsonPath("$.unitPrice").value("33.909"))
                .andExpect(jsonPath("$.fees").value("1.45"))
                .andExpect(jsonPath("$.currency").value("SGD"));
        verify(transactionService, times(1)).createTransaction(any(TransactionDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenValidRequestAndUserRole_whenCreateTransaction_thenReturnCreated() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        TransactionDto responseTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        when(transactionService.createTransaction(any(TransactionDto.class))).thenReturn(responseTransactionDto);
        mockMvc.perform(post("/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.transactionDate").value("2024-01-01"))
                .andExpect(jsonPath("$.transactionType").value("Sell"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.units").value("100"))
                .andExpect(jsonPath("$.unitPrice").value("33.909"))
                .andExpect(jsonPath("$.fees").value("1.45"))
                .andExpect(jsonPath("$.currency").value("SGD"));
        verify(transactionService, times(1)).createTransaction(any(TransactionDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenCreateTransaction_thenReturnBadRequest() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        mockMvc.perform(post("/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Transaction type must be either 'Buy' or 'Sell'."));
    }

    @Test
    void givenNoRole_whenCreateTransaction_thenReturnUnauthorized() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Buy", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        mockMvc.perform(post("/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetAllTransactions_thenReturnTransactions() throws Exception {
        TransactionDto responseTransactionDto1 = new TransactionDto("ccyh_97", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "0.", "SGD");
        TransactionDto responseTransactionDto2 = new TransactionDto("ccyh_97", "2023-05-12", "Buy", "D05", "SGX", "18.53791", "30.7478", "0.", "SGD");
        List<TransactionDto> responseTransactionDtoList = Arrays.asList(responseTransactionDto1, responseTransactionDto2);
        when(transactionService.getAllTransactions()).thenReturn(responseTransactionDtoList);
        mockMvc.perform(get("/transactions/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("ccyh_97"))
                .andExpect(jsonPath("$[0].transactionDate").value("2023-04-14"))
                .andExpect(jsonPath("$[0].transactionType").value("Buy"))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].units").value("17.39087"))
                .andExpect(jsonPath("$[0].unitPrice").value("32.7758"))
                .andExpect(jsonPath("$[0].fees").value("0."))
                .andExpect(jsonPath("$[0].currency").value("SGD"))
                .andExpect(jsonPath("$[1].username").value("ccyh_97"))
                .andExpect(jsonPath("$[1].transactionDate").value("2023-05-12"))
                .andExpect(jsonPath("$[1].transactionType").value("Buy"))
                .andExpect(jsonPath("$[1].stockTicker").value("D05"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].units").value("18.53791"))
                .andExpect(jsonPath("$[1].unitPrice").value("30.7478"))
                .andExpect(jsonPath("$[1].fees").value("0."))
                .andExpect(jsonPath("$[1].currency").value("SGD"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetAllTransactions_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/transactions/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetAllTransactions_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/transactions/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenGetTransactionById_thenReturnTransaction() throws Exception {
        TransactionDto responseTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Buy", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        when(transactionService.getTransactionById(any())).thenReturn(responseTransactionDto);
        mockMvc.perform(get("/transactions/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.transactionDate").value("2024-01-01"))
                .andExpect(jsonPath("$.transactionType").value("Buy"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.units").value("100"))
                .andExpect(jsonPath("$.unitPrice").value("33.909"))
                .andExpect(jsonPath("$.fees").value("1.45"))
                .andExpect(jsonPath("$.currency").value("SGD"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenGetTransactionById_thenReturnForbidden() throws Exception {
        mockMvc.perform(get("/transactions/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetTransactionById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/transactions/id/{id}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenUserRoleWithValidUserId_whenGetTransactionsByUserId_thenReturnTransactions() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                1L,
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        TransactionDto responseTransactionDto1 = new TransactionDto("ccyh_97", "2023-04-14", "Buy", "D05", "SGX", "17.39087", "32.7758", "0.", "SGD");
        TransactionDto responseTransactionDto2 = new TransactionDto("ccyh_97", "2023-05-12", "Buy", "D05", "SGX", "18.53791", "30.7478", "0.", "SGD");
        List<TransactionDto> responseTransactionDtoList = Arrays.asList(responseTransactionDto1, responseTransactionDto2);
        when(transactionService.getTransactionsByUserId(any())).thenReturn(responseTransactionDtoList);
        mockMvc.perform(get("/transactions/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].username").value("ccyh_97"))
                .andExpect(jsonPath("$[0].transactionDate").value("2023-04-14"))
                .andExpect(jsonPath("$[0].transactionType").value("Buy"))
                .andExpect(jsonPath("$[0].stockTicker").value("D05"))
                .andExpect(jsonPath("$[0].exchange").value("SGX"))
                .andExpect(jsonPath("$[0].units").value("17.39087"))
                .andExpect(jsonPath("$[0].unitPrice").value("32.7758"))
                .andExpect(jsonPath("$[0].fees").value("0."))
                .andExpect(jsonPath("$[0].currency").value("SGD"))
                .andExpect(jsonPath("$[1].username").value("ccyh_97"))
                .andExpect(jsonPath("$[1].transactionDate").value("2023-05-12"))
                .andExpect(jsonPath("$[1].transactionType").value("Buy"))
                .andExpect(jsonPath("$[1].stockTicker").value("D05"))
                .andExpect(jsonPath("$[1].exchange").value("SGX"))
                .andExpect(jsonPath("$[1].units").value("18.53791"))
                .andExpect(jsonPath("$[1].unitPrice").value("30.7478"))
                .andExpect(jsonPath("$[1].fees").value("0."))
                .andExpect(jsonPath("$[1].currency").value("SGD"));
    }

    @Test
    void givenUserRoleWithInvalidUserId_whenGetTransactionsByUserId_thenReturnForbidden() throws Exception {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                2L,
                null,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
        mockMvc.perform(get("/transactions/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenGetTransactionsByUserId_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/transactions/userId/{userId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenValidRequestAndAdminRole_whenUpdateTransactionById_thenReturnOk() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        TransactionDto responseTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "SGX", "100", "33.909", "1.47", "SGD");
        when(transactionService.updateTransactionById(any(), any())).thenReturn(responseTransactionDto);
        mockMvc.perform(post("/transactions/update/id/{transactionId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("ccyh_97"))
                .andExpect(jsonPath("$.transactionDate").value("2024-01-01"))
                .andExpect(jsonPath("$.transactionType").value("Sell"))
                .andExpect(jsonPath("$.stockTicker").value("D05"))
                .andExpect(jsonPath("$.exchange").value("SGX"))
                .andExpect(jsonPath("$.units").value("100"))
                .andExpect(jsonPath("$.unitPrice").value("33.909"))
                .andExpect(jsonPath("$.fees").value("1.47"))
                .andExpect(jsonPath("$.currency").value("SGD"));
        verify(transactionService, times(1)).updateTransactionById(any(), any());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenInvalidRequestAndAdminRole_whenUpdateTransactionById_thenReturnBadRequest() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "", "100", "33.909", "1.45", "SGD");
        mockMvc.perform(post("/transactions/update/id/{transactionId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("400 Bad Request"))
                .andExpect(jsonPath("$.errorMessages").value("Exchange name must only contain letters and be up to 10 characters."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenUpdateTransactionById_thenReturnForbidden() throws Exception {
        TransactionDto requestTransactionDto = new TransactionDto("ccyh_97", "2024-01-01", "Sell", "D05", "SGX", "100", "33.909", "1.45", "SGD");
        mockMvc.perform(post("/transactions/update/id/{transactionId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestTransactionDto))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenUpdateTransactionById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(post("/transactions/update/id/{transactionId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteAllTransactions_thenDeleteAllTransactions() throws Exception {
        mockMvc.perform(delete("/transactions/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted all transactions."));
        verify(transactionService, times(1)).deleteAllTransactions();
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteAllTransactions_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/transactions/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteAllTransactions_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/transactions/delete/all")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void givenAdminRole_whenDeleteTransactionById_thenDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/transactions/delete/id/{transactionId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully deleted transaction with id: 1"));
        verify(transactionService, times(1)).deleteTransactionById(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void givenUserRole_whenDeleteTransactionById_thenReturnForbidden() throws Exception {
        mockMvc.perform(delete("/transactions/delete/id/{transactionId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("403 Forbidden"))
                .andExpect(jsonPath("$.errorMessages").value("You do not have permission to access this resource."));
    }

    @Test
    void givenNoRole_whenDeleteTransactionById_thenReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/transactions/delete/id/{transactionId}", 1)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}