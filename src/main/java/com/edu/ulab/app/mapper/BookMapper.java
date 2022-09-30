package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    Book bookRequestToBook(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    @Mapping(source = "userId", target = "person", qualifiedByName = "userIdToPerson")
    Book bookDtoToBook(BookDto bookDto, @Context UserService userService);

    @Mapping(source = "person", target = "userId", qualifiedByName = "personToUserId")
    BookDto bookToBookDto(Book book);

    @Named(value = "userIdToPerson")
    default Person userIdToPerson(Long personId, @Context UserService userService) {
        if (personId == null) {
            throw new NotFoundException("Null given personId");
        }
        return userService.getPersonById(personId);
    }

    @Named(value = "personToUserId")
    default Long personToUserId(Person person) {
        if (person == null) {
            throw new NotFoundException("Null given person");
        }
        return person.getId();
    }
}
