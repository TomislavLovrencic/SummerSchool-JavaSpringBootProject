package com.agency04.project.model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import javax.persistence.*;

@Entity
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String level = "*";

    @ManyToOne
    private HeistMember heistMember;

    public Skill() {

    }

    public Skill(String name, String level) {
        this.name = name;
        this.level = level;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public HeistMember getHeistMember() {
        return heistMember;
    }

    public void setHeistMember(HeistMember heistMember) {
        this.heistMember = heistMember;
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

    @Override
    public String toString() {
        return "Skill{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", level='" + level + '\'' +
                ", heistMember=" + heistMember +
                '}';
    }
}
