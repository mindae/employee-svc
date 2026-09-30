package com.mindae.employeesvc.eda.repo;

import com.mindae.employeesvc.eda.event.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository
        extends JpaRepository<OutboxEvent, String> {

    List<OutboxEvent> findByPublishedFalse();
}
