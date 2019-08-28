package com.andersen.peakwork.challenge.services.interfaces;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;

public interface GeoCoordinateService {

    /**
     *
     *
     *
     * */
    GeoCoordinateInfo getGeoCoordinateInfo(double latitude, double longitude);
}
