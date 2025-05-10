package com.isaacwallace.api_gateway.Author.Business;

import com.isaacwallace.api_gateway.Author.Presentation.AuthorController;
import com.isaacwallace.api_gateway.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Author.Presentation.Models.AuthorResponseModel;
import com.isaacwallace.api_gateway.DomainClient.AuthorServiceClient;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorServiceClient authorServiceClient;

    public AuthorServiceImpl(AuthorServiceClient authorServiceClient) {
        this.authorServiceClient = authorServiceClient;
    }

    public List<AuthorResponseModel> getAllAuthors() {
        return this.authorServiceClient.getAuthors().stream().map(this::addLinks).toList();
    }

    public AuthorResponseModel getAuthorById(String authorid) {;
        return this.addLinks(this.authorServiceClient.getAuthorByAuthorId(authorid));
    }

    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel) {
        return this.addLinks(this.authorServiceClient.addAuthor(authorRequestModel));
    }

    public AuthorResponseModel updateAuthor(String authorid, AuthorRequestModel authorRequestModel) {
        return this.addLinks(this.authorServiceClient.updateAuthor(authorid, authorRequestModel));
    }

    public void deleteAuthor(String authorid) {
        this.authorServiceClient.deleteAuthor(authorid);
    }

    private AuthorResponseModel addLinks(AuthorResponseModel authorResponseModel) {
        Link selfLink = linkTo(methodOn(AuthorController.class)
                .getAuthorById(authorResponseModel.getAuthorId()))
                .withSelfRel();
        authorResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(AuthorController.class)
                .getAuthors())
                .withRel("authors");
        authorResponseModel.add(allLink);

        return authorResponseModel;
    }
}
