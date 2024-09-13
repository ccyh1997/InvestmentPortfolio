package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.service.StatisticService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class UpdateStatisticsTasklet implements Tasklet {
    private final StatisticService statisticService;

    public UpdateStatisticsTasklet(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @Override
    public RepeatStatus execute(@Nullable StepContribution contribution, @Nullable ChunkContext chunkContext) {
        statisticService.updateStatisticsForAllUsers();
        return RepeatStatus.FINISHED;
    }
}
