package com.isaacwallace.api_gateway.Membership.Business;

import com.isaacwallace.api_gateway.Membership.Presentation.Models.MembershipRequestModel;
import com.isaacwallace.api_gateway.Membership.Presentation.Models.MembershipResponseModel;

import java.util.List;

public interface MembershipService {
    public List<MembershipResponseModel> getAllMembers();
    public MembershipResponseModel getMemberById(String memberid);
    public MembershipResponseModel addMember(MembershipRequestModel membershipRequestModel);
    public MembershipResponseModel updateMember(String memberid, MembershipRequestModel membershipRequestModel);
    public void deleteMember(String memberid);
}
