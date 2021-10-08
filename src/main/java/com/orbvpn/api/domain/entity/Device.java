package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.dto.DeviceIdView;
import com.orbvpn.api.domain.enums.DeviceAppType;
import com.orbvpn.api.domain.enums.DeviceOsType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;


@Slf4j
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Device {
    private static final String SEPARATOR = " - ";
    private DeviceAppType deviceAppType;
    private DeviceOsType deviceOsType;
    private DeviceIdView deviceIdView;
    private String appVersion;
    private LocalDateTime maxConnectionStartTime;
    private Boolean isConnected;

    public Device(String connectioninfo_start, LocalDateTime maxConnectionStartTime,
                  Long connectionStartCnt,Long connectionStopCnt) {
        parsInfo(connectioninfo_start);
        this.maxConnectionStartTime = maxConnectionStartTime;
        this.isConnected = (connectionStartCnt != connectionStopCnt);
    }

    private void parsInfo(String connectioninfo_start) {
        Integer firstIdx = connectioninfo_start.indexOf(SEPARATOR);
        Integer lastIdx = connectioninfo_start.lastIndexOf(SEPARATOR);

        /**
         * Determining Device App Type
         */
        String os = null;
        String id = null;
        String ver = null;
        if (firstIdx == -1 || lastIdx == -1) {
            deviceAppType = DeviceAppType.EXTERNAL;
        } else {
            os = connectioninfo_start.substring(0, firstIdx);
            id = connectioninfo_start.substring(firstIdx + SEPARATOR.length(), lastIdx);
            ver = connectioninfo_start.substring(lastIdx + SEPARATOR.length());

            if (os == null || id == null || ver == null) {
                deviceAppType = DeviceAppType.EXTERNAL;
            } else {
                deviceAppType = DeviceAppType.INTERNAL;
            }
        }

        /**
         * Internal App
         */
        if (deviceAppType == DeviceAppType.INTERNAL) {
            appVersion = ver.trim();
            os = os.trim().toUpperCase();
            deviceOsType = DeviceOsType.valueOf(os);

            deviceIdView = new DeviceIdView();
            switch (deviceOsType) {
                case IOS:
                    deviceIdView.setUuid(id);
                    break;
                case ANDROID:
                    deviceIdView.setSerial(id);
                    break;
                case MACOS:
                case WINDOWS:
                    deviceIdView.setName(id);
                    break;
                default:
                    deviceIdView.setName(id);
            }
        } else if (deviceAppType == DeviceAppType.EXTERNAL) {
            for (DeviceOsType osType : DeviceOsType.values()) {
                if (connectioninfo_start.toUpperCase().contains(osType.getExternalAppAlternateName().toUpperCase())) {
                    deviceOsType = osType;
                }
            }
        }
    }

    public static String getDeviceIdWrappedBySeparators(String deviceId) {
        return SEPARATOR + deviceId + SEPARATOR;
    }
}
