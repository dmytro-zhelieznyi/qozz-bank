package io.qozz.qozzbank.mapper;

import io.qozz.qozzbank.domain.entity.AccountEntity;
import io.qozz.qozzbank.service.dto.account.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.AccountResponse;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AccountMapper {
    @Mapping(source = "user.id", target = "userId")
    AccountDto toDto(AccountEntity entity);

    AccountResponse toResponse(AccountDto dto);
}