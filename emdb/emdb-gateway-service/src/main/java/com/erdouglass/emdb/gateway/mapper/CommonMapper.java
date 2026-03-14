package com.erdouglass.emdb.gateway.mapper;

import com.erdouglass.emdb.media.proto.v1.SaveStatus;

public interface CommonMapper {
  
  default Integer mapProtoStatusToHttpCode(SaveStatus status) {
    if (status == null) {
      return 500;
    }
    return switch (status) {
      case CREATED -> 201;
      case UPDATED -> 200;
      case UNCHANGED -> 204;
      case UNRECOGNIZED, STATUS_UNSPECIFIED -> 500;
    };
  }

}
