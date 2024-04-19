package com.example.crawling.genre.repo;

import com.example.crawling.genre.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
