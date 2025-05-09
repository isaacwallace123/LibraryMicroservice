package com.isaacwallace.inventory_service.Presentation;

import com.isaacwallace.inventory_service.Business.BookService;
import com.isaacwallace.inventory_service.Presentation.Models.BookRequestModel;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/books")
public class BookController {
    private BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("")
    public ResponseEntity<List<BookResponseModel>> getAllBooks() {
        return ResponseEntity.status(HttpStatus.OK).body(this.bookService.getAllBooks());
    }

    @GetMapping("{bookid}")
    public ResponseEntity<BookResponseModel> getBookById(@PathVariable String bookid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.bookService.getBookById(bookid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseModel> addBook(@RequestBody BookRequestModel bookRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bookService.addBook(bookRequestModel));
    }

    @PutMapping(value = "{bookid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResponseModel> EditBook(@PathVariable String bookid, @RequestBody BookRequestModel bookRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.bookService.updateBook(bookid, bookRequestModel));
    }

    @DeleteMapping("{bookid}")
    public ResponseEntity<BookResponseModel> DeleteBook(@PathVariable String bookid) {
        this.bookService.deleteBook(bookid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
