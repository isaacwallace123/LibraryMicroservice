package com.isaacwallace.author_service.Business;

import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;

import java.util.List;

public interface AuthorService {
    public List<AuthorResponseModel> getAllAuthors();
    public AuthorResponseModel getAuthorById(String authorid);
    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel);
    public AuthorResponseModel updateAuthor(String id, AuthorRequestModel authorRequestModel);
    public void deleteAuthor(String authorid);
}
