package com.ipap.springbatchvirtualthreads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBatchVirtualThreadsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchVirtualThreadsApplication.class, args);
    }

}
