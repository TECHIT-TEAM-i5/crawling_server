package com.example.crawling.artist.repo;

import com.example.crawling.artist.entity.ArtistGenre;
import com.example.crawling.artist.entity.QArtist;
import com.example.crawling.artist.entity.QArtistGenre;
import com.example.crawling.genre.entity.QGenre;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class QArtistRepoImpl implements QArtistRepo {
  private final JPAQueryFactory queryFactory;
  private final QArtist qArtist = new QArtist("qArtist");
  private final QGenre qGenre = new QGenre("qGenre");
  private final QArtistGenre qArtistGenre = new QArtistGenre("qArtistGenre");



  @Override
  public List<ArtistGenre> findArtistGenre(Long artistId) {
    return queryFactory
            .selectFrom(qArtistGenre)
            .innerJoin(qArtistGenre.artist, qArtist).fetchJoin()  // Artist 정보를 즉시 로드
            .innerJoin(qArtistGenre.genre, qGenre).fetchJoin()    // Genre 정보를 즉시 로드
            .where(qArtistGenre.artist.id.eq(artistId))
            .fetch();
  }

}
