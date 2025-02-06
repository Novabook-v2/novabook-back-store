package store.novabook.store.search.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.novabook.store.book.entity.Book;

public interface JpaBookSearchRepository extends JpaRepository<Book, Long> {
	// 키워드 검색 (제목, 설명, 상세 설명에서 검색)
	@Query("SELECT b FROM Book b " +
		"WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(b.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		"OR LOWER(b.descriptionDetail) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

	// 저자로 검색
	@Query("SELECT b FROM Book b WHERE LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%'))")
	Page<Book> searchByAuthor(@Param("author") String author, Pageable pageable);

	// 출판사로 검색
	@Query("SELECT b FROM Book b WHERE LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))")
	Page<Book> searchByPublisher(@Param("publisher") String publisher, Pageable pageable);

	// 카테고리 검색 (BookStatus 엔티티의 name 필드를 기준으로 검색)
	@Query("SELECT b FROM Book b WHERE LOWER(b.bookStatus.name) LIKE LOWER(CONCAT('%', :category, '%'))")
	Page<Book> searchByCategory(@Param("category") String category, Pageable pageable);
}
