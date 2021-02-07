package com.agency04.project.model;



import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "HeistMember", uniqueConstraints = { @UniqueConstraint(name = "member_email_unique",columnNames = "email") })
public class HeistMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String sex;

    private String email;

    @OneToMany
    @JoinColumn(name = "heist_member_id",referencedColumnName = "id")
    private List<Skill> skills;


    @ManyToMany(mappedBy = "members")
    private List<Heist> heist;

    private String mainSkill;

    @Enumerated(EnumType.STRING)
    private RobberStatus status;

    public HeistMember(){

    }

    public HeistMember(String name, String sex, String email,List<Skill> skills,String mainSkill, RobberStatus status) {
        this.name = name;
        this.sex = sex;
        this.email = email;
        this.skills = skills;
        this.mainSkill = mainSkill;
        this.status = status;
    }


    public void updateHeist(Heist heist){
        this.heist.add(heist);
    }

    public List<Heist> getHeist() {
        return heist;
    }

    public void setHeist(List<Heist> heist) {
        this.heist = heist;
    }

    public Long getId(){
        return this.id;
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

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public String getMainSkill() {
        return mainSkill;
    }

    public void setMainSkill(String mainSkill) {
        this.mainSkill = mainSkill;
    }

    public RobberStatus getStatus() {
        return status;
    }

    public void setStatus(RobberStatus status) {
        this.status = status;
    }
}
