package com.example.crawling.show.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    @Transactional
    public String uploadIntoS3(String folder, String imageUrl) {
        log.info("uploadIntoS3 tx start");
        String imgUrlList = "";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(imageUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                byte[] imageBytes = EntityUtils.toByteArray(response.getEntity());
                String fileExtension = imageUrl.substring(imageUrl.lastIndexOf("."));
                String filename = UUID.randomUUID() + fileExtension;

                // S3에 이미지 업로드
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                metadata.setContentType("image/" + fileExtension.substring(1));
                try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                    amazonS3.putObject(new PutObjectRequest(bucket + folder,
                            filename,
                            inputStream,
                            metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                    imgUrlList = amazonS3.getUrl(bucket + folder, filename).toString();
                }
            }
        } catch (IOException e) {
            log.warn(e.getMessage() + " : 이미지 업로드에 실패하였습니다. ");
        }
        log.info("uploadIntoS3 tx end");
        return imgUrlList;
    }



}
