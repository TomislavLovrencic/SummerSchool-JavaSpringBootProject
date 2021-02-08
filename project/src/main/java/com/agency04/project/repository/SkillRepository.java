package com.agency04.project.repository;

import com.agency04.project.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("skill")
public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Query(value = "SELECT * FROM Skill WHERE heist_member_id = ?1", nativeQuery = true)
    public List<Skill> findAllSkills(Long id);
}
