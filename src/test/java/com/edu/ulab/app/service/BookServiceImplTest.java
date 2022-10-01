package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserServiceImpl userService;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto, userService)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given

        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("title");

        Book book = new Book();
        book.setId(1L);
        book.setPerson(person);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setTitle("new title");
        bookDto.setAuthor("new author");
        bookDto.setPageCount(999);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setPerson(person);
        updatedBook.setTitle("new title");
        updatedBook.setAuthor("new author");
        updatedBook.setPageCount(999);

        //when
        when(bookRepository.findById(
                bookDto.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(bookDto);
        when(bookMapper.userIdToPerson(1L, userService)).thenReturn(person);

        BookDto bookDtoResult = bookService.updateBook(bookDto);

        //then
        assertEquals(1L, bookDtoResult.getId());
        assertEquals(1L, bookDtoResult.getUserId());
        assertEquals(999, bookDtoResult.getPageCount());
        assertEquals("new title", bookDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение книги по идентификатору. Должно пройти успешно.")
    void getBookById_Test() {
        //given
        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        Book book = new Book();
        book.setId(1L);
        book.setAuthor("test author");
        book.setPageCount(999);
        book.setTitle("test title");
        book.setPerson(person);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setAuthor("test author");
        bookDto.setPageCount(999);
        bookDto.setTitle("test title");
        bookDto.setUserId(person.getId());

        //when
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        BookDto resultBook = bookService.getBookById(1L);

        //then
        assertEquals(1L, resultBook.getId());
        assertEquals("test author", resultBook.getAuthor());
        assertEquals("test title", resultBook.getTitle());
        assertEquals(1L, resultBook.getUserId());
    }

    @Test
    @DisplayName("Получение всех книг юзера по идентификатору юзера. Должно пройти успешно.")
    void getAllBooksByUserId_Test() {
        //given
        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setAuthor("first author");
        firstBook.setPageCount(999);
        firstBook.setTitle("first title");
        firstBook.setPerson(person);

        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setAuthor("second author");
        secondBook.setPageCount(1000);
        secondBook.setTitle("second title");
        secondBook.setPerson(person);

        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setAuthor("first author");
        firstBookDto.setPageCount(999);
        firstBookDto.setTitle("first title");
        firstBookDto.setUserId(person.getId());

        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setAuthor("second author");
        secondBookDto.setPageCount(1000);
        secondBookDto.setTitle("second title");
        secondBookDto.setUserId(person.getId());

        //when
        when(bookRepository.findAllByPersonId(1L)).thenReturn(Set.of(firstBook, secondBook));
        when(bookMapper.bookToBookDto(firstBook)).thenReturn(firstBookDto);
        when(bookMapper.bookToBookDto(secondBook)).thenReturn(secondBookDto);

        Set<BookDto> bookDtoSet = bookService.getAllBooksByUserId(1L);

        //then
        assertEquals(2, bookDtoSet.size());
        assertEquals("first author", bookDtoSet.stream()
                .filter(bookDto -> bookDto.getId().equals(1L))
                .findFirst().orElse(new BookDto()).getAuthor());
        assertEquals(1L, bookDtoSet.stream().findFirst()
                .orElse(new BookDto()).getUserId());
    }

    @Test
    @DisplayName("Получение всех книг по коллекции идентификаторов. Должно пройти успешно.")
    void getAllBooksByIdSet_Test() {
        //given
        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setAuthor("first author");
        firstBook.setPageCount(999);
        firstBook.setTitle("first title");
        firstBook.setPerson(person);

        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setAuthor("second author");
        secondBook.setPageCount(1000);
        secondBook.setTitle("second title");
        secondBook.setPerson(person);

        BookDto firstBookDto = new BookDto();
        firstBookDto.setId(1L);
        firstBookDto.setAuthor("first author");
        firstBookDto.setPageCount(999);
        firstBookDto.setTitle("first title");
        firstBookDto.setUserId(person.getId());

        BookDto secondBookDto = new BookDto();
        secondBookDto.setId(2L);
        secondBookDto.setAuthor("second author");
        secondBookDto.setPageCount(1000);
        secondBookDto.setTitle("second title");
        secondBookDto.setUserId(person.getId());

        //when
        when(bookRepository.findAllById(Set.of(1L, 2L))).thenReturn(Set.of(firstBook, secondBook));
        when(bookMapper.bookToBookDto(firstBook)).thenReturn(firstBookDto);
        when(bookMapper.bookToBookDto(secondBook)).thenReturn(secondBookDto);

        Set<BookDto> bookDtoSet = bookService.getAllBooksByIdSet(Set.of(1L, 2L));

        //then
        assertEquals(2, bookDtoSet.size());
        assertEquals("first author", bookDtoSet.stream()
                .filter(bookDto -> bookDto.getId().equals(1L))
                .findFirst().orElse(new BookDto()).getAuthor());
        assertEquals(1L, bookDtoSet.stream().findFirst()
                .orElse(new BookDto()).getUserId());
    }

    @Test
    @DisplayName("Удаление книги по идентификатору. Должно пройти успешно.")
    void deleteBookById_Test() {
        //given
        Long bookId = 1L;

        try {
            //when
            bookService.deleteBookById(bookId);
            //then
            assertTrue(true);
        } catch (Exception e) {
            //or then
            fail();
        }
    }

    @Test
    @DisplayName("Удаление книги по несуществующему идентификатору. Должно пройти успешно.")
    void deleteBookByIdWithWrongId_Test() {
        //given
        Long bookId = 0L;

        //when
        doThrow(new EmptyResultDataAccessException("No entity with id 0 exists!", 1)).when(bookRepository).deleteById(bookId);

        //then
        assertThatThrownBy(() -> bookService.deleteBookById(bookId))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("No entity with id 0 exists!");
    }
}
