package com.isaacwallace.membership_service.Presentation.Models;

import com.isaacwallace.membership_service.DataAccess.Address;
import com.isaacwallace.membership_service.DataAccess.Phone;
import lombok.*;

import org.springframework.hateoas.RepresentationModel;

@Value
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberRequestModel extends RepresentationModel<MemberRequestModel> {
    String first_name;
    String last_name;

    String email;

    Address address;
    Phone phone;
}
