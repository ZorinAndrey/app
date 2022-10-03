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

    private static UserBookRequest createRequest;

    private static UserBookRequest updateRequest;

    private static UserBookResponse response;

    @BeforeAll
    public static void initEntities() {
        UserRequest createUserRequest = new UserRequest();
        createUserRequest.setAge(33);
        createUserRequest.setFullName("test name");
        createUserRequest.setTitle("test title");

        BookRequest createBookRequest = new BookRequest();
        createBookRequest.setAuthor("test author");
        createBookRequest.setTitle("test book title");
        createBookRequest.setPageCount(1000);

        createRequest = new UserBookRequest();
        createRequest.setUserRequest(createUserRequest);
        createRequest.setBookRequests(List.of(createBookRequest));

        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setId(1L);
        updateUserRequest.setAge(44);
        updateUserRequest.setFullName("new name");
        updateUserRequest.setTitle("new title");

        BookRequest updateBookRequest = new BookRequest();
        updateBookRequest.setId(1L);
        updateBookRequest.setAuthor("new author");
        updateBookRequest.setTitle("new book title");
        updateBookRequest.setPageCount(999);

        updateRequest = new UserBookRequest();
        updateRequest.setUserRequest(updateUserRequest);
        updateRequest.setBookRequests(List.of(updateBookRequest));

        response = UserBookResponse.builder()
                .userId(1L)
                .booksIdList(List.of(1L))
                .build();
    }

    @Test
    @DisplayName("Создание юзера с книгами. Должно пройти успешно.")
    public void createUserWithBooksTest() throws Exception {
        //given
        given(userDataFacade.createUserWithBooks(createRequest)).willReturn(response);

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
        given(userDataFacade.updateUserWithBooks(updateRequest)).willReturn(response);

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
        Long userId = 1L;
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
        Long userId = 1L;

        //when
        mvc.perform(delete(WebConstant.VERSION_URL + "/user/delete/" + userId)
                        .header("rqid", "requestId1010101"))

                //then
                .andExpect(status().isOk());
    }
}
