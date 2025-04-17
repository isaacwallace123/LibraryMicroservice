package com.isaacwallace.membership_service.Mapper;

import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;
import com.isaacwallace.membership_service.DataAccess.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MemberResponseMapper {
    @Mapping(expression = "java(member.getMemberIdentifier().getMemberid())", target = "memberid")
    MemberResponseModel entityToResponseModel(Member member);
    List<MemberResponseModel> entityToResponseModelList(List<Member> members);
}
