package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import org.springframework.stereotype.Repository;

@Repository
public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto);

    UserDto getUserById(Long id);

    Person getPersonById(Long id);

    void deleteUserById(Long id);
}
