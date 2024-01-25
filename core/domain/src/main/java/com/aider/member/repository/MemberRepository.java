package com.aider.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aider.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
