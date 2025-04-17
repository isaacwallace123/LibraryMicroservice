package com.isaacwallace.author_service.Presentation.Models;

import lombok.*;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorRequestModel {
    String firstName;
    String lastName;

    String pseudonym;
}
