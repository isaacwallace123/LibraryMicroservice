package com.isaacwallace.inventory_service.Mapper;

import com.isaacwallace.inventory_service.DataAccess.Availability;
import com.isaacwallace.inventory_service.DataAccess.Book;
import com.isaacwallace.inventory_service.DomainClient.Author.AuthorServiceClient;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {
    @Mapping(expression = "java(book.getBookIdentifier().getBookid())", target = "bookid")
    @Mapping(expression = "java(authorServiceClient.getAuthorById(book.getAuthorid()))", target = "author")
    @Mapping(target = "availability", ignore = true)

    BookResponseModel entityToResponseModel(Book book, @Context AuthorServiceClient authorServiceClient);

    default List<BookResponseModel> entitiesToResponseModelList(List<Book> books, AuthorServiceClient authorServiceClient) {
        return books.stream()
                .map(book -> entityToResponseModel(book, authorServiceClient))
                .toList();
    }

    @AfterMapping
    default void mapResponseFields(@MappingTarget BookResponseModel bookResponseModel, Book book) {
        bookResponseModel.setAvailability(bookResponseModel.getStock() > 0 ? Availability.AVAILABLE : Availability.UNAVAILABLE);
    }
}
