package com.example.crawling.genre.dto;

import com.example.crawling.genre.entity.Genre;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

    private Long id;
    private LocalDateTime createdAt;
    private String name;
    private Long subscribes;
    private Boolean isSubscribed;


}
