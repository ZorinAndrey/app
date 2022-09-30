package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final BookServiceImplTemplate bookServiceImplTemplate;
    private final UserMapper userMapper;


    private Long getNextId(){
        return jdbcTemplate.query("SELECT nextval('sequence')",
                rs -> {
                    if (rs.next()) {
                        return rs.getLong(1);
                    } else {
                        throw new SQLException("Unable to retrieve value from sequence sequence.");
                    }
                });
    }

    @Override
    public UserDto createUser(UserDto userDto) {

        final String INSERT_SQL = "INSERT INTO ULAB_EDU.PERSON(ID, FULL_NAME, TITLE, AGE) VALUES (?,?,?,?)";
        Long id = getNextId();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setLong(1, id);
                    ps.setString(2, userDto.getFullName());
                    ps.setString(3, userDto.getTitle());
                    ps.setLong(4, userDto.getAge());
                    return ps;
                });

        userDto.setId(id);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final String UPDATE_SQL = "UPDATE ULAB_EDU.PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    ps.setLong(4, userDto.getId());
                    return ps;
                });
        log.info("Updated user: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.PERSON WHERE ID = ?";
        return jdbcTemplate.queryForObject(SELECT_SQL, (rs, rowNum) -> {
            UserDto userDto = new UserDto();
            userDto.setId(id);
            userDto.setAge(rs.getInt("age"));
            userDto.setFullName(rs.getString("full_name"));
            userDto.setTitle(rs.getString("title"));
            userDto.setBooks(bookServiceImplTemplate.getAllBooksByUserId(id));
            log.info("User from DB: {}", userDto);
            return userDto;
        }, id);
    }

    @Override
    public Person getPersonById(Long id) {
        return userMapper.userDtoToPerson(getUserById(id));
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.PERSON WHERE ID = ?";
        int rows = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Removed {} rows from PERSON", rows);
    }
}
