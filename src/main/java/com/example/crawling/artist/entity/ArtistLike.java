package com.example.crawling.artist.entity;

import com.example.crawling.BaseEntity;
import com.example.crawling.user.entity.UserEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ArtistLike extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  private Artist artist;
  @ManyToOne(fetch = FetchType.LAZY)
  private UserEntity userEntity;



}
