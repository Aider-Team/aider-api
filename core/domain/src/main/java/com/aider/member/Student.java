package com.aider.member;

import com.aider.domain.common.MutableBaseEntity;
import com.aider.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phoneNum")
})
public class Student extends MutableBaseEntity {
    @Id @GeneratedValue
    @Column(name = "student_id")
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birth;

    private String phoneNum;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;
}
