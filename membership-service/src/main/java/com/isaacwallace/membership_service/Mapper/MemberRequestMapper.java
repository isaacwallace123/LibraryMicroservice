package com.isaacwallace.membership_service.Mapper;


import com.isaacwallace.membership_service.DataAccess.MemberIdentifier;
import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.DataAccess.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MemberRequestMapper {
    @Mapping(target = "id", ignore = true)
    Member requestModelToEntity(MemberRequestModel memberRequestModel, MemberIdentifier memberIdentifier);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(MemberRequestModel memberRequestModel, @MappingTarget Member member);
}
