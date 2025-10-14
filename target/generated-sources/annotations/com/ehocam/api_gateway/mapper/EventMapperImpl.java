package com.ehocam.api_gateway.mapper;

import com.ehocam.api_gateway.dto.EventDto;
import com.ehocam.api_gateway.entity.Event;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-14T12:28:22+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEntity(EventDto.Create dto) {
        if ( dto == null ) {
            return null;
        }

        Event event = new Event();

        event.setTitle( dto.getTitle() );
        event.setSummary( dto.getSummary() );
        event.setContent( dto.getContent() );
        event.setDate( dto.getDate() );
        event.setCategory( dto.getCategory() );
        event.setCountry( dto.getCountry() );
        event.setMedia( toMediaEntity( dto.getMedia() ) );
        event.setI18n( stringI18nContentDtoMapToStringI18nContentMap( dto.getI18n() ) );

        return event;
    }

    @Override
    public void updateEntity(EventDto.Update dto, Event entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getTitle() != null ) {
            entity.setTitle( dto.getTitle() );
        }
        if ( dto.getSummary() != null ) {
            entity.setSummary( dto.getSummary() );
        }
        if ( dto.getContent() != null ) {
            entity.setContent( dto.getContent() );
        }
        if ( dto.getDate() != null ) {
            entity.setDate( dto.getDate() );
        }
        if ( dto.getCategory() != null ) {
            entity.setCategory( dto.getCategory() );
        }
        if ( dto.getCountry() != null ) {
            entity.setCountry( dto.getCountry() );
        }
        if ( dto.getMedia() != null ) {
            entity.setMedia( toMediaEntity( dto.getMedia() ) );
        }
        if ( entity.getI18n() != null ) {
            Map<String, Event.I18nContent> map = stringI18nContentDtoMapToStringI18nContentMap( dto.getI18n() );
            if ( map != null ) {
                entity.getI18n().clear();
                entity.getI18n().putAll( map );
            }
        }
        else {
            Map<String, Event.I18nContent> map = stringI18nContentDtoMapToStringI18nContentMap( dto.getI18n() );
            if ( map != null ) {
                entity.setI18n( map );
            }
        }
    }

    @Override
    public EventDto.Response toResponseDto(Event entity) {
        if ( entity == null ) {
            return null;
        }

        EventDto.Response response = new EventDto.Response();

        response.setId( entity.getId() );
        response.setTitle( entity.getTitle() );
        response.setSummary( entity.getSummary() );
        response.setContent( entity.getContent() );
        response.setDate( entity.getDate() );
        response.setCategory( entity.getCategory() );
        response.setCountry( entity.getCountry() );
        response.setMedia( toMediaDto( entity.getMedia() ) );
        response.setEngagement( toEngagementDto( entity.getEngagement() ) );
        response.setCreatedAt( entity.getCreatedAt() );
        response.setUpdatedAt( entity.getUpdatedAt() );
        response.setI18n( stringI18nContentMapToStringI18nContentDtoMap( entity.getI18n() ) );

        return response;
    }

    @Override
    public EventDto.MediaDto toMediaDto(Event.Media media) {
        if ( media == null ) {
            return null;
        }

        EventDto.MediaDto mediaDto = new EventDto.MediaDto();

        mediaDto.setThumbnailUrl( media.getThumbnailUrl() );
        mediaDto.setBannerUrl( media.getBannerUrl() );
        mediaDto.setYoutubeId( media.getYoutubeId() );
        mediaDto.setAudioUrl( media.getAudioUrl() );

        return mediaDto;
    }

    @Override
    public Event.Media toMediaEntity(EventDto.MediaDto dto) {
        if ( dto == null ) {
            return null;
        }

        Event.Media media = new Event.Media();

        media.setThumbnailUrl( dto.getThumbnailUrl() );
        media.setBannerUrl( dto.getBannerUrl() );
        media.setYoutubeId( dto.getYoutubeId() );
        media.setAudioUrl( dto.getAudioUrl() );

        return media;
    }

    @Override
    public EventDto.EngagementDto toEngagementDto(Event.Engagement engagement) {
        if ( engagement == null ) {
            return null;
        }

        EventDto.EngagementDto engagementDto = new EventDto.EngagementDto();

        engagementDto.setLikes( engagement.getLikes() );
        engagementDto.setComments( engagement.getComments() );
        engagementDto.setShares( engagement.getShares() );

        return engagementDto;
    }

    @Override
    public Event.Engagement toEngagementEntity(EventDto.EngagementDto dto) {
        if ( dto == null ) {
            return null;
        }

        Event.Engagement engagement = new Event.Engagement();

        engagement.setLikes( dto.getLikes() );
        engagement.setComments( dto.getComments() );
        engagement.setShares( dto.getShares() );

        return engagement;
    }

    @Override
    public EventDto.I18nContentDto toI18nContentDto(Event.I18nContent content) {
        if ( content == null ) {
            return null;
        }

        EventDto.I18nContentDto i18nContentDto = new EventDto.I18nContentDto();

        i18nContentDto.setTitle( content.getTitle() );
        i18nContentDto.setSummary( content.getSummary() );
        i18nContentDto.setContent( content.getContent() );

        return i18nContentDto;
    }

    @Override
    public Event.I18nContent toI18nContentEntity(EventDto.I18nContentDto dto) {
        if ( dto == null ) {
            return null;
        }

        Event.I18nContent i18nContent = new Event.I18nContent();

        i18nContent.setTitle( dto.getTitle() );
        i18nContent.setSummary( dto.getSummary() );
        i18nContent.setContent( dto.getContent() );

        return i18nContent;
    }

    protected Map<String, Event.I18nContent> stringI18nContentDtoMapToStringI18nContentMap(Map<String, EventDto.I18nContentDto> map) {
        if ( map == null ) {
            return null;
        }

        Map<String, Event.I18nContent> map1 = new LinkedHashMap<String, Event.I18nContent>( Math.max( (int) ( map.size() / .75f ) + 1, 16 ) );

        for ( java.util.Map.Entry<String, EventDto.I18nContentDto> entry : map.entrySet() ) {
            String key = entry.getKey();
            Event.I18nContent value = toI18nContentEntity( entry.getValue() );
            map1.put( key, value );
        }

        return map1;
    }

    protected Map<String, EventDto.I18nContentDto> stringI18nContentMapToStringI18nContentDtoMap(Map<String, Event.I18nContent> map) {
        if ( map == null ) {
            return null;
        }

        Map<String, EventDto.I18nContentDto> map1 = new LinkedHashMap<String, EventDto.I18nContentDto>( Math.max( (int) ( map.size() / .75f ) + 1, 16 ) );

        for ( java.util.Map.Entry<String, Event.I18nContent> entry : map.entrySet() ) {
            String key = entry.getKey();
            EventDto.I18nContentDto value = toI18nContentDto( entry.getValue() );
            map1.put( key, value );
        }

        return map1;
    }
}
