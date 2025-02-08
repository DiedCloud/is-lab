package org.example.islab.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.InputStream;

@Service
public class MinioService {
    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.access-key}") String accessKey,
                        @Value("${minio.secret-key}") String secretKey,
                        @Value("${minio.bucket}") String bucket) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucket;
    }

    public void uploadFile(String objectName, InputStream inputStream, long size, String contentType) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .stream(inputStream, size, -1)
                        .contentType(contentType)
                        .build()
        );
    }

    public InputStream downloadFile(String objectName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    public void deleteFile(String objectName) throws Exception {
        RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(this.bucketName).object(objectName).build();
        minioClient.removeObject(args);
    }
}
