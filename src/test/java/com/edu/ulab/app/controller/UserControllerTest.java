package com.edu.ulab.app.controller;

import com.edu.ulab.app.facade.UserDataFacade;
import com.edu.ulab.app.web.UserController;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.request.UserRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тестирование контроллера {@link UserController}.
 */

@DisplayName("Testing UserController")
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private UserDataFacade userDataFacade;

    private static UserBookRequest request;

    private static UserRequest userRequest;

    private static BookRequest bookRequest;

    private static UserBookResponse response;

    @BeforeAll
    public static void initEntities() {
        userRequest = new UserRequest();
        userRequest.setAge(33);
        userRequest.setFullName("test name");
        userRequest.setTitle("test title");

        bookRequest = new BookRequest();
        bookRequest.setAuthor("test author");
        bookRequest.setTitle("test book title");
        bookRequest.setPageCount(1000);

        request = new UserBookRequest();
        request.setUserRequest(userRequest);
        request.setBookRequests(List.of(bookRequest));

        response = UserBookResponse.builder()
                .userId(userRequest.getId())
                .booksIdList(request.getBookRequests().stream()
                        .map(BookRequest::getId).collect(Collectors.toList()))
                .build();
    }

    @Test
    @DisplayName("Создание юзера с книгами. Должно пройти успешно.")
    public void createUserWithBooksTest() throws Exception {
        //given
        given(userDataFacade.createUserWithBooks(request)).willReturn(response);

        //when
        mvc.perform(post(WebConstant.VERSION_URL + "/user/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("rqid", "requestId1010101")
                        .content(objectMapper
                                .writeValueAsString(response
                                )))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Обновление юзера с книгами. Должно пройти успешно.")
    public void updateUserWithBooksTest() throws Exception {
        //given
        userRequest.setId(1L);
        bookRequest.setId(1L);

        given(userDataFacade.updateUserWithBooks(request)).willReturn(response);

        //when
        mvc.perform(put(WebConstant.VERSION_URL + "/user/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("rqid", "requestId1010101")
                        .content(objectMapper
                                .writeValueAsString(response
                                )))
        //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Получение юзера с книгами. Должно пройти успешно.")
    public void getUserWithBooksTest() throws Exception {
        //given
        userRequest.setId(1L);
        bookRequest.setId(1L);

        Long userId = userRequest.getId();
        given(userDataFacade.getUserWithBooks(userId)).willReturn(response);

        //when
        mvc.perform(get(WebConstant.VERSION_URL + "/user/get/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("rqid", "requestId1010101")
                        .content(objectMapper
                                .writeValueAsString(response
                                )))

        //then
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Удаление юзера с книгами. Должно пройти успешно.")
    public void deleteUserWithBooksTest() throws Exception {
        //given
        userRequest.setId(1L);
        bookRequest.setId(1L);

        Long userId = userRequest.getId();

        try {
            userDataFacade.deleteUserWithBooks(userId);
            assertTrue(true);
        } catch (Exception e) {
            fail();
        }

        //when
        mvc.perform(delete(WebConstant.VERSION_URL + "/user/delete/" + userId)
                        .header("rqid", "requestId1010101"))

        //then
                .andExpect(status().isOk());
    }
}
