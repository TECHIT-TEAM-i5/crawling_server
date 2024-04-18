package com.example.crawling.artist.repo;

import com.example.crawling.artist.entity.ArtistSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistSubscribeRepo extends JpaRepository<ArtistSubscribe, Long> {

  List<ArtistSubscribe> findByArtistId(Long artistId);
}
