package com.webprojectSEA.WebBlogProject.Services.AWSServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Paths;

@Service
public class AwsS3Service {

    private final S3Client s3Client;
    private final String bucketName;
    private final Region region;

    public AwsS3Service(@Value("${aws.accessKeyId}") String accessKeyId,
                        @Value("${aws.secretAccessKey}") String secretAccessKey,
                        @Value("${aws.region}") String region,
                        @Value("${aws.bucketName}") String bucketName) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.region = Region.of(region);
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .region(this.region)
                .build();
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String fileName = folderName + "/" + Paths.get(file.getOriginalFilename()).getFileName().toString();
        try {
            System.out.println("Storing file: " + fileName);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(URLConnection.guessContentTypeFromName(fileName))
                    .acl("public-read")
                    .build();

            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));
            System.out.println("File stored successfully: " + fileName);
            return getFileUrl(fileName);
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            throw new RuntimeException("Failed to store file in AWS S3", e);
        } catch (S3Exception e) {
            System.err.println("S3Exception: " + e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3Exception: " + e.awsErrorDetails().errorMessage(), e);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl != null && !fileUrl.isEmpty()) {
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            try {
                System.out.println("Deleting file: " + fileName);
                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
                System.out.println("File deleted successfully: " + fileName);
            } catch (S3Exception e) {
                System.err.println("S3Exception: " + e.awsErrorDetails().errorMessage());
                throw new RuntimeException("S3Exception: " + e.awsErrorDetails().errorMessage(), e);
            }
        }
    }

    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), fileName);
    }
}
