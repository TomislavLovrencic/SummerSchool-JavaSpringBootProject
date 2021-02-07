package com.agency04.project.model.DTO;

import java.util.List;

public class SkillsDTO {

    private List<SkillDTO> skills;

    private String mainSkill;

    public SkillsDTO(){
    }

    public List<SkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<SkillDTO> skills) {
        this.skills = skills;
    }

    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
    }
}
