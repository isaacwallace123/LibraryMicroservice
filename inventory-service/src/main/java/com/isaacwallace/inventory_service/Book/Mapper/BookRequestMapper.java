package com.isaacwallace.inventory_service.Book.Mapper;

import com.isaacwallace.inventory_service.Book.DataAccess.Book;
import com.isaacwallace.inventory_service.Book.DataAccess.BookIdentifier;
import com.isaacwallace.inventory_service.Book.Presentation.Models.BookRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookRequestMapper {

    @Mapping(target = "id", ignore = true)
    Book requestModelToEntity(BookRequestModel bookRequestModel, BookIdentifier bookIdentifier);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(BookRequestModel bookRequestModel, @MappingTarget Book book);
}
