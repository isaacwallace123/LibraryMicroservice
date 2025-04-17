package com.isaacwallace.api_gateway.Services.Membership.Business;

import com.isaacwallace.api_gateway.DomainClient.MembershipServiceClient;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.EmployeeController;
import com.isaacwallace.api_gateway.Services.Employee.Presentation.Models.EmployeeResponseModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.MembershipController;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Services.Membership.Presentation.Models.MembershipResponseModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class MembershipServiceImpl implements MembershipService {
    private final MembershipServiceClient membershipServiceClient;

    public MembershipServiceImpl(MembershipServiceClient membershipServiceClient) {
        this.membershipServiceClient = membershipServiceClient;
    }

    public List<MembershipResponseModel> getAllMembers() {
        return this.membershipServiceClient.getMembers().stream().map(this::addLinks).toList();
    }

    public MembershipResponseModel getMemberById(String memberid) {
        return this.addLinks(this.membershipServiceClient.getMemberByMemberId(memberid));
    }

    public MembershipResponseModel addMember(MembershipRequestModel membershipRequestModel) {
        return this.addLinks(this.membershipServiceClient.addMember(membershipRequestModel));
    }

    public MembershipResponseModel updateMember(String memberid, MembershipRequestModel membershipRequestModel) {
        return this.addLinks(this.membershipServiceClient.updateMember(memberid, membershipRequestModel));
    }

    public void deleteMember(String memberid) {
        this.membershipServiceClient.deleteMember(memberid);
    }

    private MembershipResponseModel addLinks(MembershipResponseModel membershipResponseModel) {
        Link selfLink = linkTo(methodOn(MembershipController.class)
                .getMemberById(membershipResponseModel.getMemberid()))
                .withSelfRel();
        membershipResponseModel.add(selfLink);

        Link allLink = linkTo(methodOn(MembershipController.class)
                .getMembers())
                .withRel("members");
        membershipResponseModel.add(allLink);

        return membershipResponseModel;
    }
}
