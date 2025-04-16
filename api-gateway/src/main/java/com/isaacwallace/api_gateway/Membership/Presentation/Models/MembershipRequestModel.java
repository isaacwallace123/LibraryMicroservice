package com.isaacwallace.api_gateway.Membership.Presentation.Models;

import com.isaacwallace.api_gateway.Membership.Business.Address;
import com.isaacwallace.api_gateway.Membership.Business.Phone;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MembershipRequestModel extends RepresentationModel<MembershipRequestModel> {
    String first_name;
    String last_name;

    String email;

    Address address;
    Phone phone;
}
