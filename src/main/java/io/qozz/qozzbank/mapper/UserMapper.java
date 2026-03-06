package io.qozz.qozzbank.mapper;

import io.qozz.qozzbank.domain.entity.UserEntity;
import io.qozz.qozzbank.service.dto.user.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.openapitools.model.UserRequest;
import org.openapitools.model.UserResponse;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    UserEntity toEntity(UserRequest request);

    UserDto toDto(UserEntity entity);

    UserResponse toResponse(UserDto dto);
}