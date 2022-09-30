package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private Long getNextId(){
        return jdbcTemplate.query("SELECT nextval('sequence')",
                rs -> {
                    if (rs.next()) {
                        return rs.getLong(1) + 100;
                    } else {
                        throw new SQLException("Unable to retrieve value from sequence sequence.");
                    }
                });
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO ULAB_EDU.BOOK(ID, TITLE, AUTHOR, PAGE_COUNT, PERSON_ID) VALUES (?,?,?,?,?)";
        Long id = getNextId();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setLong(1, id);
                    ps.setString(2, bookDto.getTitle());
                    ps.setString(3, bookDto.getAuthor());
                    ps.setLong(4, bookDto.getPageCount());
                    ps.setLong(5, bookDto.getUserId());
                    return ps;
                });

        bookDto.setId(id);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_SQL = "UPDATE ULAB_EDU.BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, PERSON_ID = ? WHERE ID = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL);
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    if (bookDto.getUserId() == null) {
                        ps.setNull(4, JDBCType.NULL.getVendorTypeNumber());
                    } else {
                        ps.setLong(4, bookDto.getUserId());
                    }
                    ps.setLong(5, bookDto.getId());
                    return ps;
                });
        log.info("Updated book: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        final String SELECT_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE ID = ?";
        return jdbcTemplate.queryForObject(SELECT_SQL, (rs, rowNum) -> {
            BookDto bookDto = new BookDto();
            bookDto.setId(id);
            bookDto.setTitle(rs.getString("title"));
            bookDto.setAuthor(rs.getString("author"));
            bookDto.setPageCount(rs.getLong("page_count"));
            bookDto.setUserId(rs.getLong("person_id"));
            log.info("User from DB: {}", bookDto);
            return bookDto;
        }, id);
    }

    @Override
    public Set<BookDto> getAllBooksByUserId(Long userId) {
        final String SELECT_BOOKS_BY_USER_ID_SQL = "SELECT * FROM ULAB_EDU.BOOK WHERE PERSON_ID = ?";
        return Set.copyOf(jdbcTemplate.query(
                SELECT_BOOKS_BY_USER_ID_SQL,
                (rs, rowNum) -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setId(rs.getLong("id"));
                    bookDto.setTitle(rs.getString("title"));
                    bookDto.setAuthor(rs.getString("author"));
                    bookDto.setPageCount(rs.getLong("page_count"));
                    bookDto.setUserId(rs.getLong("person_id"));
                    return bookDto;
                }, userId
        ));
    }

    @Override
    public Set<BookDto> getAllBooksByIdSet(Set<Long> idSet) {
        List<String> idStringList = idSet.stream().map(String::valueOf).collect(Collectors.toList());
        final String SELECT_BOOKS_SQL = String.format("SELECT * FROM ULAB_EDU.BOOK WHERE ID IN (%s)", String.join(",", idStringList));
        List<BookDto> bookDtoList = jdbcTemplate.query(
                SELECT_BOOKS_SQL,
                (rs, rowNum) -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setId(rs.getLong("id"));
                    bookDto.setTitle(rs.getString("title"));
                    bookDto.setAuthor(rs.getString("author"));
                    bookDto.setPageCount(rs.getLong("page_count"));
                    bookDto.setUserId(rs.getLong("person_id"));
                    return bookDto;
                }
        );
        return Set.copyOf(bookDtoList);
    }

    @Override
    public void deleteBookById(Long id) {
        final String DELETE_SQL = "DELETE FROM ULAB_EDU.BOOK WHERE ID = ?";
        int rows = jdbcTemplate.update(DELETE_SQL, id);
        log.info("Removed {} rows from BOOK", rows);
    }
}
