package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = userRepository.findById(1001L)
                .orElseThrow(() -> new NotFoundException("Test person not found"));
        person.setFullName("New full name");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(55);
        assertThat(result.getFullName()).isEqualTo("New full name");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера и его книги. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPersonWithBooks_thenAssertDmlCount() {
        //Given
        Person person;

        //When
        person = userRepository.findById(1001L)
                .orElseThrow(() -> new NotFoundException("Test person not found"));

        //Then
        assertThat(person.getAge()).isEqualTo(55);
        assertThat(person.getFullName()).isEqualTo("default user");
        assertThat(person.getBooks().size()).isEqualTo(2);
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить всех юзеров. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllPersonsWithBooks_thenAssertDmlCount() {
        //Given
        List<Person> personList;

        //When
        personList = (List<Person>) userRepository.findAll();

        //Then
        assertThat(personList.size()).isEqualTo(1);
        assertThat(personList.get(0).getFullName()).isEqualTo("default user");
        assertThat(personList.get(0).getBooks().size()).isEqualTo(2);
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given
        Long userId = 1001L;

        //When
        userRepository.deleteById(userId);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Сохранить юзера с нулевым полем. Число select должно равняться 0")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPersonWithNullField_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        // title = null
        person.setFullName("Test Test");
        Person savedPerson = new Person();

        try {
            //When
            savedPerson = userRepository.save(person);
        } catch (Exception e) {
            //Then
            assertThat(e.getClass()).isEqualTo(DataIntegrityViolationException.class);
            assertThat(e.getCause().getClass()).isEqualTo(PropertyValueException.class);
            assertSelectCount(0);
            assertInsertCount(0);
            assertUpdateCount(0);
            assertDeleteCount(0);
        }
        assertThat(savedPerson.getId()).isEqualTo(null);
    }
}
