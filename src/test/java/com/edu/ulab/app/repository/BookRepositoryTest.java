package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        //Given
        Book book = bookRepository.findById(2002L)
                .orElseThrow(() -> new NotFoundException("Test book not found"));
        book.setAuthor("New author");

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(5500);
        assertThat(result.getTitle()).isEqualTo("default book");
        assertThat(result.getAuthor()).isEqualTo("New author");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Получить книгу с юзером. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {
        //Given
        Book book;

        //When
        book = bookRepository.findById(2002L)
                .orElseThrow(() -> new NotFoundException("Test book not found"));

        //Then
        assertThat(book.getPageCount()).isEqualTo(5500);
        assertThat(book.getTitle()).isEqualTo("default book");
        assertThat(book.getAuthor()).isEqualTo("author");
        assertThat(book.getPerson().getFullName()).isEqualTo("default user");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Получить все книги с юзерами. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBooks_thenAssertDmlCount() {
        //Given
        List<Book> bookList;

        //When
        bookList = (List<Book>) bookRepository.findAll();

        //Then
        assertThat(bookList.size()).isEqualTo(2);
        assertThat(bookList.get(0).getAuthor()).isEqualTo("author");
        assertThat(bookList.get(0).getPerson().getFullName()).isEqualTo("default user");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {
        //Given
        Long bookId = 2002L;

        //When
        bookRepository.deleteById(bookId);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу с несуществующим идентификатором." +
            " Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBookWithWrongId_thenAssertDmlCount() {
        //Given
        Long bookId = 8000L;

        try {
            //When
            bookRepository.deleteById(bookId);
        } catch (Exception e) {
            //Then
            assertThat(e.getClass()).isEqualTo(EmptyResultDataAccessException.class);
            assertSelectCount(1);
            assertInsertCount(0);
            assertUpdateCount(0);
            assertDeleteCount(0);
        }
    }
}
