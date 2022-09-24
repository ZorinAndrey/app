package com.edu.ulab.app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Person person;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "page_count")
    private long pageCount;

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", pageCount=" + pageCount +
                '}';
    }
}
