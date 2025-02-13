package store.novabook.store.image.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.book.dto.request.ReviewImageDTO;
import store.novabook.store.book.entity.Book;
import store.novabook.store.book.entity.Review;
import store.novabook.store.common.util.FileConverter;
import store.novabook.store.image.entity.BookImage;
import store.novabook.store.image.entity.Image;
import store.novabook.store.image.entity.ReviewImage;
import store.novabook.store.image.repository.BookImageRepository;
import store.novabook.store.image.repository.ImageRepository;
import store.novabook.store.image.repository.ReviewImageRepository;
import store.novabook.store.image.service.ImageService;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
@Profile("prod")
public class ImageAwsServiceImpl implements ImageService {

	private final AmazonS3 amazonS3;
	private final ImageRepository imageRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final BookImageRepository bookImageRepository;

	@Value("${cloud-aws-s3-bucket}")
	private String bucket;

	@Override
	public void createReviewImage(Review review, List<ReviewImageDTO> reviewImageDTOs)  {
		if (reviewImageDTOs == null || reviewImageDTOs.isEmpty()) {
			log.warn("No review images provided.");
			return;
		}

		List<MultipartFile> files = FileConverter.convertToMultipartFile(reviewImageDTOs);

		for (MultipartFile file : files) {
			String fileUrl = null;
			try {
				fileUrl = uploadToS3(file.getOriginalFilename(), file.getInputStream(), file.getSize(),
					file.getContentType());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Image imageEntity = imageRepository.save(new Image(fileUrl));
			reviewImageRepository.save(new ReviewImage(review, imageEntity));
		}

		log.info("Successfully uploaded all review images to S3 for review: {}", review.getId());
	}

	@Override
	public void createBookImage(Book book, String requestImage) {
		String fileName = requestImage.substring(requestImage.lastIndexOf("/") + 1); // URL에서 파일명 추출

		try (InputStream inputStream = new URI(requestImage).toURL().openStream()) {
			// 🔹 직접 URL에서 S3로 업로드
			String fileUrl = uploadToS3(fileName, inputStream, -1, "image/jpeg");

			Image savedImage = imageRepository.save(new Image(fileUrl));
			bookImageRepository.save(BookImage.of(book, savedImage));
			log.info("Successfully uploaded book image to S3: name = {} {}", savedImage, fileUrl);
		} catch (IOException | URISyntaxException e) {
			log.error("Failed to upload book image to S3. Error: {}", e.getMessage(), e);
			throw new RuntimeException("S3 Upload Failed");
		}
	}

	/**
	 * 🔹 S3에 파일 업로드 (InputStream 사용)
	 */
	private String uploadToS3(String fileName, InputStream inputStream, long contentLength, String contentType) throws
		IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		if (contentLength > 0) {
			metadata.setContentLength(contentLength);
		}
		metadata.setContentType(contentType);

		amazonS3.putObject(bucket, fileName, inputStream, metadata);
		return amazonS3.getUrl(bucket, fileName).toString();
	}
}