package com.isaacwallace.api_gateway.Membership.Presentation.Models;

import com.isaacwallace.api_gateway.Membership.DataAccess.Address;
import com.isaacwallace.api_gateway.Membership.DataAccess.Phone;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MembershipRequestModel extends RepresentationModel<MembershipRequestModel> {
    String firstName;
    String lastName;

    String email;

    Address address;
    Phone phone;
}
