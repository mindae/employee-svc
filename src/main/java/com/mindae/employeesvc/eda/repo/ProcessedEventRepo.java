package com.mindae.employeesvc.eda.repo;

import com.mindae.employeesvc.eda.event.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepo extends JpaRepository<ProcessedEvent, String> {
    
}
