package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *  UDID for iOS,
 *  Serial number for Android,
 *  Name for MacOS and Windows
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class DeviceIdView {
    private String uuid;
    private String serial;
    private String name;
}
