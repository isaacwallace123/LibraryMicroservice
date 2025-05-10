package com.isaacwallace.inventory_service.Mapper;

import com.isaacwallace.inventory_service.DataAccess.Availability;
import com.isaacwallace.inventory_service.DataAccess.Book;
import com.isaacwallace.inventory_service.Presentation.Models.BookResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookResponseMapper {
    @Mapping(expression = "java(book.getBookIdentifier().getBookid())", target = "bookid")
    BookResponseModel entityToResponseModel(Book book);
    List<BookResponseModel> entityToResponseModelList(List<Book> books);

    @AfterMapping
    default void mapResponseFields(@MappingTarget BookResponseModel bookResponseModel, Book book) {
        bookResponseModel.setAvailability(bookResponseModel.getStock() > 0 ? Availability.AVAILABLE : Availability.UNAVAILABLE);
    }
}
