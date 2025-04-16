package com.isaacwallace.author_service.Presentation;

import com.isaacwallace.author_service.Business.AuthorService;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuthorResponseModel>> getAllAuthors() {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.getAllAuthors());
    }

    @GetMapping(value = "{authorid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseModel> getAuthorById(@PathVariable String authorid) {
        return ResponseEntity.status(HttpStatus.OK).body(authorService.getAuthorById(authorid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseModel> addAuthor(@RequestBody AuthorRequestModel authorRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.authorService.addAuthor(authorRequestModel));
    }

    @PutMapping(value = "{authorid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseModel> updateAuthor(@PathVariable String authorid, @RequestBody AuthorRequestModel authorRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.authorService.updateAuthor(authorid, authorRequestModel));
    }

    @DeleteMapping(value = "{authorid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthorResponseModel> deleteAuthor(@PathVariable String authorid) {
        this.authorService.deleteAuthor(authorid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
