package com.isaacwallace.api_gateway.Author.Presentation.Models;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorRequestModel extends RepresentationModel<AuthorRequestModel> {
    String firstName;
    String lastName;

    String pseudonym;
}
