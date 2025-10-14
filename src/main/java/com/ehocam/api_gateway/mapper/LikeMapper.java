package com.ehocam.api_gateway.mapper;

import org.mapstruct.Mapper;

import com.ehocam.api_gateway.dto.LikeDto;
import com.ehocam.api_gateway.entity.Like;

@Mapper(componentModel = "spring")
public interface LikeMapper {

    LikeDto.Response toResponseDto(Like entity);
}
