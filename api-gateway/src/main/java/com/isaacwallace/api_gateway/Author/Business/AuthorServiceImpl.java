package com.isaacwallace.api_gateway.Author.Business;

import com.isaacwallace.api_gateway.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Author.Presentation.Models.AuthorResponseModel;
import com.isaacwallace.api_gateway.DomainClient.AuthorServiceClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorServiceClient authorServiceClient;

    public AuthorServiceImpl(AuthorServiceClient authorServiceClient) {
        this.authorServiceClient = authorServiceClient;
    }

    public List<AuthorResponseModel> getAllAuthors() {
        return this.authorServiceClient.getAuthors();
    }

    public AuthorResponseModel getAuthorById(String authorid) {
        return this.authorServiceClient.getAuthorByAuthorId(authorid);
    }

    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel) {
        return this.authorServiceClient.addAuthor(authorRequestModel);
    }

    public AuthorResponseModel updateAuthor(String id, AuthorRequestModel authorRequestModel) {
        return this.authorServiceClient.updateAuthor(authorRequestModel, id);
    }

    public void deleteAuthor(String authorid) {
        this.authorServiceClient.deleteAuthor(authorid);
    }
}
