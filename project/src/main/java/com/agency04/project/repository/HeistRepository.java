package com.agency04.project.repository;

import com.agency04.project.model.Heist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("heist")
public interface HeistRepository extends JpaRepository<Heist, Long> {


}
