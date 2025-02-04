package store.novabook.store.image.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	@Override
	public void createReviewImage(Review review, List<ReviewImageDTO> reviewImageDTOs) {

	}

	public void createBookImage(Book book, String requestImage) {

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