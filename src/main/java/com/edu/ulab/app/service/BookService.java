package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.Set;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(Long id);

    Set<BookDto> getAllBooksByUserId(Long userId);

    Set<BookDto> getAllBooksByIdSet(Set<Long> idSet);

    void deleteBookById(Long id);
}
