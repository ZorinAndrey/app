package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUser_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(22);
        userDto.setFullName("new name");
        userDto.setTitle("new title");

        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setTitle("test title");
        bookDto.setAuthor("test author");
        bookDto.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPerson(person);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPageCount(1000);

        Person updatedPerson = new Person();
        updatedPerson.setId(1L);
        updatedPerson.setAge(22);
        updatedPerson.setFullName("new name");
        updatedPerson.setTitle("new title");

        //when
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(person));
        when(bookMapper.bookDtoToBook(bookDto, userService)).thenReturn(book);
        when(userRepository.save(person)).thenReturn(updatedPerson);
        when(userMapper.personToUserDto(updatedPerson)).thenReturn(userDto);

        UserDto userDtoResult = userService.updateUser(userDto);

        //then
        assertEquals(1L, userDtoResult.getId());
        assertEquals(22, userDtoResult.getAge());
        assertEquals("new name", userDtoResult.getFullName());
        assertEquals("new title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение представления пользователя по идентификатору. Должно пройти успешно.")
    void getUserById_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        //when
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(userDto);

        UserDto userDtoResult = userService.getUserById(userDto.getId());

        //then
        assertEquals(1L, userDtoResult.getId());
        assertEquals(11, userDtoResult.getAge());
        assertEquals("test name", userDtoResult.getFullName());
        assertEquals("test title", userDtoResult.getTitle());
    }

    @Test
    @DisplayName("Получение пользователя по идентификатору. Должно пройти успешно.")
    void getPersonById_Test() {
        //given

        Person person = new Person();
        person.setId(1L);
        person.setAge(11);
        person.setFullName("test name");
        person.setTitle("test title");

        //when
        when(userRepository.findById(1L)).thenReturn(Optional.of(person));

        Person personResult = userService.getPersonById(1L);

        //then
        assertEquals(1L, personResult.getId());
        assertEquals(11, personResult.getAge());
        assertEquals("test name", personResult.getFullName());
        assertEquals("test title", personResult.getTitle());
    }

    @Test
    @DisplayName("Удаление пользователя по идентификатору. Должно пройти успешно.")
    void deleteUserById_Test() {
        //given
        Long userId = 1L;

        try {
            //when
            userService.deleteUserById(userId);
            //then
            assertTrue(true);
        } catch (Exception e) {
            //or then
            fail();
        }
    }

    @Test
    @DisplayName("Удаление пользователя по несуществующему идентификатору. Должно пройти успешно.")
    void deleteUserByIdWithWrongId_Test() {
        //given
        Long userId = 0L;

        //when
        doThrow(new EmptyResultDataAccessException("No entity with id 0 exists!", 1)).when(userRepository).deleteById(userId);

        //then
        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(EmptyResultDataAccessException.class)
                .hasMessage("No entity with id 0 exists!");
    }
}
