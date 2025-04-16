package com.isaacwallace.author_service.Mapper;

import com.isaacwallace.author_service.DataAccess.Author;
import com.isaacwallace.author_service.Presentation.AuthorController;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface AuthorResponseMapper {
    @Mapping(expression = "java(author.getAuthorIdentifier().getAuthorid())", target = "authorId")
    AuthorResponseModel entityToResponseModel(Author author);
    List<AuthorResponseModel> entityToResponseModelList(List<Author> authors);

    @AfterMapping
    default void addLinks(@MappingTarget AuthorResponseModel authorResponseModel, Author author) {
        Link selfLink = linkTo(methodOn(AuthorController.class).getAuthorById(author.getAuthorIdentifier().getAuthorid())).withSelfRel();
        authorResponseModel.add(selfLink);

        Link authorsLink = linkTo(methodOn(AuthorController.class).getAllAuthors()).withRel("authors");
        authorResponseModel.add(authorsLink);
    }
}
