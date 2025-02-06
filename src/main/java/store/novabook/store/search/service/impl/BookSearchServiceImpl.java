package store.novabook.store.search.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import store.novabook.store.book.entity.Book;
import store.novabook.store.common.exception.ErrorCode;
import store.novabook.store.common.exception.InternalServerException;
import store.novabook.store.image.entity.BookImage;
import store.novabook.store.image.repository.BookImageRepository;
import store.novabook.store.image.repository.ImageRepository;
import store.novabook.store.search.dto.JpaGetBookSearchResponse;
import store.novabook.store.search.repository.JpaBookSearchRepository;

import java.util.Optional;
@Slf4j
@Service
@RequiredArgsConstructor
public class BookSearchServiceImpl {
	private final JpaBookSearchRepository bookRepository;
	private final BookImageRepository bookImageRepository;
	private final ImageRepository imageRepository;

	// 키워드 검색 (제목, 설명, 상세 설명)
	public Page<JpaGetBookSearchResponse> searchByKeyword(String keyword, Pageable pageable) {
		try {
			Page<Book> books = bookRepository.searchByKeyword(keyword, pageable);
			return books.map(this::mapToResponse);
		} catch (Exception e) {
			throw new InternalServerException(ErrorCode.INVALID_REQUEST_ARGUMENT);
		}
	}

	// 저자 검색
	public Page<JpaGetBookSearchResponse> searchByAuthor(String author, Pageable pageable) {
		try {
			Page<Book> books = bookRepository.searchByAuthor(author, pageable);
			return books.map(this::mapToResponse);
		} catch (Exception e) {
			throw new InternalServerException(ErrorCode.INVALID_REQUEST_ARGUMENT);
		}
	}

	// 출판사 검색
	public Page<JpaGetBookSearchResponse> searchByPublisher(String publisher, Pageable pageable) {
		try {
			Page<Book> books = bookRepository.searchByPublisher(publisher, pageable);
			return books.map(this::mapToResponse);
		} catch (Exception e) {
			throw new InternalServerException(ErrorCode.INVALID_REQUEST_ARGUMENT);
		}
	}

	// 카테고리 검색
	public Page<JpaGetBookSearchResponse> searchByCategory(String category, Pageable pageable) {
		try {
			Page<Book> books = bookRepository.searchByCategory(category, pageable);
			return books.map(this::mapToResponse);
		} catch (Exception e) {
			throw new InternalServerException(ErrorCode.INVALID_REQUEST_ARGUMENT);
		}
	}

	/**
	 * Book 엔티티를 JpaGetBookSearchResponse로 변환하는 메서드
	 * - 이미지 정보 조회 추가
	 */
	private JpaGetBookSearchResponse mapToResponse(Book book) {

		// 1️⃣ Book ID로 Image ID 조회
		Optional<Long> imageIdOpt = bookImageRepository.findImageIdByBookId(book.getId());

		// 2️⃣ Image ID로 source 값 조회
		String imageUrl = imageIdOpt.flatMap(imageRepository::findSourceById).orElse(null);

		log.info("image Url = {}", imageUrl);
		// 3️⃣ DTO 변환
		return JpaGetBookSearchResponse.of(book, 0.0, 0, imageUrl);
	}
}