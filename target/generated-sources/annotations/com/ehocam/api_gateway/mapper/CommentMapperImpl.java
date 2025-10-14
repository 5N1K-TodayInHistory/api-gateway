package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.CommentDto;
import com.ehocam.api_gateway.entity.Comment;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-14T12:28:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class CommentMapperImpl implements CommentMapper {

    @Override
    public Comment toEntity(CommentDto.Create dto) {
        if ( dto == null ) {
            return null;
        }

        Comment comment = new Comment();

        comment.setEventId( dto.getEventId() );
        comment.setContent( dto.getContent() );

        return comment;
    }

    @Override
    public CommentDto.Response toResponseDto(Comment entity) {
        if ( entity == null ) {
            return null;
        }

        CommentDto.Response response = new CommentDto.Response();

        response.setId( entity.getId() );
        response.setEventId( entity.getEventId() );
        response.setUserId( entity.getUserId() );
        response.setContent( entity.getContent() );
        response.setCreatedAt( entity.getCreatedAt() );

        return response;
    }
}
