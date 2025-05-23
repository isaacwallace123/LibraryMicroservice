package com.isaacwallace.author_service.Presentation.Models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorResponseModel {
    private String authorid;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
