package com.example.crawling.artist.repo;

import com.example.crawling.artist.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {

  Optional<Artist> findByName(String artistName);
}
