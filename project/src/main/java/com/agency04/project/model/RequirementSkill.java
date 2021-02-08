package com.agency04.project.model;

import javax.persistence.*;

@Entity
public class RequirementSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String level;

    private int members;

    @ManyToOne
    private Heist heist;

    public RequirementSkill() {

    }

    public RequirementSkill(String name, String level, int members) {
        this.name = name;
        this.level = level;
        this.members = members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Heist getHeist() {
        return heist;
    }

    public void setHeist(Heist heist) {
        this.heist = heist;
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

    public int getMembers() {
        return members;
    }

    public void setMembers(int members) {
        this.members = members;
    }
}
