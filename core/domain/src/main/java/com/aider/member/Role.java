package com.aider.member;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;

@Getter
@Table(name = "roles")
@Entity
public class Role {
	@Id
	@GeneratedValue
	private Long id;

	private String name;
}
