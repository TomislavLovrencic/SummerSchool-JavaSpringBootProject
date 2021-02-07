package com.agency04.project.repository;

import com.agency04.project.model.RequirementSkill;
import com.agency04.project.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("requirement_skill")
public interface RequirementSkillRepository extends JpaRepository<RequirementSkill,Long> {

    @Query(value = "SELECT * FROM Requirement_Skill WHERE heist_id = ?1",nativeQuery = true)
    public List<RequirementSkill> findAllSkills(Long id);
}
