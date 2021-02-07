package com.agency04.project.service;

import com.agency04.project.api.HelperController;
import com.agency04.project.model.*;
import com.agency04.project.model.DTO.*;
import com.agency04.project.repository.HeistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeistService {

    @Autowired
    private HeistRepository heistRepository;

    @Autowired
    private RequirementSkillService requirementSkillService;

    @Autowired
    private HeistMemberService heistMemberService;

    @Autowired
    private MailSendingService mailSendingService;

    @Autowired
    private ScheduledTasksService scheduledTasksService;


    public void addHeist(Heist heist){
        heistRepository.save(heist);
    }

    public Long addHeist(HeistDTO heistDTO) throws ParseException {
        Heist heist = HelperController.heistDTOtoHeist(heistDTO);

        Date startTime = HelperController.parseISOtimeToDate(heist.getStartTime());
        Date endTime = HelperController.parseISOtimeToDate(heist.getEndTime());

        if((startTime.compareTo(endTime) > 0)) {
            throw new IllegalArgumentException();
        }

        if(HelperController.checkForSameNameAndLevelSkills(heist.getSkills())){
            throw new IllegalArgumentException();
        }

        for(RequirementSkill elem : heist.getSkills()){
            requirementSkillService.addRequirementSkill(elem);
        }

        heistRepository.save(heist);

        scheduledTasksService.updateStartAndEndHeist(heist);

        return heist.getId();
    }

    public void getHeistOutcome(Long id){
        Heist heist = getHeist(id);

        List<RequirementSkill> requirementSkills = heist.getSkills();

        int numberOfMembers = heist.getMembers().size();
        int numberOfReqMembers = 0;
        for(RequirementSkill skill : requirementSkills){
            numberOfReqMembers+=skill.getMembers();
        }

        double percentage = ((double) numberOfMembers/numberOfReqMembers) * 100;


        if(percentage < 50.0){
            heist.setHeistOutcome(HeistOutcome.FAILED);
            for(HeistMember member : heist.getMembers()){
                int tmp = (int) ( Math.random() * 2 + 1);
                if(tmp == 1){
                    member.setStatus(RobberStatus.EXPIRED);
                }
                else{
                    member.setStatus(RobberStatus.INCARCERATED);
                }
                heistMemberService.addHeistMember(member);
            }
            addHeist(heist);
        }
        else if(percentage>= 50.0 && percentage < 75.0){
            int tmp = (int) ( Math.random() * 2 + 1);
            int x = 0;
            if(tmp == 1){
                heist.setHeistOutcome(HeistOutcome.FAILED);
                x = numberOfMembers * 2 / 3;
            }
            else{
                heist.setHeistOutcome(HeistOutcome.SUCCEEDED);
                x = numberOfMembers / 3;
            }
            addHeist(heist);

            List<HeistMember> randomArray = HelperController.getRandomArray(x,heist.getMembers());
            for(HeistMember member : randomArray){
                int tmp2 = (int) ( Math.random() * 2 + 1);
                if(tmp2 == 1){
                    member.setStatus(RobberStatus.EXPIRED);
                }
                else{
                    member.setStatus(RobberStatus.INCARCERATED);
                }
                heistMemberService.addHeistMember(member);
            }
        }
        else if(percentage >= 75.0 && percentage < 100.0){
            int x = numberOfMembers / 3;
            heist.setHeistOutcome(HeistOutcome.SUCCEEDED);
            List<HeistMember> randomArray = HelperController.getRandomArray(x,heist.getMembers());
            for(HeistMember member : randomArray){
                member.setStatus(RobberStatus.INCARCERATED);
                heistMemberService.addHeistMember(member);
            }
            addHeist(heist);
        }
        else{
            heist.setHeistOutcome(HeistOutcome.SUCCEEDED);
            addHeist(heist);
        }
    }

    public List<Heist> findAllHeists(){
        return heistRepository.findAll();
    }

    public void updateRequiredSkills(HeistDTO heistDTO,Long id){
        List<RequirementSkill> skills = heistDTO.getSkills().stream().map(HelperController::DTOtoRequirementSkill).collect(Collectors.toList());

        if(HelperController.checkForSameNameAndLevelSkills(skills)){
            throw new IllegalArgumentException();
        }

        // TODO - CHECK IF THE HEIST HAS ALREADY STARTED !!!! SBSS-08

        Heist heist = getHeist(id);

        List<RequirementSkill> oldSkills = requirementSkillService.findAllSkills(id);

        boolean equalSkill = false;

        for(int i=0;i<skills.size();i++){
            equalSkill = false;
            RequirementSkill newSkill = skills.get(i);
            for(int j=0;j<oldSkills.size();j++){
                RequirementSkill oldSkill = oldSkills.get(j);
                if(newSkill.getName().equals(oldSkill.getName()) && newSkill.getLevel().equals(oldSkill.getLevel())){
                    if(newSkill.getMembers() == oldSkill.getMembers()){
                        throw new IllegalArgumentException();
                    }
                    oldSkill.setMembers(newSkill.getMembers());
                    requirementSkillService.addRequirementSkill(oldSkill);
                    equalSkill = true;
                }
            }
            if(!equalSkill){
                newSkill.setHeist(heist);
                requirementSkillService.addRequirementSkill(newSkill);
            }
        }
    }

    public Heist getHeist(Long id){
       return heistRepository.getOne(id);
    }

    public EligibleMembersDTO createEligibleMembers(Long id){
        List<HeistMember> members = heistMemberService.getAllHeistMembers();

        List<HeistMember> eligibleMembers = new ArrayList<>();

        Heist heist = getHeist(id);

        List<RequirementSkill> reqSkills = heist.getSkills();

        // TODO - SBSS-08 OVDJE UVJET TREBA DODAT !!!!!!!!!!!!

        for(HeistMember member : members){
            if(member.getStatus().equals(RobberStatus.AVAILABLE) || member.getStatus().equals(RobberStatus.RETIRED)){
                for(RequirementSkill skill : reqSkills){
                    for(Skill memberSkill : member.getSkills()){
                        if(memberSkill.getName().equals(skill.getName())){
                            if(memberSkill.getLevel().length() >= skill.getLevel().length()){
                                if(!eligibleMembers.contains(member)){
                                    eligibleMembers.add(member);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        List<RequirementSkillDTO> requirementsSkills = reqSkills.stream().map(HelperController::requirementSkillToDTO).collect(Collectors.toList());
        List<HeistMemberDTO> eligibleHeistMembers = eligibleMembers.stream().map(HelperController::heistMemberToDTO).collect(Collectors.toList());


        EligibleMembersDTO eligibleMembersDTO = new EligibleMembersDTO();
        eligibleMembersDTO.setMembers(eligibleHeistMembers);
        eligibleMembersDTO.setSkills(requirementsSkills);

        return eligibleMembersDTO;
    }

    public void confirmMembersForHeist(MembersDTO membersDTO, Long id) throws ParseException {
            Heist heist = heistRepository.getOne(id);
            List<String> membersString = membersDTO.getMembers();
            List<HeistMember> members = new ArrayList<>();

            for(String elem : membersString){
                HeistMember heistMember = heistMemberService.findMembersByTheirName(elem);
                if(heistMember == null){
                    throw new IllegalArgumentException();
                }
                members.add(heistMember);
            }

            for(HeistMember heistMember : members){

                if(!(heistMember.getStatus().equals(RobberStatus.AVAILABLE) || heistMember.getStatus().equals(RobberStatus.RETIRED)) ){
                    throw new IllegalArgumentException();
                }
                for(Heist onGoingHeist : heistMember.getHeist()){
                    Date startTimeHeistDto = HelperController.parseISOtimeToDate(onGoingHeist.getStartTime());
                    Date startTimeHeist = HelperController.parseISOtimeToDate(heist.getStartTime());
                    Date endTimeHeistDto = HelperController.parseISOtimeToDate(onGoingHeist.getEndTime());
                    Date endTimeHeist = HelperController.parseISOtimeToDate(heist.getEndTime());
                    if(startTimeHeist.compareTo(startTimeHeistDto) >= 0 && startTimeHeist.compareTo(endTimeHeistDto) <= 0){
                        throw new IllegalArgumentException();
                    }
                    if(endTimeHeist.compareTo(endTimeHeistDto) <= 0 && endTimeHeist.compareTo(startTimeHeistDto) >= 0){
                        throw new IllegalArgumentException();
                    }
                    if(startTimeHeist.compareTo(startTimeHeistDto) < 0 && endTimeHeist.compareTo(endTimeHeistDto) > 0){
                        throw new IllegalArgumentException();
                    }
                }
            }

            mailSendingService.notifyMembers(heist,"U have been confirmed to participate in a heist!");

            heist.setMembers(members);
            heist.setHeistStatus(HeistStatus.READY);
            heistRepository.save(heist);

    }

    public boolean heistExists(Long id){
        return heistRepository.existsById(id);
    }
}
