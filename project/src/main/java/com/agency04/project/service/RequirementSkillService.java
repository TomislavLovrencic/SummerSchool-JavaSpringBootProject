package com.agency04.project.service;

import com.agency04.project.model.RequirementSkill;
import com.agency04.project.repository.RequirementSkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequirementSkillService {

    @Autowired
    private RequirementSkillRepository requirementSkillRepository;

    public void addRequirementSkill(RequirementSkill requirementSkill) {
        requirementSkillRepository.save(requirementSkill);
    }

    public List<RequirementSkill> findAllSkills(Long id) {
        return requirementSkillRepository.findAllSkills(id);
    }
}
