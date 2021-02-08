package com.agency04.project.service;

import com.agency04.project.api.HelperController;
import com.agency04.project.model.DTO.HeistMemberDTO;
import com.agency04.project.model.HeistMember;
import com.agency04.project.model.Skill;
import com.agency04.project.repository.HeistMemberRepository;
import com.agency04.project.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HeistMemberService {

    @Autowired
    private HeistMemberRepository heistMemberRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private SkillService skillService;

    @Autowired
    private MailSendingService mailSendingService;

    public List<HeistMember> getAllHeistMembers() {
        return heistMemberRepository.findAll();
    }

    public void addHeistMember(HeistMember heistMember) {
        heistMemberRepository.save(heistMember);
    }

    public Long addHeistMember(HeistMemberDTO heistMemberDTO) {
        HeistMember heistMember = HelperController.fromDTOtoModel(heistMemberDTO);

        for (Skill elem : heistMember.getSkills()) {
            skillService.addSkill(elem);
        }

        addHeistMember(heistMember);

        mailSendingService.sendEmail(heistMemberDTO.getEmail(), "u have been added as member!");

        return heistMember.getId();
    }

    public HeistMember getHeistMember(Long id) {
        return heistMemberRepository.getOne(id);
    }

    public boolean existsHeistMember(Long id) {
        return heistMemberRepository.existsById(id);
    }

    public void updateHeistMemberMainSkill(HeistMemberDTO heistMemberDTO, Long id) {
        HeistMember member = heistMemberRepository.getOne(id);
        member.setMainSkill(heistMemberDTO.getMainSkill());
        addHeistMember(member);
    }

    public HeistMember findMembersByTheirName(String name) {

        return heistMemberRepository.findAllMembersWithName(name);
    }

    public void updateHeistMemberSkills(HeistMemberDTO heistMemberDTO, Long id) {
        List<Skill> skillsMember = skillRepository.findAllSkills(id);
        List<Skill> skillsUpdated = heistMemberDTO.getSkills().stream().map(HelperController::skillDTOtoSkill).collect(Collectors.toList());


        if (HelperController.checkForSameNameSkills(skillsUpdated)) {
            throw new IllegalArgumentException();
        }

        if (skillsUpdated.stream().noneMatch(p -> p.getName().equals(heistMemberDTO.getMainSkill()))) {
            if (skillsMember.stream().noneMatch(p -> p.getName().equals(heistMemberDTO.getMainSkill()))) {
                throw new IllegalArgumentException();
            }
        }

        boolean equalSkill = false;

        for (int i = 0; i < skillsUpdated.size(); i++) {
            equalSkill = false;
            for (int j = 0; j < skillsMember.size(); j++) {
                Skill skill = skillsUpdated.get(i);
                Skill oldSkill = skillsMember.get(j);
                if (skill.getName().equals(oldSkill.getName())) {
                    oldSkill.setLevel(skill.getLevel());
                    skillRepository.save(oldSkill);
                    equalSkill = true;
                }
            }
            if (!equalSkill) {
                Skill newSkill = skillsUpdated.get(i);
                newSkill.setHeistMember(heistMemberRepository.getOne(id));
                skillRepository.save(newSkill);
            }
        }

    }
}
