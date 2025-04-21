package com.peakmeshop.api.mapper;

import com.peakmeshop.api.dto.MemberDTO;
import com.peakmeshop.domain.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface MemberMapper {
    
    @Mapping(target = "password", ignore = true)
    MemberDTO toDto(Member member);
    
    List<MemberDTO> toDtoList(List<Member> members);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockoutEndTime", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetTokenExpiresAt", ignore = true)
    Member toEntity(MemberDTO memberDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "lockoutEndTime", ignore = true)
    @Mapping(target = "emailVerifiedAt", ignore = true)
    @Mapping(target = "emailVerificationToken", ignore = true)
    @Mapping(target = "passwordResetToken", ignore = true)
    @Mapping(target = "passwordResetTokenExpiresAt", ignore = true)
    void updateMemberFromDto(MemberDTO memberDTO, @MappingTarget Member member);
} 