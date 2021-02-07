package com.agency04.project.service;

import com.agency04.project.api.HelperController;
import com.agency04.project.model.DTO.SkillDTO;
import com.agency04.project.model.HeistMember;
import com.agency04.project.model.Skill;
import com.agency04.project.repository.HeistMemberRepository;
import com.agency04.project.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private HeistMemberRepository heistMemberRepository;

    public void addSkill(Skill skill){
        skillRepository.save(skill);
    }

    public void addSkill(SkillDTO skillDTO){
        Skill skill = HelperController.skillDTOtoSkill(skillDTO);
        skillRepository.save(skill);
    }

    public List<Skill> findAllSkills(Long id){
        return skillRepository.findAllSkills(id);
    }

    public Skill getSkill(Long id){
        return skillRepository.getOne(id);
    }

    public void updateSkill(Skill newSkill){
        skillRepository.save(newSkill);
    }

    public void removeSkill(List<Skill> skillsMember,String name,Long id){

        Long idToRemove = 0l;
        for(Skill elem : skillsMember){
            if(elem.getName().equals(name)){
                idToRemove = elem.getId();
            }
        }
        skillRepository.delete(skillRepository.getOne(idToRemove));

        HeistMember member = heistMemberRepository.getOne(id);
        if(member.getMainSkill().equals(name)){
            member.setMainSkill("none");
            heistMemberRepository.save(member);
        }
    }






}
