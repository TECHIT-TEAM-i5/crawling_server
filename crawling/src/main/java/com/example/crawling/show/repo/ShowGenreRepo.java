package com.example.crawling.show.repo;

import com.example.crawling.show.entity.ShowGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowGenreRepo extends JpaRepository<ShowGenre, Long> {
  List<ShowGenre> findByShowInfoId(Long showInfoId);
}
