package com.example.crawling.artist.entity;

import com.example.crawling.BaseEntity;
import com.example.crawling.genre.entity.Genre;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ArtistGenre extends BaseEntity {

  @Setter
  @ManyToOne(fetch = FetchType.LAZY)
  private Artist artist;
  @ManyToOne(fetch = FetchType.LAZY)
  private Genre genre;



}
