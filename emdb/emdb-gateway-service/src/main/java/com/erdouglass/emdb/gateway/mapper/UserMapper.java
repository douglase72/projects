package com.erdouglass.emdb.gateway.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.user.api.command.UpdateUser;
import com.erdouglass.emdb.user.api.query.UserDetails;
import com.erdouglass.emdb.user.proto.v1.UpdateUserRequest;
import com.erdouglass.emdb.user.proto.v1.UserResponse;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS 
)
public interface UserMapper {
  
  UpdateUserRequest toUpdateUserRequest(String id, UpdateUser command);
  
  UserDetails toUserDetails(UserResponse response);
}
