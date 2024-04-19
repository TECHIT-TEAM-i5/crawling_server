package com.example.crawling.alert.service;

import com.example.crawling.alert.dto.AlertDto;
import com.example.crawling.alert.entity.Alert;
import com.example.crawling.alert.repo.AlertRepository;
import com.example.crawling.artist.entity.ArtistSubscribe;
import com.example.crawling.artist.repo.ArtistSubscribeRepo;
import com.example.crawling.genre.entity.GenreSubscribe;
import com.example.crawling.genre.repo.GenreSubscribeRepo;
import com.example.crawling.show.entity.ShowArtist;
import com.example.crawling.show.entity.ShowGenre;
import com.example.crawling.show.repo.ShowArtistRepo;
import com.example.crawling.show.repo.ShowGenreRepo;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAlertService {
    private final AlertRepository alertRepository;
    private final ShowArtistRepo showArtistRepo;
    private final ShowGenreRepo showGenreRepo;
    private final GenreSubscribeRepo genreSubscribeRepo;
    private final ArtistSubscribeRepo artistSubscribeRepo;

    // RabbitMQ 사용
    private final RabbitTemplate rabbitTemplate;

    private final Queue queue;
    private final Gson gson;
    private static final String EMAIL_ALARM_TITLE = "알림 : 새 공연이 등록되었습니다";

    @Transactional
    public void createAlert(Long showInfoId) {
        // 해당 공연정보에서 아티스트 관련 알림 객체 생성
        List<ShowArtist> showArtists = showArtistRepo.findByShowInfoId(showInfoId);
        generateArtistSubAlert(showArtists);

        List<ShowGenre> showGenres = showGenreRepo.findByShowInfoId(showInfoId);
        generateGenreSubAlert(showGenres);

        List<Alert> alerts = alertRepository.findByShowInfoId(showInfoId);
        for (Alert alert : alerts) {
            log.info("send email to RabbitMQ");
            alert.setMessage(generateMessage(alert, alert.getUserNickname()));
            AlertDto alertDto = AlertDto.builder()
                    .email(alert.getUserEmail())
                    .title(alert.getTitle())
                    .message(alert.getMessage()).build();
            // 메시지 큐로 전송
            rabbitTemplate.convertAndSend(queue.getName(), gson.toJson(alertDto));
        }
    }

    private void generateArtistSubAlert(List<ShowArtist> shows) {
        for (ShowArtist artist : shows) {
            List<ArtistSubscribe> subscribes = artistSubscribeRepo.findByArtistId(artist.getArtist().getId());
            for(ArtistSubscribe subscribe : subscribes) {
                String userEmail = subscribe.getUserEntity().getEmail();
                Optional<Alert> alertOptional = alertRepository.
                        findByUserEmailAndShowInfoId(userEmail, artist.getShowInfo().getId());

                // 이미 해당 구독자 유저에 대한 알림이 생성된 상태이면 이미 생성된 alert에 추가만 하고 알림 이메일은 보내지 않음
                if (alertOptional.isEmpty()) {
                    Alert alert = Alert.builder()
                            .showInfo(artist.getShowInfo())
                            .userEmail(subscribe.getUserEntity().getEmail())
                            .userNickname(subscribe.getUserEntity().getNickname())
                            .artistSubscribe(subscribe)
                            .title(EMAIL_ALARM_TITLE)
                            .build();

                    alertRepository.save(alert);
                }
            }
        }
    }

    private void generateGenreSubAlert(List<ShowGenre> shows) {
        for (ShowGenre genre : shows) {
            List<GenreSubscribe> subscribes = genreSubscribeRepo.findByGenreId(genre.getGenre().getId());
            for (GenreSubscribe subscribe : subscribes) {
                String userEmail = subscribe.getUserEntity().getEmail();
                Optional<Alert> alertOptional = alertRepository.findByUserEmailAndShowInfoId(userEmail, genre.getShowInfo().getId());
                Alert alert;
                // 이미 해당 구독자 유저에 대한 알림이 생성된 상태이면 이미 생성된 alert에 추가만 하고 알림 이메일은 보내지 않음
                if (alertOptional.isPresent()) {
                    alert = alertOptional.get();
                    alert.setGenreSubscribe(subscribe);
                    alertRepository.save(alert);
                } else { // 한 유저에 대한 알림이 생성되지 않은 상태이면 새로 생성
                    alert = Alert.builder()
                            .showInfo(genre.getShowInfo())
                            .genreSubscribe(subscribe)
                            .title(EMAIL_ALARM_TITLE)
                            .userEmail(userEmail)
                            .userNickname(subscribe.getUserEntity().getNickname())
                            .build();
                    alertRepository.save(alert);
                }
            }
        }
    }

    private String generateMessage(Alert alert, String userNickname){
        StringBuffer sb = new StringBuffer();
        sb.append("안녕하세요. ").append(userNickname).append("님, 스테이지 알람에서 알림 드립니다.   \n");
        sb.append("구독하신 ");
        if (alert.getGenreSubscribe() != null && alert.getArtistSubscribe()!=null) {
            sb.append("아티스트 :: ").append(alert.getArtistSubscribe().getArtist().getName()).append("와 ");
            sb.append("장르 :: ").append(alert.getGenreSubscribe().getGenre().getName()).append("의 공연 정보가 등록되었습니다.  \n");
        } else if (alert.getGenreSubscribe() == null) {
            sb.append("아티스트 :: ").append(alert.getArtistSubscribe().getArtist().getName()).append("의 공연 정보가 등록되었습니다.  \n");
        } else {
            sb.append("장르 :: ").append(alert.getGenreSubscribe().getGenre().getName()).append("의 공연 정보가 등록되었습니다.  \n");
        }
        sb.append("해당 공연 보기 : ").append(alert.getShowInfo().getTicketVendor()).append(" \n\n");
        sb.append("저희 스테이지 알람을 사랑해주셔서 감사합니다. ");

        return sb.toString();
    }

}
