package com.isaacwallace.author_service.Mapper;

import com.isaacwallace.author_service.DataAccess.Author;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorResponseMapper {
    @Mapping(expression = "java(author.getAuthorIdentifier().getAuthorid())", target = "authorid")
    AuthorResponseModel entityToResponseModel(Author author);
    List<AuthorResponseModel> entityToResponseModelList(List<Author> authors);
}
