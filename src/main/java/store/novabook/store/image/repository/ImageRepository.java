package store.novabook.store.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import store.novabook.store.image.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {
	@Query("SELECT i.source FROM Image i WHERE i.id = :imageId")
	Optional<String> findSourceById(@Param("imageId") Long imageId);
}
