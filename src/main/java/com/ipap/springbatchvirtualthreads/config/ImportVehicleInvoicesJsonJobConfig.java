package com.ipap.springbatchvirtualthreads.config;

import com.ipap.springbatchvirtualthreads.dto.VehicleJsonDTO;
import com.ipap.springbatchvirtualthreads.listener.CustomJobExecutionListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
// @EnableBatchProcessing
public class ImportVehicleInvoicesJsonJobConfig {

    @Value("${input.folder.vehicles.json}")
    private Resource[] resources;

    private final CustomJobExecutionListener customJobExecutionListener;

    @Bean
    @Primary
    public Job importJsonVehicleInvoicesJob(JobRepository repository, Step importJsonVehicleStep) {
        return new JobBuilder("importJsonVehicleInvoiceJob", repository)
                .incrementer(new RunIdIncrementer())
                .start(importJsonVehicleStep)
                .listener(customJobExecutionListener)
                .build();
    }

    @Bean
    public Step importJsonVehicleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importVehicleInvoiceStep", jobRepository)
                .<VehicleJsonDTO, VehicleJsonDTO>chunk(100, transactionManager)
                .reader(multiResourceItemReader())
                .processor(ImportVehicleInvoicesJsonJobConfig::vehicleProcessor)
                .writer(item -> log.info("Writing item: {}", item))
                .build();
    }

    public JsonItemReader<VehicleJsonDTO> jsonItemReader() {
        return new JsonItemReaderBuilder<VehicleJsonDTO>()
                .name("vehicle json reader ")
                .jsonObjectReader(new JacksonJsonObjectReader<>(VehicleJsonDTO.class))
                .strict(false) // default Strict
                .build();
    }

    private static VehicleJsonDTO vehicleProcessor(VehicleJsonDTO item) {
        log.info("Processing item: {}", item);
        return item;
    }

    public MultiResourceItemReader<VehicleJsonDTO> multiResourceItemReader() {
        return new MultiResourceItemReaderBuilder<VehicleJsonDTO>()
                .name("vehicle invoice resources item reader")
                .resources(resources)
                .delegate(jsonItemReader())
                .build();
    }
}
