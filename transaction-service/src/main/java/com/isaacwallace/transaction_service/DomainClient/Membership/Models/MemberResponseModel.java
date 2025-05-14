package com.isaacwallace.transaction_service.DomainClient.Membership.Models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberResponseModel {
    private String memberid;

    private String firstName;
    private String lastName;
    private String email;

    private Address address;
    private Phone phone;
}
