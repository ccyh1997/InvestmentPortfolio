package com.example.investmentportfolio.batch;

import com.example.investmentportfolio.util.GeneralException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static com.example.investmentportfolio.util.Constants.INTERNAL_SERVER_ERROR_ERROR_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchJobSchedulerTest {
    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job job;

    @InjectMocks
    private BatchJobScheduler batchJobScheduler;

    @Test
    void givenNoExceptions_whenRunBatchJob_thenReturnJobExecution() throws JobExecutionException {
        JobExecution jobExecution = mock(JobExecution.class);
        when(jobLauncher.run(job, new JobParameters())).thenReturn(jobExecution);
        batchJobScheduler.runBatchJob();
        verify(jobLauncher).run(job, new JobParameters());
    }

    @Test
    void givenException_whenRunBatchJob_thenThrowGeneralException() throws JobExecutionException {
        String errorMessage = "Job execution failed";
        doAnswer(invocation -> {
            throw new JobExecutionException(errorMessage);
        }).when(jobLauncher).run(any(Job.class), any(JobParameters.class));
        GeneralException exception = assertThrows(GeneralException.class, () -> {
            batchJobScheduler.runBatchJob();
        });
        assertEquals(INTERNAL_SERVER_ERROR_ERROR_CODE, exception.getError().getErrorCode());
        assertEquals(errorMessage, exception.getError().getErrorMessages().getFirst());

    }
}