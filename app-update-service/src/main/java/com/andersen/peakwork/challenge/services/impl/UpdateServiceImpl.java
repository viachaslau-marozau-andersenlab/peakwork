package com.andersen.peakwork.challenge.services.impl;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.repository.interfaces.GeoCoordinateRepository;
import com.andersen.peakwork.challenge.services.interfaces.UpdateService;
import com.andersen.peakwork.challenge.third.party.connectors.interfaces.NominatimConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UpdateServiceImpl implements UpdateService {

    @Autowired
    @Qualifier("connector")
    private NominatimConnector connector;

    @Autowired
    @Qualifier("repository")
    private GeoCoordinateRepository repository;

    @Scheduled(fixedRateString = "${pollingInterval}")
    @Override
    public long updateGeoCoordinateData() {
        log.info("process updateGeoCoordinateData() started at {}", new Date());
        List<GeoCoordinateInfo> dataToUpdate = new ArrayList<>();
        Iterator<GeoCoordinateInfo> iterator = repository.getGeoCoordinateInfoScrollIterator();
        while (iterator.hasNext()) {
            GeoCoordinateInfo internalGeoCoordinateInfo = iterator.next();
            GeoCoordinateInfo externalGeoCoordinateInfo = connector.getGeoCoordinateInfoByFullOsmId(
                    internalGeoCoordinateInfo.getPlaceId(), getOsmFullId(internalGeoCoordinateInfo));
            if (internalGeoCoordinateInfo != null && !internalGeoCoordinateInfo.equals(externalGeoCoordinateInfo)) {
                dataToUpdate.add(externalGeoCoordinateInfo);
            }
        }
        log.info("process updateGeoCoordinateData() started at {}", new Date());
        long updatedEntities = repository.updateGeoCoordinateData(dataToUpdate);
        log.info("process updateGeoCoordinateData() finished successfully at {}", new Date());
        return updatedEntities;
    }

    private String getOsmFullId(GeoCoordinateInfo geoCoordinateInfo) {
        StringBuilder builder = new StringBuilder();
        builder.append(geoCoordinateInfo.getOsmType().toUpperCase().charAt(0));
        builder.append(geoCoordinateInfo.getOsmId());
        return builder.toString();
    }
}
