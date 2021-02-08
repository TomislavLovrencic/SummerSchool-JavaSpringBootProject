package com.agency04.project.model;

import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.TimeZone;

@Entity
@Table(
        name = "Heist",
        uniqueConstraints = {
                @UniqueConstraint(name = "heist_name_unique", columnNames = "name")
        }
)
public class Heist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String location;

    private String startTime;

    private String endTime;

    @Enumerated(value = EnumType.STRING)
    private HeistOutcome heistOutcome = HeistOutcome.NOT_FINISHED;

    @Enumerated(value = EnumType.STRING)
    private HeistStatus status = HeistStatus.PLANING;

    @ManyToMany
    @JoinTable(
            name = "heist_members",
            joinColumns = @JoinColumn(name = "heist_id"),
            inverseJoinColumns = @JoinColumn(name = "members_id"))
    private List<HeistMember> members;

    @OneToMany
    @JoinColumn(name = "heist_id", referencedColumnName = "id")
    private List<RequirementSkill> skills;

    public Heist() {

    }

    public Heist(String name, String location, String startTime, String endTime, List<RequirementSkill> skills, HeistStatus status) {
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.skills = skills;
        this.status = status;
    }


    public HeistOutcome getHeistOutcome() {
        return heistOutcome;
    }

    public void setHeistOutcome(HeistOutcome heistOutcome) {
        this.heistOutcome = heistOutcome;
    }

    public HeistStatus getHeistStatus() {
        return status;
    }

    public void setHeistStatus(HeistStatus heistStatus) {
        this.status = heistStatus;
    }

    public List<HeistMember> getMembers() {
        return members;
    }

    public void setMembers(List<HeistMember> members) {
        this.members = members;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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

    public List<RequirementSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<RequirementSkill> skills) {
        this.skills = skills;
    }

    public String toIso(String time) {
        LocalDate date = LocalDate.parse(time);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        return df.format(date);
    }
}
