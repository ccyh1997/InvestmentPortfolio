package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.service.StatisticService;
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
class UpdateStatisticsTaskletTest {
    @Mock
    private StatisticService statisticService;

    @InjectMocks
    private UpdateStatisticsTasklet updateStatisticsTasklet;

    @Test
    void whenExecute_thenUpdateStockPricesAndReturnFinished() throws Exception {
        RepeatStatus status = updateStatisticsTasklet.execute(mock(StepContribution.class), mock(ChunkContext.class));
        verify(statisticService, times(1)).updateStatisticsForAllUsers();
        assertEquals(RepeatStatus.FINISHED, status);
    }
}
