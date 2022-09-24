package com.edu.ulab.app.dto;

import lombok.Data;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
public class UserDto {
    private Long id;
    private String fullName;
    private String title;
    private int age;
    private Set<BookDto> books = new CopyOnWriteArraySet<>();
}
