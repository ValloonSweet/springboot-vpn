package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ConnectionHistoryView;
import com.orbvpn.api.domain.dto.OnlineSessionView;
import com.orbvpn.api.domain.entity.RadAcct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConnectionMapper {
    @Mapping(source = "radacctid", target = "id")
    @Mapping(source = "acctsessionid", target = "sessionId")
    @Mapping(source = "nasipaddress", target = "nasIpAddress")
    @Mapping(source = "nasportid", target = "nasPortId")
    @Mapping(source = "nasporttype", target = "nasPortType")
    @Mapping(source = "acctstarttime", target = "startTime")
    @Mapping(source = "acctstoptime", target = "stopTime")
    @Mapping(source = "connectinfo_start", target = "connectInfoStart")
    @Mapping(source = "callingstationid", target = "callingStationId")
    @Mapping(source = "acctterminatecause", target = "terminateCause")
    @Mapping(source = "framedipaddress", target = "framedIpAddress")
    ConnectionHistoryView connectionHistoryView(RadAcct radAcct);

    @Mapping(source = "radacctid", target = "id")
    @Mapping(source = "acctsessionid", target = "sessionId")
    @Mapping(source = "nasipaddress", target = "nasIpAddress")
    @Mapping(source = "nasportid", target = "nasPortId")
    @Mapping(source = "nasporttype", target = "nasPortType")
    @Mapping(source = "acctstarttime", target = "startTime")
    @Mapping(source = "connectinfo_start", target = "connectInfoStart")
    @Mapping(source = "callingstationid", target = "callingStationId")
    @Mapping(source = "framedipaddress", target = "framedIpAddress")
    OnlineSessionView onlineSessionView(RadAcct radAcct);
}
