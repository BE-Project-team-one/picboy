package com.sparta.picboy.S3Upload;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.sparta.picboy.domain.post.Post;
import com.sparta.picboy.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AwsS3Service {

    private final PostRepository postRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFiles(String file, String dirName) throws IOException {
        File uploadFile = convert(file)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));
        return upload(uploadFile, dirName);
    }
    public String upload(File uploadFile, String filePath) {
        String fileName = "";

        if(filePath.length() >= 14) {
             fileName = filePath + "/" + UUID.randomUUID() + "-" + filePath.substring(14);   //  S3에 저장된 파일 이름
        } else {
             fileName = filePath + "/" + UUID.randomUUID() + "-" + filePath.substring(11);   //  S3에 저장된 파일 이름
        }

        String uploadImageUrl = putS3(uploadFile, fileName); //  s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            System.out.println("File delete success");
            return;
        }
        System.out.println("File delete fail");
    }

    // 로컬에 Base64 업로드 하기
    private Optional<File> convert(String stringImage) throws IOException {
        byte[] bytes = decodeBase64(stringImage);
        File convertFile = new File(System.getProperty("user.dir") + "/" + "ServerImageFile.png");
        if (convertFile.createNewFile()) { //  바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(bytes);
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }


    // 버킷 게시물 이미지 폴더 삭제
    public void removeFolder(String folderName){
        ListObjectsV2Request listObjectsV2Request = new ListObjectsV2Request().withBucketName(bucket).withPrefix(folderName+"/");
        ListObjectsV2Result listObjectsV2Result = amazonS3Client.listObjectsV2(listObjectsV2Request);

        for (S3ObjectSummary objectSummary : listObjectsV2Result.getObjectSummaries()) {
            DeleteObjectRequest request = new DeleteObjectRequest(bucket, objectSummary.getKey());
            amazonS3Client.deleteObject(request);
          //  System.out.println("Deleted " + objectSummary.getKey());
        }
    }

    public void deleteImage(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    // base64 file decoder
    public byte[] decodeBase64(String encodedFile) {
        String substring = encodedFile.substring(encodedFile.indexOf(",") + 1);
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(substring);
    }

    // 파일 다운로드
    public ResponseEntity<byte[]> getObject(Long postId, String storedFileName) throws IOException {
        String substring = storedFileName.substring(storedFileName.lastIndexOf("picboy/"));
        S3Object o = amazonS3Client.getObject(new GetObjectRequest(bucket, substring));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        Post post = postRepository.findById(postId).orElseThrow();
        String fileName = post.getId() + " " + post.getTopic() + ".gif";
        String encodedFilename = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", encodedFilename);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }




}
