package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = getPersonById(userDto.getId());
        log.info("User from DB: {}", user);
        user.setAge(userDto.getAge());
        user.setFullName(userDto.getFullName());
        user.setTitle(userDto.getTitle());
        user.setBooks(userDto.getBooks().stream()
                .map(bookDto -> bookMapper.bookDtoToBook(bookDto, this))
                .collect(Collectors.toSet()));
        Person savedUser = userRepository.save(user);
        log.info("Updated user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        if (id == null) {
            throw new NotFoundException(String.format("Person with id: %d not found", id));
        }
        Person person = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Person with id: %d not found", id)));
        log.info("(Service)Received person: {}", person);
        return userMapper.personToUserDto(person);
    }

    @Override
    public Person getPersonById(Long id) {
        if (id == null) {
            throw new NotFoundException(String.format("Person with id: %d not found", id));
        }
        Person person = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Person with id: %d not found", id)));
        log.info("(Service)Received person: {}", person);
        return person;
    }

    @Override
    public void deleteUserById(Long id) {
        if (id == null) {
            throw new NotFoundException(String.format("Person with id: %d not found", id));
        }
        userRepository.deleteById(id);
        log.info("(Service)Removed person with id : {}", id);
    }
}
