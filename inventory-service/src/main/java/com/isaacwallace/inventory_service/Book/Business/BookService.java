package com.isaacwallace.inventory_service.Book.Business;

import com.isaacwallace.inventory_service.Book.Presentation.Models.BookRequestModel;
import com.isaacwallace.inventory_service.Book.Presentation.Models.BookResponseModel;

import java.util.List;

public interface BookService {
    public List<BookResponseModel> getAllBooks();
    public BookResponseModel getBookById(String bookid);
    public BookResponseModel addBook(BookRequestModel bookRequestModel);
    public BookResponseModel updateBook(String bookid, BookRequestModel bookRequestModel);
    public void deleteBook(String bookid);
}