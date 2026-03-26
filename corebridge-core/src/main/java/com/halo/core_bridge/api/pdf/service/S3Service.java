package com.halo.core_bridge.api.pdf.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    public String generatePresignedUrl(String directory, String originalFilename) {
        String fileName = directory + "/" + UUID.randomUUID() + "_" + originalFilename;

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime() + 1000 * 60 * 10; // 10분
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);

        return url.toString();
    }

    public String getFileKey(String presignedUrl) {
        // presigned URL에서 파일 key 추출
        String path = presignedUrl.split("\\?")[0];
        return path.substring(path.indexOf(bucket) + bucket.length() + 1);
    }
}