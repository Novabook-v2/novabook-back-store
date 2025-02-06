package store.novabook.store.image.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.book.dto.request.ReviewImageDTO;
import store.novabook.store.book.entity.Book;
import store.novabook.store.book.entity.Review;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.common.exception.InternalServerException;
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
public class ImageServiceImpl implements ImageService {
	private final ImageRepository imageRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final BookImageRepository bookImageRepository;
	private static final String LOCAL_PATH = "/Users/isehui/nhn-academy/supernova-v2/novabook-back-store/src/main/resources/tmp/";

	@Override
	public void createReviewImage(Review review, List<ReviewImageDTO> reviewImageDTOs) {
		if (reviewImageDTOs == null || reviewImageDTOs.isEmpty()) {
			log.warn("No review images provided.");
			return;
		}

		List<MultipartFile> files = FileConverter.convertToMultipartFile(reviewImageDTOs);
		List<Image> savedImages = new ArrayList<>();

		for (MultipartFile file : files) {
			String fileName = file.getOriginalFilename();
			String outputFilePath = "%s/%s".formatted(LOCAL_PATH, fileName);
			Path imagePath = Paths.get(outputFilePath);

			try {
				// 이미지 파일 저장
				Files.write(imagePath, file.getBytes());
				log.info("Successfully saved review image: {}", outputFilePath);

				// DB에 저장
				Image imageEntity = imageRepository.save(new Image(outputFilePath));
				savedImages.add(imageEntity);
			} catch (IOException e) {
				log.error("Failed to save review image: {}. Error: {}", fileName, e.getMessage(), e);
				fileDelete(imagePath, outputFilePath);
				throw new InternalServerException(ErrorCode.FAILED_CREATE_BOOK);
			}
		}

		// ReviewImage 매핑 후 저장
		savedImages.forEach(image -> reviewImageRepository.save(new ReviewImage(review, image)));

		log.info("Successfully saved all review images for review: {}", review.getId());
	}

	public void createBookImage(Book book, String requestImage) {
		String fileName = requestImage.substring(requestImage.lastIndexOf("/") + 1);
		String outputFilePath = "/%s%s".formatted(LOCAL_PATH, fileName);
		Path imagePath = Paths.get(outputFilePath);
		try (InputStream in = new URI(requestImage).toURL().openStream()) {
			Files.copy(in, imagePath);
		} catch (IOException | URISyntaxException e) {
			log.error("Failed to download file: {}. Error: {}", requestImage, e.getMessage(), e);
			fileDelete(imagePath, outputFilePath);
			throw new InternalServerException(ErrorCode.FAILED_CREATE_BOOK);
		}

		Image save = imageRepository.save(new Image(requestImage));
		bookImageRepository.save(BookImage.of(book, save));
	}

	public void fileDelete(Path imagePath, String outputFilePath) {
		if (Files.exists(imagePath)) {
			try {
				Files.delete(imagePath);
				log.info("Successfully deleted file: {}", outputFilePath);
			} catch (IOException ex) {
				log.error("Failed to delete file: {}. Error: {}", outputFilePath, ex.getMessage(), ex);
			}
		}
	}

}