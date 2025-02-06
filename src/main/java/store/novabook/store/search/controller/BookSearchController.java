package store.novabook.store.search.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import store.novabook.store.search.controller.docs.BookSearchControllerDocs;
import store.novabook.store.search.dto.JpaGetBookSearchResponse;
import store.novabook.store.search.service.impl.BookSearchServiceImpl;

@RestController
@RequestMapping("/api/v1/store/search")
@RequiredArgsConstructor
public class BookSearchController implements BookSearchControllerDocs {

	private final BookSearchServiceImpl bookSearchService;

	@GetMapping("/keyword")
	public Page<JpaGetBookSearchResponse> searchByKeyword(@RequestParam String title, Pageable pageable) {
		return bookSearchService.searchByKeyword(title, pageable);
	}

	@GetMapping("/author")
	public Page<JpaGetBookSearchResponse> searchByAuthor(@RequestParam String author, Pageable pageable) {
		return bookSearchService.searchByAuthor(author, pageable);
	}

	@GetMapping("/publish")
	public Page<JpaGetBookSearchResponse> searchByPublish(@RequestParam String publish, Pageable pageable) {
		return bookSearchService.searchByPublisher(publish, pageable);
	}

	@GetMapping("/category")
	public Page<JpaGetBookSearchResponse> searchByCategory(@RequestParam String category, Pageable pageable) {
		return bookSearchService.searchByCategory(category, pageable);
	}

}
