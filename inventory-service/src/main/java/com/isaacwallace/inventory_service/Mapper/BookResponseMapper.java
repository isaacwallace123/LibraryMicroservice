package com.isaacwallace.inventory_service.Mapper;

import com.isaacwallace.inventory_service.DataAccess.Book;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {
    @Mapping(expression = "java(book.getBookIdentifier().getBookid())", target = "bookid")
    BookResponseModel entityToResponseModel(Book book);
    List<BookResponseModel> entityToResponseModelList(List<Book> books);
    List<BookResponseModel> entitiesToResponseModelList(List<Book> books);
}
