package com.example.crawling.show.dto;

import com.example.crawling.artist.dto.ArtistDto;
import com.example.crawling.genre.dto.GenreDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShowInfoResponseDto {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private Integer hours;
    private Integer duration;
    private String location;
    private String title;
    private String ticketVendor;
    private String price;
    private String posterImage;
    private Long totalLike;
    @Setter
    private Boolean isLiked;

    private List<ArtistDto> artists;
    private List<GenreDto> genres;


}
