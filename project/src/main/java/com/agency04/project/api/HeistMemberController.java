package com.agency04.project.api;


import com.agency04.project.model.*;
import com.agency04.project.model.DTO.HeistMemberDTO;
import com.agency04.project.model.DTO.SkillsDTO;
import com.agency04.project.service.HeistMemberService;
import com.agency04.project.service.MailSendingService;
import com.agency04.project.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HeistMemberController {

    @Autowired
    private HeistMemberService heistMemberService;

    @Autowired
    private SkillService skillService;

    @Autowired
    private MailSendingService mailSendingService;

    @PostMapping("/member")
    public ResponseEntity<Void> addHeistMember(@RequestBody HeistMemberDTO heistMemberDTO){
        URI location = null;

        try {
            Long id  = heistMemberService.addHeistMember(heistMemberDTO);

            location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(id)
                    .toUri();

        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);


        return new ResponseEntity<Void>(headers,HttpStatus.CREATED);

    }

    @PutMapping("/member/{id}/skills")
    public ResponseEntity<Void> updateMemberSkills(@PathVariable Long id,@RequestBody HeistMemberDTO heistMemberDTO){

        if(heistMemberDTO.getMainSkill() == null && heistMemberDTO.getSkills() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(!heistMemberService.existsHeistMember(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try{
            if(heistMemberDTO.getSkills() == null){
                heistMemberService.updateHeistMemberMainSkill(heistMemberDTO,id);
            }
            else if(heistMemberDTO.getMainSkill() == null){
                heistMemberService.updateHeistMemberSkills(heistMemberDTO,id);
            }
            else{
                heistMemberService.updateHeistMemberMainSkill(heistMemberDTO,id);
                heistMemberService.updateHeistMemberSkills(heistMemberDTO,id);
            }
        } catch (RuntimeException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand(id)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<Void>(headers,HttpStatus.NO_CONTENT);

    }

    @DeleteMapping("/member/{id}/skills/{name}")
    public ResponseEntity<Void> removeHeistMemberSkill(@PathVariable Long id, @PathVariable String name){

        if(!heistMemberService.existsHeistMember(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Skill> skillsMember = skillService.findAllSkills(id);

        if(skillsMember.stream().noneMatch(p -> p.getName().equals(name))){
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        skillService.removeSkill(skillsMember,name,id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/member/{id}")
    public ResponseEntity<HeistMemberDTO> getHeistMember(@PathVariable Long id){

        if(!heistMemberService.existsHeistMember(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        HeistMemberDTO heistMemberDTO = HelperController.heistMemberToDTO(heistMemberService.getHeistMember(id));

        return new ResponseEntity<>(heistMemberDTO,HttpStatus.OK);

    }


    @GetMapping("/member/{id}/skills")
    public ResponseEntity<SkillsDTO> getHeistMemberSkills(@PathVariable Long id){
        if(!heistMemberService.existsHeistMember(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HeistMember heistMember = heistMemberService.getHeistMember(id);

        SkillsDTO skillsDTO = new SkillsDTO();
        skillsDTO.setSkills(heistMember.getSkills().stream().map(HelperController::skillToSkillDTO).collect(Collectors.toList()));
        skillsDTO.setMainSkill(heistMember.getMainSkill());

        return new ResponseEntity<>(skillsDTO,HttpStatus.OK);

    }

}
