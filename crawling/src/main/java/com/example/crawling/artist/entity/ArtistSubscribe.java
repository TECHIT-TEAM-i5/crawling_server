package com.example.crawling.artist.entity;

import com.example.crawling.BaseEntity;
import com.example.crawling.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSubscribe  extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  private Artist artist;
  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity userEntity;
}
