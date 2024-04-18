package com.example.crawling.alert.entity;

import com.example.crawling.artist.entity.ArtistSubscribe;
import com.example.crawling.BaseEntity;
import com.example.crawling.genre.entity.GenreSubscribe;
import com.example.crawling.show.entity.ShowInfo;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Alert extends BaseEntity {
  private String title;
  @Setter
  private String message;

  private String userNickname;
  private String userEmail;

  @ManyToOne(fetch = FetchType.LAZY)
  private ShowInfo showInfo;

  @ManyToOne(fetch = FetchType.LAZY)
  @Setter
  private ArtistSubscribe artistSubscribe;

  @ManyToOne(fetch = FetchType.LAZY)
  @Setter
  private GenreSubscribe genreSubscribe;
}
