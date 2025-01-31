package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.service.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateLivePricesTaskletTest {
    @Mock
    private StockService stockService;

    @InjectMocks
    private UpdateLivePricesTasklet updateLivePricesTasklet;

    @Test
    void whenExecute_thenUpdateStockPricesAndReturnFinished() throws Exception {
        RepeatStatus status = updateLivePricesTasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));
        verify(stockService, times(1)).updateLiveStockPrices();
        assertEquals(RepeatStatus.FINISHED, status);
    }
}