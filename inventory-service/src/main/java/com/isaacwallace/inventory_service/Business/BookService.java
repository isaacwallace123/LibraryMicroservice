package com.isaacwallace.inventory_service.Business;

import com.isaacwallace.inventory_service.Presentation.Models.BookRequestModel;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface BookService {
    public List<BookResponseModel> getAllBooks();
    public BookResponseModel getBookById(String bookid);
    public BookResponseModel addBook(BookRequestModel bookRequestModel);
    public BookResponseModel updateBook(String bookid, BookRequestModel bookRequestModel);
    public void deleteBook(String bookid);
    public void deleteBooksByAuthor(@PathVariable String authorid);
}