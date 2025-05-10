package com.isaacwallace.api_gateway.Author.Presentation.Models;

import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class AuthorResponseModel extends RepresentationModel<AuthorResponseModel> {
    private String authorId;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
