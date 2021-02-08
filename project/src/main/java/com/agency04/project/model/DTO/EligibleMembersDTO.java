package com.agency04.project.model.DTO;

import java.util.List;

public class EligibleMembersDTO {

    private List<RequirementSkillDTO> skills;

    private List<HeistMemberDTO> members;

    public EligibleMembersDTO() {
    }

    public List<RequirementSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<RequirementSkillDTO> skills) {
        this.skills = skills;
    }

    public List<HeistMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<HeistMemberDTO> members) {
        this.members = members;
    }
}
