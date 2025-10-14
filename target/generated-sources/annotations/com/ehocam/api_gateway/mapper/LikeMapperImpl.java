package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.LikeDto;
import com.ehocam.api_gateway.entity.Like;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-14T12:28:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class LikeMapperImpl implements LikeMapper {

    @Override
    public LikeDto.Response toResponseDto(Like entity) {
        if ( entity == null ) {
            return null;
        }

        LikeDto.Response response = new LikeDto.Response();

        response.setId( entity.getId() );
        response.setEventId( entity.getEventId() );
        response.setUserId( entity.getUserId() );
        response.setCreatedAt( entity.getCreatedAt() );

        return response;
    }
}
