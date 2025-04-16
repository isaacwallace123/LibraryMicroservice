package com.isaacwallace.author_service.Business;

import com.isaacwallace.author_service.DataAccess.Author;
import com.isaacwallace.author_service.DataAccess.AuthorIdentifier;
import com.isaacwallace.author_service.DataAccess.AuthorRepository;
import com.isaacwallace.author_service.Mapper.AuthorRequestMapper;
import com.isaacwallace.author_service.Mapper.AuthorResponseMapper;
import com.isaacwallace.author_service.Presentation.Models.AuthorRequestModel;
import com.isaacwallace.author_service.Presentation.Models.AuthorResponseModel;
import com.isaacwallace.author_service.Utils.Exceptions.InUseException;
import com.isaacwallace.author_service.Utils.Exceptions.InvalidInputException;
import com.isaacwallace.author_service.Utils.Exceptions.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final AuthorResponseMapper authorResponseMapper;
    private final AuthorRequestMapper authorRequestMapper;

    public AuthorServiceImpl(AuthorRepository authorRepository, AuthorResponseMapper authorResponseMapper, AuthorRequestMapper authorRequestMapper) {
        this.authorRepository = authorRepository;
        this.authorResponseMapper = authorResponseMapper;
        this.authorRequestMapper = authorRequestMapper;
    }

    private void validateAuthorRequestModel(AuthorRequestModel model) {
        if (model.getFirstName() == null || model.getFirstName().isBlank()) {
            throw new InvalidInputException("Invalid firstName: " + model.getFirstName());
        }
        if (model.getLastName() == null || model.getLastName().isBlank()) {
            throw new InvalidInputException("Invalid lastName: " + model.getLastName());
        }
    }

    private Author getAuthorObjectById(String authorid) {
        try {
            UUID.fromString(authorid);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid authorid: " + authorid);
        }

        Author author = this.authorRepository.findAuthorByAuthorIdentifier_Authorid(authorid);

        if (author == null) {
            throw new NotFoundException("Unknown authorid: " + authorid);
        }

        return author;
    }

    public List<AuthorResponseModel> getAllAuthors() {
        return authorResponseMapper.entityToResponseModelList(authorRepository.findAll());
    }

    public AuthorResponseModel getAuthorById(String authorid) {
        Author author = this.getAuthorObjectById(authorid);

        return this.authorResponseMapper.entityToResponseModel(author);
    }

    public AuthorResponseModel addAuthor(AuthorRequestModel authorRequestModel) {
        Author author = this.authorRequestMapper.requestModelToEntity(authorRequestModel, new AuthorIdentifier());

        this.validateAuthorRequestModel(authorRequestModel);

        return authorResponseMapper.entityToResponseModel(this.authorRepository.save(author));
    }

    public AuthorResponseModel updateAuthor(String authorid, AuthorRequestModel authorRequestModel) {
        Author author = this.getAuthorObjectById(authorid);

        this.validateAuthorRequestModel(authorRequestModel);

        this.authorRequestMapper.updateEntityFromRequest(authorRequestModel, author);

        Author updatedAuthor = this.authorRepository.save(author);

        return this.authorResponseMapper.entityToResponseModel(updatedAuthor);
    }

    public void deleteAuthor(String authorid) {
        Author author = this.getAuthorObjectById(authorid);

        try {
            this.authorRepository.delete(author);
        } catch (DataIntegrityViolationException e) {
            throw new InUseException("Author with id: " + authorid + " is already in use by another entity.");
        }
    }
}
