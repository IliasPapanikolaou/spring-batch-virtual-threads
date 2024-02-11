package com.ipap.springbatchvirtualthreads.config;

import com.ipap.springbatchvirtualthreads.dto.VehicleCsvDTO;
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
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.MultiResourceItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
// @EnableBatchProcessing
public class ImportVehicleInvoicesFlatFileJobConfig {

    @Value("${input.folder.vehicles}")
    private Resource[] resources;

    private final CustomJobExecutionListener customJobExecutionListener;

    @Bean
    public Job importCsvVehicleInvoicesJob(JobRepository repository, Step importCsvVehicleStep) {
        return new JobBuilder("importCsvVehicleInvoiceJob", repository)
                .incrementer(new RunIdIncrementer())
                .start(importCsvVehicleStep)
                .listener(customJobExecutionListener)
                .build();
    }

    @Bean
    public Step importCsvVehicleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("importVehicleInvoiceStep", jobRepository)
                .<VehicleCsvDTO, VehicleCsvDTO>chunk(100, transactionManager)
                //.reader(vehicleDTOFlatFileItemReader())
                .reader(multiResourceItemReader())
                .processor(ImportVehicleInvoicesFlatFileJobConfig::vehicleProcessor)
                .writer(item -> log.info("Writing item: {}", item))
                .build();
    }

    private static VehicleCsvDTO vehicleProcessor(VehicleCsvDTO item) {
        log.info("Processing item: {}", item);
        return item;
    }

    public MultiResourceItemReader<VehicleCsvDTO> multiResourceItemReader() {
        return new MultiResourceItemReaderBuilder<VehicleCsvDTO>()
                .name("vehicle invoice resources item reader")
                .resources(resources)
                .delegate(vehicleDTOFlatFileItemReader())
                .build();
    }

    public ResourceAwareItemReaderItemStream<VehicleCsvDTO> vehicleDTOFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<VehicleCsvDTO>()
                .name("vehicle invoice item reader")
                .saveState(Boolean.FALSE)
                .linesToSkip(1) // skip header
                .delimited().delimiter(",")
                .names("id", "manufacturer", "model", "owner")
                .comments("#") // skip comments start with '#'
                .targetType(VehicleCsvDTO.class)
                .build();
    }

    /*public FlatFileItemReader<VehicleDTO> vehicleDTOFlatFileItemReader() {
        return new FlatFileItemReaderBuilder<VehicleDTO>()
                .name("vehicle invoice item reader")
                .resource(new ClassPathResource("/data/car_invoices.csv"))
                // .strict(Boolean.TRUE) // True Default
                // .distanceLimit(1) // misspelling toleration
                .saveState(Boolean.FALSE)
                .linesToSkip(1) // skip header
                .delimited().delimiter(",")
                .names("id", "manufacturer", "model", "owner")
                .comments("#") // skip comments start with '#'
                .targetType(VehicleDTO.class)
                .build();
    }*/
}
