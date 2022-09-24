package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final UserServiceImpl userService;
    private final BookMapper bookMapper;


    @Transactional
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto, userService);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Transactional
    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new NotFoundException(String.format("Book with id: %d not found", bookDto.getId())));
        log.info("Book from DB: {}", book);
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setPageCount(bookDto.getPageCount());
        book.setPerson(bookMapper.userIdToPerson(bookDto.getUserId(), userService));
        Book updatedBook = bookRepository.save(book);
        log.info("Updated book: {}", updatedBook);
        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        if (id == null) {
            throw new NotFoundException(String.format("Book with id: %d not found", id));
        }
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Book with id: %d not found", id)));
        log.info("(Service)Received book: {}", book);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public Set<BookDto> getAllBooksByUserId(Long userId) {
        return bookRepository.findAllByPersonId(userId).stream()
                .map(bookMapper::bookToBookDto).collect(Collectors.toSet());
    }

    @Override
    public Set<BookDto> getAllBooksByIdSet(Set<Long> idSet) {
        Iterable<Book> iterable = bookRepository.findAllById(idSet);
        Set<BookDto> result = new CopyOnWriteArraySet<>();
        if (iterable instanceof List<Book>) {
            return result;
        }
        iterable.forEach(book -> result.add(bookMapper.bookToBookDto(book)));
        return result;
    }

    @Override
    public void deleteBookById(Long id) {
        if (id == null) {
            throw new NotFoundException(String.format("Book with id: %d not found", id));
        }
        bookRepository.deleteById(id);
        log.info("(Service)Removed book with id : {}", id);
    }
}
