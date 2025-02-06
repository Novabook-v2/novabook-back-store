package store.novabook.store.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.novabook.store.image.entity.BookImage;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
	@Query("SELECT bi.image.id FROM BookImage bi WHERE bi.book.id = :bookId")
	Optional<Long> findImageIdByBookId(@Param("bookId") Long bookId);
}
