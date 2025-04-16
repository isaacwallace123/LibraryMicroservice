package com.isaacwallace.membership_service.Presentation.Models;

import com.isaacwallace.membership_service.DataAccess.Address;
import com.isaacwallace.membership_service.DataAccess.Phone;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
public class MemberResponseModel extends RepresentationModel<MemberResponseModel> {
    private String memberid;

    private String first_name;
    private String last_name;
    private String email;

    private Address address;
    private Phone phone;
}
