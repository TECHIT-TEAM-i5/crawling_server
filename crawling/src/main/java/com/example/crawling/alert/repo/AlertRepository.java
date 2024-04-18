package com.example.crawling.alert.repo;

import com.example.crawling.alert.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

  Optional<Alert> findByUserEmailAndShowInfoId(String userEmail, Long showInfoId);

  List<Alert> findByShowInfoId(Long showInfoId);

}
