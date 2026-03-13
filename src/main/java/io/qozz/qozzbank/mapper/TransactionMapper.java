package io.qozz.qozzbank.mapper;

import io.qozz.qozzbank.domain.entity.TransactionEntity;
import io.qozz.qozzbank.service.dto.transaction.TransactionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.TransactionData;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TransactionMapper {
    @Mapping(target = "relatedCardId", source = "relatedCard.id")
    TransactionDto toDto(TransactionEntity entity);

    @Mapping(target = "type", expression = "java(dto.type().getValue())")
    @Mapping(target = "status", expression = "java(dto.status().getValue())")
    TransactionData toTransactionData(TransactionDto dto);
}