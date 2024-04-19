package com.example.crawling.show.repo;


import com.example.crawling.show.entity.ShowInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowInfoRepository extends JpaRepository<ShowInfo, Long> {
    boolean existsByTitle(String title);
}
