package com.isaacwallace.api_gateway.Services.Author.Business;

import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.api_gateway.Services.Author.Presentation.Models.AuthorResponseModel;

import java.util.List;

public interface AuthorService {
    public List<AuthorResponseModel> getAllAuthors();
    public AuthorResponseModel getAuthorById(String authorid);
    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel);
    public AuthorResponseModel updateAuthor(String id, AuthorRequestModel authorRequestModel);
    public void deleteAuthor(String authorid);
}
