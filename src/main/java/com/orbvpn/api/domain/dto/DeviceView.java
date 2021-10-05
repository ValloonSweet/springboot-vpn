package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.orbvpn.api.domain.enums.DeviceAppType;
import com.orbvpn.api.domain.enums.DeviceOsType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class DeviceView {
    DeviceAppType deviceAppType;
    DeviceOsType deviceOsType;
    DeviceIdView deviceIdView;
    String appVersion;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime maxConnectionStartTime;
    Boolean isConnected;
}
