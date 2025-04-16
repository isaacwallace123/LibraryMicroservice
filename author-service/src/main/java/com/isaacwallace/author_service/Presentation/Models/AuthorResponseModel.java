package com.isaacwallace.author_service.Presentation.Models;

import lombok.Builder;
import lombok.Data;

import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
public class AuthorResponseModel {
    private String authorId;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
