package com.erdouglass.emdb.user.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.user.api.command.UpdateUser;
import com.erdouglass.emdb.user.entity.User;
import com.erdouglass.emdb.user.proto.v1.UpdateUserRequest;
import com.erdouglass.emdb.user.proto.v1.UserResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "username", ignore = true)
  void merge(UpdateUser command, @MappingTarget User user);
  
  UpdateUser toUpdateUser(UpdateUserRequest request);
  
  UserResponse toUserResponse(User user);
}
