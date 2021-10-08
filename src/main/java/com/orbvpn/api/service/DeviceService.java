package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.DeviceIdInput;
import com.orbvpn.api.domain.dto.DeviceView;
import com.orbvpn.api.domain.dto.UserDevice;
import com.orbvpn.api.domain.entity.Device;
import com.orbvpn.api.domain.entity.RadCheck;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.mapper.DeviceMapper;
import com.orbvpn.api.mapper.UserDeviceMapper;
import com.orbvpn.api.reposiitory.RadAcctRepository;
import com.orbvpn.api.reposiitory.RadCheckRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviceService {
    private final RadAcctRepository radAcctRepository;
    private final RadCheckRepository radCheckRepository;
    private final UserRepository userRepository;
    private final DeviceMapper deviceMapper;
    private final UserDeviceMapper userDeviceMapper;

    public Boolean deactivateDevice(Integer userId, DeviceIdInput deviceIdInput) {
        if(deviceIdInput.getValue() == null || deviceIdInput.getValue().equals("")){
            throw  new RuntimeException("deactivation is just for devices with valid id ");
        }
        Optional<User> user = userRepository.findById(userId);
        RadCheck deactivateCheck = new RadCheck();
        deactivateCheck.setUsername(user.get().getUsername());
        deactivateCheck.setAttribute("Deactivated-Device");
        deactivateCheck.setOp(":=");
        deactivateCheck.setValue(deviceIdInput.getValue());
        radCheckRepository.save(deactivateCheck);
        return true;
    }

    public Boolean activateDevice(Integer userId, DeviceIdInput deviceIdInput) {
        if(deviceIdInput.getValue() == null || deviceIdInput.getValue().equals("")){
            throw  new RuntimeException("activation is just for devices with valid id ");
        }
        Optional<User> user = userRepository.findById(userId);
        radCheckRepository.deleteByUsernameAndAttributeAndValue(user.get().getUsername(), "Deactivated-Device",
                deviceIdInput.getValue());
        return true;
    }

    public List<DeviceView> getDevices(Integer userId) {
        List<Device> devices = radAcctRepository.getDevices(userId);
        return devices.stream()
                .map(deviceMapper::deviceView)
                .collect(Collectors.toList());
    }

    public List<UserDevice> getAllDeactivatedDevices() {
        List<RadCheck> radChecks = radCheckRepository.findByAttribute("Deactivated-Device");
        return radChecks.stream()
                .map(userDeviceMapper::userDevice)
                .collect(Collectors.toList());
    }
}
