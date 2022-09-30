package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        createdUser.setBooks(bookService.getAllBooksByIdSet(Set.copyOf(bookIdList)));
        UserDto updatedUser = userService.updateUser(createdUser);
        log.info("Updated user: {}", updatedUser);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book update request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        bookService.getAllBooksByUserId(updatedUser.getId())
                .forEach(bookDto -> bookService.deleteBookById(bookDto.getId()));

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userDto.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookDto -> {
                    if (bookDto.getId() == null) {
                        return bookService.createBook(bookDto);
                    } else {
                        return bookService.updateBook(bookDto);
                    }
                })
                .peek(bookDto -> {
                    if (bookDto.getId() == null) {
                        log.info("Created book: {}", bookDto);
                    } else {
                        log.info("Updated book: {}", bookDto);
                    }
                })
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        updatedUser.setBooks(bookService.getAllBooksByIdSet(Set.copyOf(bookIdList)));
        updatedUser = userService.updateUser(updatedUser);
        log.info("Updated user: {}", updatedUser);

        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        log.info("Got user with books get request by id: {}", userId);
        UserDto userDto = userService.getUserById(userId);
        log.info("Received userDto: {}", userDto);
        Person person = userMapper.userDtoToPerson(userDto);
        log.info("Received person: {}", person);
        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(userDto.getBooks()
                        .stream().map(BookDto::getId).toList())
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        log.info("Got delete user with books request by id: {}", userId);
        bookService.getAllBooksByUserId(userId)
                .forEach(bookDto ->
                    bookService.deleteBookById(bookDto.getId())
                );
        userService.deleteUserById(userId);
    }
}
