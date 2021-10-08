package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserDevice;
import com.orbvpn.api.domain.entity.RadCheck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDeviceMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "value", target = "deviceId")
    UserDevice userDevice(RadCheck radCheck);
}
