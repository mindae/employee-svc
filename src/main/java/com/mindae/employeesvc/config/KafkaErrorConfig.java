package com.mindae.employeesvc.config;

import com.mindae.employeesvc.eda.exception.InvalidEmployeeEventException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {
    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, String> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate, ((record, exception) ->
                        new TopicPartition(record.topic() + "-dlt", record.partition())));

        FixedBackOff fixedBackOff = new FixedBackOff(
                2000L,
                2L
        );

        DefaultErrorHandler errorHandler =
                new DefaultErrorHandler(
                        recoverer,
                        fixedBackOff
                );

        errorHandler.addNotRetryableExceptions(
                InvalidEmployeeEventException.class);

        return errorHandler;
    }
}
