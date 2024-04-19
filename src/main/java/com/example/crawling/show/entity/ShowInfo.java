package com.example.crawling.show.entity;

import com.example.crawling.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ShowInfo extends BaseEntity {

    @Column(nullable = false)
    @Setter
    private LocalDate date; //공연 날짜
    @Setter
    private LocalTime startTime; //공연 시작 시간
    @Setter
    private Integer hours; //예상 공연 시간
    @Setter
    private Integer duration; //공연 진행 기간 (3일이면 72시간)

    @Column(nullable = false)
    @Setter
    private String location;
    @Column(nullable = false)
    @Setter
    private String title;
    @Setter
    private String ticketVendor; //예매 사이트 url
    @Setter
    private String posterImage;
    @Column(nullable = false)
    @Setter
    private String price;

    @Builder.Default
    @OneToMany(mappedBy = "showInfo")
    private List<ShowLike> showLikes = new ArrayList<>();

    @OneToMany(mappedBy = "showInfo")
    private List<ShowGenre> showGenres = new ArrayList<>();

    @OneToMany(mappedBy = "showInfo")
    private List<ShowArtist> showArtists = new ArrayList<>();



    public void addShowGenres(ShowGenre showGenre) {
        if (showGenres != null) {
            showGenre.setShowInfo(this);
            showGenres.add(showGenre);
        }
    }

    public void addShowArtists(ShowArtist showArtist) {
        if (showArtists != null) {
            showArtist.setShowInfo(this);
            showArtists.add(showArtist);
        }
    }
}
