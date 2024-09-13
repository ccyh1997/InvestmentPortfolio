package com.example.investmentportfolio.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class LiveStockPriceBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public LiveStockPriceBatchConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Step updateLivePricesStep(UpdateLivePricesTasklet updateLivePricesTasklet) {
        return new StepBuilder("updateLivePricesStep", jobRepository)
                .tasklet(updateLivePricesTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step updateStatisticsStep(UpdateStatisticsTasklet updateStatisticsTasklet) {
        return new StepBuilder("updateStatisticsStep", jobRepository)
                .tasklet(updateStatisticsTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step updateTotalProfitsStep(UpdateTotalProfitsTasklet updateTotalProfitsTasklet) {
        return new StepBuilder("updateTotalProfitsStep", jobRepository)
                .tasklet(updateTotalProfitsTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job job(Step updateLivePricesStep, Step updateStatisticsStep, Step updateTotalProfitsStep) {
        return new JobBuilder("updateJob", jobRepository)
                .start(updateLivePricesStep)
                .next(updateStatisticsStep)
                .next(updateTotalProfitsStep)
                .build();
    }
}
