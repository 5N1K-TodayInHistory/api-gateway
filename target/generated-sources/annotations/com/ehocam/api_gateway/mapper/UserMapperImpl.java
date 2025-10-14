package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.UserDto;
import com.ehocam.api_gateway.entity.User;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-14T12:28:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserDto.Create dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setEmail( dto.getEmail() );
        user.setAuthProvider( dto.getAuthProvider() );
        user.setDisplayName( dto.getDisplayName() );
        user.setAvatarUrl( dto.getAvatarUrl() );
        user.setPreferences( toPreferencesEntity( dto.getPreferences() ) );
        user.setDevices( deviceDtoListToDeviceList( dto.getDevices() ) );

        return user;
    }

    @Override
    public void updateEntity(UserDto.Update dto, User entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getDisplayName() != null ) {
            entity.setDisplayName( dto.getDisplayName() );
        }
        if ( dto.getAvatarUrl() != null ) {
            entity.setAvatarUrl( dto.getAvatarUrl() );
        }
        if ( dto.getPreferences() != null ) {
            entity.setPreferences( toPreferencesEntity( dto.getPreferences() ) );
        }
    }

    @Override
    public UserDto.Response toResponseDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserDto.Response response = new UserDto.Response();

        response.setId( entity.getId() );
        response.setEmail( entity.getEmail() );
        response.setAuthProvider( entity.getAuthProvider() );
        Set<User.Role> set = entity.getRoles();
        if ( set != null ) {
            response.setRoles( new LinkedHashSet<User.Role>( set ) );
        }
        response.setDisplayName( entity.getDisplayName() );
        response.setAvatarUrl( entity.getAvatarUrl() );
        response.setPreferences( toPreferencesDto( entity.getPreferences() ) );
        response.setDevices( deviceListToDeviceDtoList( entity.getDevices() ) );
        response.setCreatedAt( entity.getCreatedAt() );
        response.setUpdatedAt( entity.getUpdatedAt() );

        return response;
    }

    @Override
    public UserDto.UserPreferencesDto toPreferencesDto(User.UserPreferences preferences) {
        if ( preferences == null ) {
            return null;
        }

        UserDto.UserPreferencesDto userPreferencesDto = new UserDto.UserPreferencesDto();

        userPreferencesDto.setViewMode( preferences.getViewMode() );
        List<String> list = preferences.getCountries();
        if ( list != null ) {
            userPreferencesDto.setCountries( new ArrayList<String>( list ) );
        }
        List<String> list1 = preferences.getCategories();
        if ( list1 != null ) {
            userPreferencesDto.setCategories( new ArrayList<String>( list1 ) );
        }
        userPreferencesDto.setLanguage( preferences.getLanguage() );
        userPreferencesDto.setNotifications( notificationPreferencesToNotificationPreferencesDto( preferences.getNotifications() ) );

        return userPreferencesDto;
    }

    @Override
    public User.UserPreferences toPreferencesEntity(UserDto.UserPreferencesDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserPreferences userPreferences = new User.UserPreferences();

        userPreferences.setViewMode( dto.getViewMode() );
        List<String> list = dto.getCountries();
        if ( list != null ) {
            userPreferences.setCountries( new ArrayList<String>( list ) );
        }
        List<String> list1 = dto.getCategories();
        if ( list1 != null ) {
            userPreferences.setCategories( new ArrayList<String>( list1 ) );
        }
        userPreferences.setLanguage( dto.getLanguage() );
        userPreferences.setNotifications( notificationPreferencesDtoToNotificationPreferences( dto.getNotifications() ) );

        return userPreferences;
    }

    @Override
    public UserDto.DeviceDto toDeviceDto(User.Device device) {
        if ( device == null ) {
            return null;
        }

        UserDto.DeviceDto deviceDto = new UserDto.DeviceDto();

        deviceDto.setFcmToken( device.getFcmToken() );
        deviceDto.setPlatform( device.getPlatform() );
        deviceDto.setLastSeen( device.getLastSeen() );

        return deviceDto;
    }

    @Override
    public User.Device toDeviceEntity(UserDto.DeviceDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.Device device = new User.Device();

        device.setFcmToken( dto.getFcmToken() );
        device.setPlatform( dto.getPlatform() );
        device.setLastSeen( dto.getLastSeen() );

        return device;
    }

    protected List<User.Device> deviceDtoListToDeviceList(List<UserDto.DeviceDto> list) {
        if ( list == null ) {
            return null;
        }

        List<User.Device> list1 = new ArrayList<User.Device>( list.size() );
        for ( UserDto.DeviceDto deviceDto : list ) {
            list1.add( toDeviceEntity( deviceDto ) );
        }

        return list1;
    }

    protected List<UserDto.DeviceDto> deviceListToDeviceDtoList(List<User.Device> list) {
        if ( list == null ) {
            return null;
        }

        List<UserDto.DeviceDto> list1 = new ArrayList<UserDto.DeviceDto>( list.size() );
        for ( User.Device device : list ) {
            list1.add( toDeviceDto( device ) );
        }

        return list1;
    }

    protected UserDto.UserPreferencesDto.NotificationPreferencesDto notificationPreferencesToNotificationPreferencesDto(User.UserPreferences.NotificationPreferences notificationPreferences) {
        if ( notificationPreferences == null ) {
            return null;
        }

        UserDto.UserPreferencesDto.NotificationPreferencesDto notificationPreferencesDto = new UserDto.UserPreferencesDto.NotificationPreferencesDto();

        notificationPreferencesDto.setDaily( notificationPreferences.isDaily() );
        notificationPreferencesDto.setBreaking( notificationPreferences.isBreaking() );

        return notificationPreferencesDto;
    }

    protected User.UserPreferences.NotificationPreferences notificationPreferencesDtoToNotificationPreferences(UserDto.UserPreferencesDto.NotificationPreferencesDto notificationPreferencesDto) {
        if ( notificationPreferencesDto == null ) {
            return null;
        }

        User.UserPreferences.NotificationPreferences notificationPreferences = new User.UserPreferences.NotificationPreferences();

        notificationPreferences.setDaily( notificationPreferencesDto.isDaily() );
        notificationPreferences.setBreaking( notificationPreferencesDto.isBreaking() );

        return notificationPreferences;
    }
}
