package aur.diploma.backend.parent.entity;

import aur.diploma.backend.student.entity.Student;
import aur.diploma.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Профиль родителя.
 * Один пользователь с ролью PARENT имеет ровно одну запись в этой таблице,
 * которая привязывает его к конкретному ребёнку (Student).
 */
@Entity
@Table(name = "parents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Аккаунт в системе (роль PARENT). */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    /** Ребёнок, за которым закреплён родитель. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String firstName;

    private String middleName;

    /** Контактный номер телефона родителя. */
    private String phone;
}
