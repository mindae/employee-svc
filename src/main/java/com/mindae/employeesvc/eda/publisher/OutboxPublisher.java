package com.mindae.employeesvc.eda.publisher;

import com.mindae.employeesvc.eda.event.OutboxEvent;
import com.mindae.employeesvc.eda.repo.OutboxEventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxPublisher {
    private final OutboxEventRepository oer;
    private final KafkaTemplate<String, String> kt;

    public OutboxPublisher(OutboxEventRepository oer,
                           KafkaTemplate<String, String> kt) {
        this.oer = oer;
        this.kt = kt;
    }

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> eventsByPublishedFalse = oer.findByPublishedFalse();
        for (OutboxEvent event : eventsByPublishedFalse) {
            kt.send(
                    "employee-events-3p",
                    event.getAggregateId(),
                    event.getPayload()
            );
            event.markPublished();
            oer.save(event);
        }
    }
}
