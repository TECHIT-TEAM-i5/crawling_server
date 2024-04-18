package com.example.crawling.show.service;

import com.example.crawling.alert.service.EmailAlertService;
import com.example.crawling.artist.entity.Artist;
import com.example.crawling.artist.repo.ArtistRepository;
import com.example.crawling.show.dto.ShowInfoRequestDto;
import com.example.crawling.genre.entity.Genre;
import com.example.crawling.genre.repo.GenreRepository;
import com.example.crawling.show.entity.ShowArtist;
import com.example.crawling.show.entity.ShowGenre;
import com.example.crawling.show.entity.ShowInfo;
import com.example.crawling.show.repo.ShowArtistRepo;
import com.example.crawling.show.repo.ShowGenreRepo;
import com.example.crawling.show.repo.ShowInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ShowInfoService {
    private final ShowInfoRepository showInfoRepository;
    private final S3FileService s3FileService;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;
    private final ShowGenreRepo showGenreRepo;
    private final ShowArtistRepo showArtistRepo;
    private final EmailAlertService emailAlertService;

    // 공연 정보 등록
    @Transactional
    public void create(ShowInfoRequestDto dto, String imgUrl) {

        // 공연 기본정보 + 아티스트 정보 + 장르 정보 + 이미지 파일

        // 1) 공연 기본정보 저장 (ShowInfo)
        ShowInfo showInfo = ShowInfo.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .hours(dto.getHours())
                .duration(dto.getDuration())
                .location(dto.getLocation())
                .title(dto.getTitle())
                .ticketVendor(dto.getTicketVendor())
                // s3service
                .posterImage(s3FileService.uploadIntoS3("/showInfoImg", imgUrl))
                .price(dto.getPrice())
                .build();

        ShowInfo saved = showInfoRepository.save(showInfo);

        if (dto.getArtists() != null) {
            // 검색된 아티스트/장르 찾아서 show와 artist/genre 연결지어주는 엔티티 객체 생성 후 저장
            List<ShowArtist> showArtists = dto.getArtists().stream()
                .map(artistId -> {
                    Artist artist = artistRepository.findById(artistId)
                        .orElseThrow(() -> new NoSuchElementException("not found."));
                    return showArtistRepo.save(ShowArtist.builder()
                        .artist(artist)
                        .showInfo(saved)
                        .build());
                }).toList();
            for (ShowArtist showArtist : showArtists) {
                saved.addShowArtists(showArtist);
            }

        }

        if (dto.getGenres() != null) {
            List<ShowGenre> showGenres = dto.getGenres().stream()
                .map(genreId -> {
                    Genre genre = genreRepository.findById(genreId)
                        .orElseThrow(() -> new NoSuchElementException("not found."));
                    return showGenreRepo.save(ShowGenre.builder()
                        .genre(genre)
                        .showInfo(saved)
                        .build());
                }).toList();
            for (ShowGenre showGenre : showGenres) {
                saved.addShowGenres(showGenre);
            }
        }
        ShowInfo finalSaved = showInfoRepository.save(saved);
        emailAlertService.createAlert(finalSaved.getId());

    }



}