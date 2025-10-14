package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    User toEntity(UserDto.Create dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    void updateEntity(UserDto.Update dto, @MappingTarget User entity);

    UserDto.Response toResponseDto(User entity);

    UserDto.UserPreferencesDto toPreferencesDto(User.UserPreferences preferences);
    User.UserPreferences toPreferencesEntity(UserDto.UserPreferencesDto dto);

    UserDto.DeviceDto toDeviceDto(User.Device device);
    User.Device toDeviceEntity(UserDto.DeviceDto dto);
}
