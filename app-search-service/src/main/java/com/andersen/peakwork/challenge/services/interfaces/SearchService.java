package com.andersen.peakwork.challenge.services.interfaces;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;

import java.util.List;

public interface SearchService {

    /**
     * Get geo coordinate by place id
     *
     * @param placeId place id
     * @return information about place
     */
    GeoCoordinateInfo getGeoCoordinateInfoById(long placeId);

    /**
     * Get geo coordinates filtered by country
     *
     * @param countryName country name
     * @return information about places
     */
    List<GeoCoordinateInfo> getGeoCoordinateDataByCountryName(String countryName);
}
