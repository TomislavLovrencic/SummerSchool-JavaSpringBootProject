package com.agency04.project.repository;

import com.agency04.project.model.HeistMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("heist_member")
public interface HeistMemberRepository extends JpaRepository<HeistMember,Long> {

    @Query(value = "SELECT * FROM heist_member WHERE name = ?1",nativeQuery = true)
    public HeistMember findAllMembersWithName(String name);

}
