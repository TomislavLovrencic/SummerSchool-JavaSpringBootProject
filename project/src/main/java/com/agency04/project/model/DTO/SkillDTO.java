package com.agency04.project.model.DTO;

import com.agency04.project.model.HeistMember;

public class SkillDTO {

    private String name;

    private String level;

    public SkillDTO(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
