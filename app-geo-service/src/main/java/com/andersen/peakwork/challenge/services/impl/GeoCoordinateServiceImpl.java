package com.andersen.peakwork.challenge.services.impl;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.repository.interfaces.GeoCoordinateRepository;
import com.andersen.peakwork.challenge.services.interfaces.GeoCoordinateService;
import com.andersen.peakwork.challenge.third.party.connectors.interfaces.NominatimConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GeoCoordinateServiceImpl implements GeoCoordinateService {

    @Autowired
    @Qualifier("connector")
    private NominatimConnector connector;

    @Autowired
    @Qualifier("repository")
    private GeoCoordinateRepository repository;

    @Override
    public GeoCoordinateInfo getGeoCoordinateInfo(double latitude, double longitude) {
        log.debug("Call third party system.");
        GeoCoordinateInfo result = connector.getGeoCoordinateInfoByGeoCoordinate(latitude, longitude);
        log.debug("Third party system result for coordinate {}:{}. Result: {}", latitude, longitude, result);
        repository.storeGeoCoordinateInfo(result);
        return result;
    }
}
