package store.novabook.store.search.dto;

import lombok.Builder;
import store.novabook.store.book.entity.Book;

@Builder
public record JpaGetBookSearchResponse(
	Long id,
	String title,
	String author,
	String publisher,
	String image,
	Long price,
	Long discountPrice,
	Double score,
	Boolean isPackaged,
	Integer review
) {
	public static JpaGetBookSearchResponse of(Book book, Double score, Integer review, String imageUrl) {
		return JpaGetBookSearchResponse.builder()
			.id(book.getId())
			.title(book.getTitle())
			.author(book.getAuthor())
			.publisher(book.getPublisher())
			.image(imageUrl) // Book 엔티티에 이미지 필드가 없으므로 null 처리
			.price(book.getPrice())
			.discountPrice(book.getDiscountPrice())
			.score(score != null ? score : 0.0) // 기본값 설정
			.isPackaged(book.isPackaged())
			.review(review != null ? review : 0) // 기본값 설정
			.build();
	}

	public static JpaGetBookSearchResponse of(Book book, String imageUrl) {
		return of(book, 0.0,0, imageUrl); // 기본 score 및 review 값 설정
	}
}