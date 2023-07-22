package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "user", target = "author")
    Comment toComment(CommentCreateDto commentCreateDto, Item item, User user);

    @Mapping(source = "comment.author.name", target = "authorName")
    @Mapping(source = "comment.id", target = "id")
    CommentOutputDto toOutputDto(Comment comment);
}
