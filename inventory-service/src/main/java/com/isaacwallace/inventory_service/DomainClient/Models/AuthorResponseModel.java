package com.isaacwallace.inventory_service.DomainClient.Models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthorResponseModel {
    private String authorId;

    private String firstName;
    private String lastName;

    private String pseudonym;
}
