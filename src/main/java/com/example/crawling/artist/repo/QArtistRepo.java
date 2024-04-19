package com.example.crawling.artist.repo;

import com.example.crawling.artist.entity.ArtistGenre;

import java.util.List;

public interface QArtistRepo {

  List<ArtistGenre> findArtistGenre(Long artistId);
}
