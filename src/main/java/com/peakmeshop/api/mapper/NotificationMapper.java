package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Notification;
import com.peakmeshop.api.dto.NotificationDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class, MemberMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface NotificationMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    NotificationDTO toDTO(Notification notification);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "read", constant = "false")
    Notification toEntity(NotificationDTO dto);
} 