package com.example.crawling.artist.entity;

import com.example.crawling.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Artist extends BaseEntity {

  @Column(nullable = false)
  private String name;
  private Integer age;
  private String gender;
  private String profileImg;

  @Builder.Default
  @OneToMany(mappedBy = "artist")
  private List<ArtistLike> likes = new ArrayList<>(); //좋아요 개수

  @OneToMany(mappedBy = "artist")
  private List<ArtistSubscribe> subscribes = new ArrayList<>();

  @OneToMany(mappedBy = "artist")
  private List<ArtistGenre> genres = new ArrayList<>();




}
