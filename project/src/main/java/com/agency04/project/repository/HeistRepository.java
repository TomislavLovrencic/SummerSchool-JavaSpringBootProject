package com.agency04.project.repository;

import com.agency04.project.model.Heist;
import com.agency04.project.model.HeistMember;
import com.agency04.project.model.RequirementSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("heist")
public interface HeistRepository extends JpaRepository<Heist,Long> {


}
