package com.example.crawling.genre.repo;

import com.example.crawling.genre.entity.GenreSubscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GenreSubscribeRepo extends JpaRepository<GenreSubscribe, Long> {

    List<GenreSubscribe> findByGenreId(Long genreId);
}
