package com.agency04.project.model.DTO;

import java.util.List;

public class HeistDTO {

    private String name;

    private String location;

    private String startTime;

    private String endTime;

    private List<RequirementSkillDTO> skills;

    private String status = "PLANING";

    public HeistDTO() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<RequirementSkillDTO> getSkills() {
        return skills;
    }

    public void setSkills(List<RequirementSkillDTO> skills) {
        this.skills = skills;
    }
}
