package com.agency04.project.model.DTO;

import java.util.List;

public class HeistMemberDTO {

    private String name;

    private String sex;

    private String email;

    private List<SkillDTO> skills;

    private String mainSkill;

    private String status;

    public HeistMemberDTO() {

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HeistMemberDTO{" +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", skills=" + skills +
                ", mainSkill='" + mainSkill + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
