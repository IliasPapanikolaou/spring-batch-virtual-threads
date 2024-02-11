package com.ipap.springbatchvirtualthreads.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobTrigger {

    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0/30 * * ? * *") // Every 30 seconds
    public void launchJobPeriodically() {
        try {
            log.info("================ Launching Job ================");
            var jobParameters = new JobParametersBuilder().addDate("uniqueness", new Date());
            JobExecution jobExecution = this.jobLauncher.run(job, jobParameters.toJobParameters());
            log.info("Job finished with status: {}", jobExecution.getExitStatus());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            log.error(e.getLocalizedMessage());
        }
    }
}
