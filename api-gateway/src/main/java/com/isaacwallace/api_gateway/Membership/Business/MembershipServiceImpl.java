package com.isaacwallace.api_gateway.Membership.Business;

import com.isaacwallace.api_gateway.DomainClient.MembershipServiceClient;
import com.isaacwallace.api_gateway.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Membership.Presentation.Models.MembershipResponseModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipServiceImpl implements MembershipService {
    private final MembershipServiceClient membershipServiceClient;

    public MembershipServiceImpl(MembershipServiceClient membershipServiceClient) {
        this.membershipServiceClient = membershipServiceClient;
    }

    public List<MembershipResponseModel> getAllMembers() {
        return this.membershipServiceClient.getMembers();
    }

    public MembershipResponseModel getMemberById(String memberid) {
        return this.membershipServiceClient.getMemberByMemberId(memberid);
    }

    public MembershipResponseModel addMember(MembershipRequestModel membershipRequestModel) {
        return this.membershipServiceClient.addMember(membershipRequestModel);
    }

    public MembershipResponseModel updateMember(String memberid, MembershipRequestModel membershipRequestModel) {
        return this.membershipServiceClient.updateMember(membershipRequestModel, memberid);
    }

    public void deleteMember(String memberid) {
        this.membershipServiceClient.deleteMember(memberid);
    }
}
