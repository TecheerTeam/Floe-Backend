package project.floe.domain.record.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.floe.domain.record.dto.request.UpdateMediaRequest;
import project.floe.domain.record.entity.Media;
import project.floe.domain.record.repository.MediaJpaRepository;
import project.floe.domain.record.entity.Record;
import project.floe.global.error.ErrorCode;
import project.floe.global.error.exception.BusinessException;
import project.floe.global.error.exception.EmptyResultException;
import project.floe.global.error.exception.S3Exception;

@Service
@Slf4j
@RequiredArgsConstructor
public class MediaService {

    public static final String KEY_SPLIT_PREFIX = ".com/";
    private final AmazonS3 amazonS3;
    private final MediaJpaRepository repository;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    @Transactional
    public void uploadFiles(Record record, List<MultipartFile> files) {
        for (MultipartFile eachFile : files) {
            Media media = Media.builder()
                    .mediaUrl(uploadToS3(eachFile))
                    .record(record)
                    .build();
            repository.save(media);
        }
    }

    public Media findMediaById(Long mediaId) {
        return repository.findById(mediaId)
                .orElseThrow(() -> new EmptyResultException(ErrorCode.MEDIA_NOT_FOUND_ERROR));
    }

    @Transactional
    public List<Media> updateMedias(Record record, List<UpdateMediaRequest> existingFiles, List<MultipartFile> newFiles) {
        List<Media> updatedMedias = new ArrayList<>();
        Iterator<MultipartFile> newFilesIterator = getIterator(newFiles);
        for (UpdateMediaRequest media : existingFiles) {
            if (media == null) {
                addNewMedia(record, newFilesIterator, updatedMedias);
                continue;
            }
            updatedMedias.add(findMediaById(media.getMediaId()));
        }
        uploadRemainFiles(record, newFilesIterator, updatedMedias);
        removeNotUseFiles(record.getMedias(), updatedMedias);
        return updatedMedias;
    }

    public void removeNotUseFiles(List<Media> existedFiles, List<Media> updatedFiles){
        // updatedFiles의 Media ID 목록
        List<Long> updatedFilesIds = updatedFiles.stream()
                .map(Media::getId)
                .toList();

        // existedFiles에 있는데 updatedFiles에 없는 Media 객체를 찾기
        List<Media> filesToRemove = existedFiles.stream()
                .filter(media -> !updatedFilesIds.contains(media.getId()))
                .toList();
        deleteUnusedFiles(filesToRemove);
    }

    @Transactional
    private void deleteUnusedFiles(List<Media> filesToRemove) {
        for (Media media: filesToRemove){
            deleteFile(media.getMediaUrl());
            repository.deleteById(media.getId());
        }
    }

    private void uploadRemainFiles(Record record, Iterator<MultipartFile> newFilesIterator, List<Media> updatedMedias) {
        while (newFilesIterator.hasNext()) {
            String newMediaUrl = uploadToS3(newFilesIterator.next());
            updatedMedias.add(Media.builder().record(record).mediaUrl(newMediaUrl).build());
        }
    }

    private void addNewMedia(Record record, Iterator<MultipartFile> newFilesIterator, List<Media> updatedMedias) {
        if (!newFilesIterator.hasNext()) {
            throw new BusinessException(ErrorCode.UPDATE_FILE_SEQUENCE_MISMATCH);
        }
        String newMediaUrl = uploadToS3(newFilesIterator.next());
        updatedMedias.add(Media.builder().record(record).mediaUrl(newMediaUrl).build());
    }

    private static Iterator<MultipartFile> getIterator(List<MultipartFile> newFiles) {
        Iterator<MultipartFile> newFilesIterator = Optional.ofNullable(newFiles)
                .map(List::iterator)
                .orElse(Collections.emptyIterator());
        return newFilesIterator;
    }

    public void deleteFiles(List<Media> medias) {
        for (Media media : medias) {
            deleteFile(media.getMediaUrl());
        }
    }

    public void deleteFile(String mediaUrl) {
        String key = extractKey(mediaUrl);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
    }

    private String extractKey(String mediaUrl) {
        String splitStr = KEY_SPLIT_PREFIX;
        return mediaUrl.substring(mediaUrl.lastIndexOf(splitStr) + splitStr.length());
    }

    public String uploadToS3(MultipartFile media) {
        String originalFilename = getOrigianlFilename(media);
        String s3FileName = UUID.randomUUID() + "_" + originalFilename;

        byte[] bytes = converToByte(media);

        // 메타데이터 설정
        ObjectMetadata metadata = setMetadata(media, bytes);

        // S3에 파일 업로드
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest); // put image to S3
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.S3_UPLOAD_FAIL_ERROR);
        }
        return amazonS3.getUrl(bucketName, s3FileName).toString();
    }

    private static byte[] converToByte(MultipartFile media) {
        // S3로 넘기기 위해서 파일 내용을 입력 스트림에서 바이트 배열로 변환
        try (InputStream is = media.getInputStream()) {
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new S3Exception(ErrorCode.CANNOT_CONVERT_TO_BYTE);
        }
    }

    private static ObjectMetadata setMetadata(MultipartFile media, byte[] bytes) {
        // 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(media.getContentType());
        metadata.setContentLength(bytes.length);
        return metadata;
    }

    private static String getOrigianlFilename(MultipartFile media) {
        String originalFilename = media.getOriginalFilename(); // 원본 파일 명
        if (originalFilename==null || originalFilename.isEmpty()) {
            throw new S3Exception(ErrorCode.FILE_NAME_NOT_FOUND_ERROR);
        }
        return originalFilename;
    }
}
