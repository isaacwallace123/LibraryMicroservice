package com.isaacwallace.author_service.Mapper;

import com.isaacwallace.author_service.DataAccess.Author;
import com.isaacwallace.author_service.DataAccess.AuthorIdentifier;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AuthorRequestMapper {
    @Mapping(target = "id", ignore = true)
    Author requestModelToEntity(AuthorRequestModel authorRequestModel, AuthorIdentifier authorIdentifier);
    void updateEntityFromRequest(AuthorRequestModel authorRequestModel, @MappingTarget Author author);
}
