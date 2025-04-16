package com.isaacwallace.author_service.Presentation.Models;

import lombok.Data;

import org.springframework.hateoas.RepresentationModel;

@Data
public class AuthorResponseModel extends RepresentationModel<AuthorResponseModel> {
    private String authorId;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
