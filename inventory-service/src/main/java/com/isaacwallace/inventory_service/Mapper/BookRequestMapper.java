package com.isaacwallace.inventory_service.Mapper;

import com.isaacwallace.inventory_service.DataAccess.Book;
import com.isaacwallace.inventory_service.DataAccess.BookIdentifier;
import com.isaacwallace.inventory_service.Presentation.Models.BookRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookIdentifier", source = "bookIdentifier")
    Book requestModelToEntity(BookRequestModel bookRequestModel, BookIdentifier bookIdentifier);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookIdentifier", ignore = true)
    void updateEntityFromRequest(BookRequestModel bookRequestModel, @MappingTarget Book book);
}
