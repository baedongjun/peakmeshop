package com.peakmeshop.api.mapper;

import com.peakmeshop.domain.entity.Qna;
import com.peakmeshop.api.dto.QnaDTO;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    uses = {BaseMapper.class},
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface QnaMapper {
    
    @Mapping(target = "memberId", source = "member.id")
    @Mapping(target = "memberName", source = "member.name")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "answeredById", source = "answeredBy.id")
    @Mapping(target = "answeredByName", source = "answeredBy.name")
    QnaDTO toDTO(Qna qna);

    @Mapping(target = "member", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "answeredBy", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    Qna toEntity(QnaDTO dto);
} 