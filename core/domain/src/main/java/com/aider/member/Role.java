package com.aider.member;

import javax.persistence.*;

import lombok.Getter;

@Getter
@Table(name = "roles")
@Entity
public class Role {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "student_id")
	private Student student;
}
