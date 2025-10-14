package com.ehocam.api_gateway.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ehocam.api_gateway.dto.CommentDto;
import com.ehocam.api_gateway.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "userId", ignore = true)
    Comment toEntity(CommentDto.Create dto);

    CommentDto.Response toResponseDto(Comment entity);
}
