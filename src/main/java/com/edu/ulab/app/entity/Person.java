package com.edu.ulab.app.entity;


import lombok.Data;

import javax.persistence.*;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


@Entity
@Data
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "title")
    private String title;
    @Column(name = "age")
    private int age;

    @OneToMany(mappedBy = "person")
    private Set<Book> books = new CopyOnWriteArraySet<>();
}
