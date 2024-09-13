package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.service.StockService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class UpdateLivePricesTasklet implements Tasklet {
    private final StockService stockService;

    public UpdateLivePricesTasklet(StockService stockService) {
        this.stockService = stockService;
    }

    @Override
    public RepeatStatus execute(@Nullable StepContribution contribution, @Nullable ChunkContext chunkContext) throws Exception {
        stockService.updateLiveStockPrices();
        return RepeatStatus.FINISHED;
    }
}
