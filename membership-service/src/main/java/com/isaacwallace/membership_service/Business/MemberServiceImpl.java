package com.isaacwallace.membership_service.Business;

import com.isaacwallace.membership_service.DataAccess.Member;
import com.isaacwallace.membership_service.DataAccess.MemberIdentifier;
import com.isaacwallace.membership_service.DataAccess.MemberRepository;
import com.isaacwallace.membership_service.Mapper.MemberRequestMapper;
import com.isaacwallace.membership_service.Mapper.MemberResponseMapper;
import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;
import com.isaacwallace.membership_service.Utils.Exceptions.InUseException;
import com.isaacwallace.membership_service.Utils.Exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final MemberResponseMapper memberResponseMapper;
    private final MemberRequestMapper memberRequestMapper;

    public MemberServiceImpl(MemberRepository memberRepository, MemberResponseMapper memberResponseMapper, MemberRequestMapper memberRequestMapper) {
        this.memberRepository = memberRepository;
        this.memberResponseMapper = memberResponseMapper;
        this.memberRequestMapper = memberRequestMapper;
    }

    public List<MemberResponseModel> getAllMembers() {
        return this.memberResponseMapper.entityToResponseModelList(this.memberRepository.findAll());
    }

    public MemberResponseModel getMemberById(String memberid) {
        Member member = this.memberRepository.findMemberByMemberIdentifier_Memberid(memberid);

        if (member == null) {
            throw new NotFoundException("Unknown memberid: " + memberid);
        }

        return this.memberResponseMapper.entityToResponseModel(member);
    }

    public MemberResponseModel addMember(MemberRequestModel memberRequestModel) {
        Member member = this.memberRequestMapper.requestModelToEntity(memberRequestModel, new MemberIdentifier());

        return this.memberResponseMapper.entityToResponseModel(this.memberRepository.save(member));
    }

    public MemberResponseModel updateMember(String memberid, MemberRequestModel memberRequestModel) {
        Member member = this.memberRepository.findMemberByMemberIdentifier_Memberid(memberid);

        if (member == null) {
            throw new NotFoundException("Unknown memberid: " + memberid);
        }

        this.memberRequestMapper.updateEntityFromRequest(memberRequestModel, member);

        Member updatedMember = this.memberRepository.save(member);

        log.info("Updated member with memberid: " + memberid);

        return this.memberResponseMapper.entityToResponseModel(updatedMember);
    }

    public void deleteMember(String memberid) {
        Member member = this.memberRepository.findMemberByMemberIdentifier_Memberid(memberid);

        if (member == null) {
            throw new NotFoundException("Unknown memberid: " + memberid);
        }

        try {
            this.memberRepository.delete(member);
            log.info("Member with id: " + memberid + " has been deleted");
        } catch (DataIntegrityViolationException exception) {
            log.error("Failed to delete member with id: " + memberid, exception.getMessage());
            throw new InUseException("Member with id: " + memberid + " is already in use by another entity, currently cannot delete.");
        }
    }
}
