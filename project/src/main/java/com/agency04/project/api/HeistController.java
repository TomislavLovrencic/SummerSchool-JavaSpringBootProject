package com.agency04.project.api;

import com.agency04.project.model.DTO.*;
import com.agency04.project.model.Heist;
import com.agency04.project.model.HeistMember;
import com.agency04.project.model.HeistStatus;
import com.agency04.project.service.HeistMemberService;
import com.agency04.project.service.HeistService;
import com.agency04.project.service.MailSendingService;
import com.agency04.project.service.RequirementSkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HeistController {

    @Autowired
    private HeistService heistService;

    @Autowired
    private RequirementSkillService requirementSkillService;

    @Autowired
    private HeistMemberService heistMemberService;

    @Autowired
    private MailSendingService mailSendingService;


    @PostMapping("/heist")
    public ResponseEntity<Void> addHeist(@RequestBody HeistDTO heistDTO) throws ParseException {

        try {
            Long id = heistService.addHeist(heistDTO);

            URI location = ServletUriComponentsBuilder
                    .fromPath("/heist")
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(location);

            return new ResponseEntity<Void>(headers, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


    @PatchMapping("/heist/{id}/skills")
    public ResponseEntity<Void> updateRequirementSkills(@PathVariable Long id, @RequestBody HeistDTO heistDTO) {

        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (heistService.getHeist(id).getHeistStatus().equals(HeistStatus.IN_PROGRESS)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        try {
            heistService.updateRequiredSkills(heistDTO, id);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        URI location = ServletUriComponentsBuilder
                .fromPath("/heist/{id}/skills")
                .buildAndExpand(id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/heist/{id}/eligible_members")
    public ResponseEntity<EligibleMembersDTO> getEligibleMembers(@PathVariable Long id) throws ParseException {
        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!heistService.getHeist(id).getHeistStatus().equals(HeistStatus.PLANING)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        EligibleMembersDTO eligibleMembersDTO = null;

        eligibleMembersDTO = heistService.createEligibleMembers(id);

        return new ResponseEntity<>(eligibleMembersDTO, HttpStatus.OK);
    }

    @PutMapping("/heist/{id}/members")
    public ResponseEntity<Void> confirmMembersForHeist(@PathVariable Long id, @RequestBody MembersDTO membersDTO) {
        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (!heistService.getHeist(id).getHeistStatus().equals(HeistStatus.PLANING)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        try {
            heistService.confirmMembersForHeist(membersDTO, id);

        } catch (RuntimeException | ParseException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        URI location = ServletUriComponentsBuilder
                .fromPath("/heist/{id}/members")
                .buildAndExpand(id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);


        return new ResponseEntity<Void>(headers, HttpStatus.NO_CONTENT);

    }

    @PutMapping("/heist/{id}/start")
    public ResponseEntity<Void> startTheHeist(@PathVariable Long id) {

        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Heist heist = heistService.getHeist(id);

        if (!heist.getHeistStatus().equals(HeistStatus.READY)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        heist.setHeistStatus(HeistStatus.IN_PROGRESS);
        heist.setStartTime(HelperController.parseDateTimeToISO(new Date()));
        heistService.addHeist(heist);

        mailSendingService.notifyMembers(heist, "The heist has started!");

        URI location = ServletUriComponentsBuilder
                .fromPath("/heist/{id}/status")
                .buildAndExpand(id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<Void>(headers, HttpStatus.OK);

    }

    @GetMapping("/heist/{id}")
    public ResponseEntity<HeistDTO> getHeist(@PathVariable Long id) {
        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HeistDTO heist = HelperController.heistToHeistDTO(heistService.getHeist(id));

        return new ResponseEntity<>(heist, HttpStatus.OK);

    }

    @GetMapping("/heist/{id}/members")
    public ResponseEntity<List<HeistMemberDTO>> getMembersInHeist(@PathVariable Long id) {
        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Heist heist = heistService.getHeist(id);
        if (heist.getHeistStatus().equals(HeistStatus.PLANING)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        List<HeistMember> members = heist.getMembers();
        List<HeistMemberDTO> memberDTOS = members.stream().map(HelperController::heistMemberToDTO).collect(Collectors.toList());

        return new ResponseEntity<>(memberDTOS, HttpStatus.OK);

    }

    @GetMapping("/heist/{id}/skills")
    public ResponseEntity<List<RequirementSkillDTO>> getHeistRequirementSkills(@PathVariable Long id) {
        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HeistDTO heistDTO = HelperController.heistToHeistDTO(heistService.getHeist(id));

        return new ResponseEntity<>(heistDTO.getSkills(), HttpStatus.OK);
    }

    @GetMapping("/heist/{id}/status")
    public ResponseEntity<HeistStatusDTO> getHeistStatus(@PathVariable Long id) {

        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Heist heist = heistService.getHeist(id);

        HeistStatusDTO heistStatusDTO = new HeistStatusDTO();
        heistStatusDTO.setStatus(
                switch (heist.getHeistStatus()) {
                    case READY -> "READY";
                    case PLANING -> "PLANING";
                    case IN_PROGRESS -> "IN_PROGRESS";
                    case FINISHED -> "FINISHED";
                });

        return new ResponseEntity<>(heistStatusDTO, HttpStatus.OK);

    }

    @GetMapping("/heist/{id}/outcome")
    public ResponseEntity<HeistOutcomeDTO> getHeistOutcome(@PathVariable Long id) {

        if (!heistService.heistExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Heist heist = heistService.getHeist(id);

        if (!heist.getHeistStatus().equals(HeistStatus.FINISHED)) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        heistService.getHeistOutcome(id);

        HeistOutcomeDTO heistOutcomeDTO = new HeistOutcomeDTO();
        heistOutcomeDTO.setOutcome(
                switch (heist.getHeistOutcome()) {
                    case FAILED -> "FAILED";
                    case SUCCEEDED -> "SUCCEEDED";
                    case NOT_FINISHED -> "NOT_FINISHED";
                });

        return new ResponseEntity<>(heistOutcomeDTO, HttpStatus.OK);

    }


}
