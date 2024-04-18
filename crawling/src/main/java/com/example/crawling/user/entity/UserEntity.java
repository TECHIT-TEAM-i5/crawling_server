package com.example.crawling.user.entity;

import com.example.crawling.BaseEntity;
import com.example.crawling.artist.entity.ArtistLike;
import com.example.crawling.artist.entity.ArtistSubscribe;
import com.example.crawling.genre.entity.GenreSubscribe;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {
  @Column(nullable = false, unique = true)
  private String loginId;
  @Column(nullable = false)
  @Setter
  private String password;
  @Column(nullable = false)
  @Setter
  private String email; //email로 가입여부 식별
  @Setter
  private String nickname;
  @Setter
  private String gender;
  @Setter
  private String phone;
  @Setter
  private String profileImg;
  @Setter
  private String address;
  @Setter
  private String authorities;


  @OneToMany(mappedBy = "userEntity")
  private List<ArtistLike> likeList;

  @OneToMany(mappedBy = "userEntity")
  private List<GenreSubscribe> genreSubscribeList;

  @OneToMany(mappedBy = "userEntity")
  private List<ArtistSubscribe> artistSubscribeList;


}
