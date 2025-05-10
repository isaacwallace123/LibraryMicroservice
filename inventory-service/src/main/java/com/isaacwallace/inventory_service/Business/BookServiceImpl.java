package com.isaacwallace.inventory_service.Business;

import com.isaacwallace.inventory_service.DataAccess.Book;
import com.isaacwallace.inventory_service.DataAccess.BookIdentifier;
import com.isaacwallace.inventory_service.DataAccess.BookRepository;
import com.isaacwallace.inventory_service.DomainClient.AuthorServiceClient;
import com.isaacwallace.inventory_service.Mapper.BookRequestMapper;
import com.isaacwallace.inventory_service.Mapper.BookResponseMapper;
import com.isaacwallace.inventory_service.Presentation.Models.BookRequestModel;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import com.isaacwallace.inventory_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.inventory_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookResponseMapper bookResponseMapper;
    private final BookRequestMapper bookRequestMapper;
    private final AuthorServiceClient authorServiceClient;

    public BookServiceImpl(BookRepository bookRepository, BookResponseMapper bookResponseMapper, BookRequestMapper bookRequestMapper, AuthorServiceClient authorServiceClient) {
        this.bookRepository = bookRepository;
        this.bookResponseMapper = bookResponseMapper;
        this.bookRequestMapper = bookRequestMapper;

        this.authorServiceClient = authorServiceClient;
    }

    private void validateBookInvariant(Book book) {
        if (book.getAuthorid() == null || book.getAuthorid().isEmpty()) {
            throw new InvalidInputException("Book must be associated with an existing author.");
        }

        this.authorServiceClient.validateAuthorExists(book.getAuthorid());
    }

    public List<BookResponseModel> getAllBooks() {
        return this.bookResponseMapper.entityToResponseModelList(this.bookRepository.findAll());
    }

    public BookResponseModel getBookById(String bookid) {
        Book book = this.bookRepository.findBookByBookIdentifier_Bookid(bookid);

        if (book == null) {
            throw new NotFoundException("Unknown bookid: " + bookid);
        }

        return this.bookResponseMapper.entityToResponseModel(book);
    }

    public BookResponseModel addBook(BookRequestModel bookRequestModel) {
        Book book = this.bookRequestMapper.requestModelToEntity(bookRequestModel, new BookIdentifier());

        this.validateBookInvariant(book);

        return this.bookResponseMapper.entityToResponseModel(this.bookRepository.save(book));
    }

    public BookResponseModel updateBook(String bookid, BookRequestModel bookRequestModel) {
        Book book = this.bookRepository.findBookByBookIdentifier_Bookid(bookid);

        if (book == null) {
            throw new NotFoundException("Unknown bookid: " + bookid);
        }

        this.bookRequestMapper.updateEntityFromRequest(bookRequestModel, book);

        this.validateBookInvariant(book);

        log.info("Updated book with bookid: {}", bookid);

        return this.bookResponseMapper.entityToResponseModel(this.bookRepository.save(book));
    }

    public void deleteBook(String bookid) {
        Book book = this.bookRepository.findBookByBookIdentifier_Bookid(bookid);

        if (book == null) {
            throw new NotFoundException("Unknown bookid: " + bookid);
        }

        this.bookRepository.delete(book);
    }
}
