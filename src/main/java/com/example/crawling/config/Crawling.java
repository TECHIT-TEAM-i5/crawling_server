package com.example.crawling.config;

import com.example.crawling.show.dto.ShowInfoRequestDto;
import com.example.crawling.artist.entity.Artist;
import com.example.crawling.artist.entity.ArtistGenre;
import com.example.crawling.artist.repo.ArtistRepository;
import com.example.crawling.artist.repo.QArtistRepo;
import com.example.crawling.show.repo.ShowInfoRepository;
import com.example.crawling.show.service.ShowInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class
Crawling {

    private final ArtistRepository artistRepository;
    private final QArtistRepo qArtistRepo;
    private final ShowInfoService showInfoService;
    private final ShowInfoRepository showInfoRepository;

    // 정해진 시간마다 자동으로 실행시키기 위함 (5분마다)

    @Scheduled(cron = "0 */5 * * * ?")
    public void createRecruitment() {
        ChromeOptions options = new ChromeOptions();
        // 브라우저 세션에 대한 페이지 로드 전략 설정
        options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
        // 최적화등을 위한 arguments 추가
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        // 크롬 드라이버의 새로운 객체 생성
        WebDriver driver = new ChromeDriver(options);

        log.info("create driver");

        try {
                // 10초 정도 대기
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
                // 크롤링할 URL
                String baseurl = "http://ticket.yes24.com/New/Genre/GenreList.aspx?genretype=1&genre=15456";
                // 해당 URL 로 이동
                driver.get(baseurl);
                // 제목이 보일때까지 10초간 대기
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                        ExpectedConditions.visibilityOfElementLocated(By.className("list-bigger-txt")));

//                JavascriptExecutor js = (JavascriptExecutor) driver;
//                js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
                // 페이지에 있는 모든 onClick 요소 찾기
                List<WebElement> elementsWithSpecificOnClick = driver
                    .findElements(By.xpath("//*[contains(@onclick, 'jsf_base_GoToPerfDetail')]"));

                ArrayList<String> urlsToVisit = new ArrayList<>();

            // 모든 URL을 수집
            for (WebElement element : elementsWithSpecificOnClick) {
                String onclickAttribute = element.getAttribute("onclick");
                String url = extractUrl(onclickAttribute); // URL 추출 로직을 적용
                urlsToVisit.add(url);
            }

            Collections.reverse(urlsToVisit);
            // 수집된 URL 각각에 대하여 처리
            for (String url : urlsToVisit) {
                driver.get(url);
                Document doc = Jsoup.connect(url).get();
                Elements titleData = doc.getElementsByClass("rn-big-title");
                // 만약 이미 있는 공연이라면 디비에 넣지 않고 종료
                if(showInfoRepository.existsByTitle(titleData.text())){
                    continue;
                }

                Elements dateData = doc.getElementsByClass("ps-date");

                Elements imgBox = doc.getElementsByClass("rn-product-imgbox");
                Element image = imgBox.select("img").first();
                String imageUrl = "";
                if(image != null){
                    imageUrl = image.attr("src");
                }

                // 주연 정보와 장소 정보 추출
                Elements rows = doc.select("table tr");  // 모든 행을 선택
                String artist = "";
                String location = "";

                for (Element row : rows) {
                    Elements ths = row.select("th");
                    Elements tds = row.select("td");

                    for (int i = 0; i < ths.size(); i++) {
                        String thText = ths.get(i).text();
                        if (thText.equals("주연")) {
                            artist = tds.get(i).text();  // 주연 정보 저장
                        } else if (thText.equals("공연장소")) {
                            location = tds.get(i).text();  // 공연장소 정보 저장
                        }
                    }
                }


                String title = titleData.text();
                String date = dateData.text();



                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

                // 날짜 문자열 분할
                String date1 = date.split(" ~ ")[0];
                String date2 = date.split(" ~ ")[1];

                // 문자열을 LocalDate 객체로 파싱
                LocalDate localDate1 = LocalDate.parse(date1, formatter);
                LocalDate localDate2 = LocalDate.parse(date2, formatter);

                // 두 날짜 사이의 차이를 일 단위로 계산
                long daysBetween = ChronoUnit.DAYS.between(localDate1, localDate2);

                if (daysBetween == 0){
                    daysBetween = 1L;
                }

                Integer duration = (int) daysBetween;

                // 아티스트 데이터 파싱
                artist = artist.replaceAll("[()]", ", "); // 괄호를 빈 문자열로 대체
                // 콤마와 공백을 기준으로 문자열 분리
                String[] artists = artist.split(", ");

                List<Long> artistsId = new ArrayList<>();
                Set<Long> genresId = new HashSet<>();

                // 아티스트 순회
                for(String showArtist: artists){
                    Optional<Artist> searched = artistRepository.findByName(showArtist);

                    if(searched.isPresent()){

                        Artist searchedArtist = searched.get();
                        log.info(searchedArtist.getName());
                        artistsId.add(searchedArtist.getId());
                        List<ArtistGenre> ArtistGenres = qArtistRepo.findArtistGenre(searchedArtist.getId());

                        // 장르 순회
                        for(ArtistGenre artistGenre: ArtistGenres){
                            genresId.add(artistGenre.getGenre().getId());
                        }

                    }
                }

                if(artistsId.isEmpty()){
                    // 아티스트가 없다면 그외장르(15L)
                    genresId.add(15L);
                }



                log.info("제목: " + title);
                log.info("아티스트: " + Arrays.toString(artists));
                log.info("공연 기간: " + date);
                log.info("공연 장소: " + location);
                log.info("티켓판매 url: " + url);
                log.info("공연 포스터 url: " + imageUrl);




                ShowInfoRequestDto showInfoRequestDto = ShowInfoRequestDto.builder()
                        .date(localDate1)
                        .location(location)
                        .duration(duration)
                        .title(title)
                        .ticketVendor(url)
                        .price("100000")
                        .artists(artistsId)
                        .genres(genresId)
                        .build();

                showInfoService.create(showInfoRequestDto, imageUrl);

                driver.navigate().back();
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("list-bigger-txt")));
            }

            log.info("Crawling completed");
        } catch (Exception e) {
            log.error("Crawling failed", e);
        } finally {
            // 크롤링 종료
            driver.quit();
        }
    }

    private String extractUrl(String onclickValue) {
        try {
            // onclick 값에서 숫자만 추출하기
            String id = onclickValue.replaceAll("[^0-9]", "");
            // 기본 URL과 결합하여 완전한 URL 생성
            return "http://ticket.yes24.com/Special/" + id;
        } catch (Exception e) {
            log.error("Error extracting URL from onclick attribute", e);
            return null;
        }
    }


}