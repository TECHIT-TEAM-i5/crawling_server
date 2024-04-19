package com.example.crawling.genre.entity;

import com.example.crawling.BaseEntity;
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
public class Genre extends BaseEntity {
  private String name;

  @Builder.Default
  @OneToMany(mappedBy = "genre")
  private List<GenreSubscribe> subscribes = new ArrayList<>();


}
