package com.isaacwallace.membership_service.Business;

import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;

import java.util.List;

public interface MemberService {
    public List<MemberResponseModel> getAllMembers();
    public  MemberResponseModel getMemberById(String memberid);
    public MemberResponseModel addMember(MemberRequestModel memberRequestModel);
    public MemberResponseModel updateMember(String memberid, MemberRequestModel memberRequestModel);
    public void deleteMember(String memberid);
}
