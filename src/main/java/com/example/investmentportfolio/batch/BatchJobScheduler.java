package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.util.CustomError;
import com.example.investmentportfolio.util.ErrorConstants;
import com.example.investmentportfolio.util.GeneralException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableScheduling
public class BatchJobScheduler {
    private static final Logger LOGGER = LogManager.getLogger(BatchJobScheduler.class);
    private final JobLauncher jobLauncher;
    private final Job updateJob;

    public BatchJobScheduler(JobLauncher jobLauncher, Job updateJob) {
        this.jobLauncher = jobLauncher;
        this.updateJob = updateJob;
    }

    @Scheduled(cron = "30 14 4 * * *") // Executes at 12:00 AM every day
    public void runBatchJob() {
        try {
            jobLauncher.run(updateJob, new JobParameters());
        } catch (JobExecutionException e) {
            List<String> errorMessages = Collections.singletonList(e.getMessage());
            LOGGER.error(errorMessages);
            throw new GeneralException(new CustomError(ErrorConstants.INTERNAL_SERVER_ERROR_ERROR_CODE, errorMessages));
        }
    }
}