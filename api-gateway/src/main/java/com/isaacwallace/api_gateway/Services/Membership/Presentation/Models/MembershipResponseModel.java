package com.isaacwallace.api_gateway.Services.Membership.Presentation.Models;

import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Address;
import com.isaacwallace.api_gateway.Services.Membership.DataAccess.Phone;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class MembershipResponseModel extends RepresentationModel<MembershipResponseModel> {
    private String memberid;

    private String firstName;
    private String lastName;
    private String email;

    private Address address;
    private Phone phone;
}
