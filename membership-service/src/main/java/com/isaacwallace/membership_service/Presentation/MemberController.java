package com.isaacwallace.membership_service.Presentation;

import com.isaacwallace.membership_service.Business.MemberService;
import com.isaacwallace.membership_service.Presentation.Models.MemberRequestModel;
import com.isaacwallace.membership_service.Presentation.Models.MemberResponseModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/members")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("")
    public ResponseEntity<List<MemberResponseModel>> getAllMembers() {
        return ResponseEntity.status(HttpStatus.OK).body(this.memberService.getAllMembers());
    }

    @GetMapping("{memberid}")
    public ResponseEntity<MemberResponseModel> getMemberById(@PathVariable String memberid) {
        return ResponseEntity.status(HttpStatus.OK).body(this.memberService.getMemberById(memberid));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponseModel> addMember(@RequestBody MemberRequestModel memberRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.memberService.addMember(memberRequestModel));
    }

    @PutMapping(value = "{memberid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberResponseModel> EditCustomer(@PathVariable String memberid, @RequestBody MemberRequestModel memberRequestModel) {
        return ResponseEntity.status(HttpStatus.OK).body(this.memberService.updateMember(memberid, memberRequestModel));
    }

    @DeleteMapping("{memberid}")
    public ResponseEntity<MemberResponseModel> DeleteCustomer(@PathVariable String memberid) {
        this.memberService.deleteMember(memberid);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
