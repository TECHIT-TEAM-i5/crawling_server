package com.example.crawling.show.repo;

import com.example.crawling.show.entity.ShowArtist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowArtistRepo extends JpaRepository<ShowArtist, Long> {
  List<ShowArtist> findByShowInfoId(Long showInfoId);
}
