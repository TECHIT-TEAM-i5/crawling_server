package com.example.crawling.artist.dto;

import com.example.crawling.genre.dto.GenreDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ArtistDto {

    private Long id;
    private LocalDateTime createdAt;
    private String name;
    private Integer age;
    private String gender;
    private String profileImg;
    private Long likes;
    private Boolean isLiked;
    private Long subscribes;
    private Boolean isSubscribed;
    private List<GenreDto> genres;


}
