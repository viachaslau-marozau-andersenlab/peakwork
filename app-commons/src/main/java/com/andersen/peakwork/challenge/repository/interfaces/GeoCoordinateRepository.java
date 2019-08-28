package com.andersen.peakwork.challenge.repository.interfaces;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;

import java.util.Iterator;
import java.util.List;

public interface GeoCoordinateRepository {

    /**
     * Get geo coordinate by place id
     *
     * @param placeId place id
     * @return information about place
     */
    GeoCoordinateInfo getGeoCoordinateInfoByPlaceId(long placeId);

    /**
     * Get geo coordinates filtered by country
     *
     * @param countryName country name
     * @return information about places
     */
    List<GeoCoordinateInfo> getGeoCoordinateDataByCountryName(String countryName);

    /**
     * Store geo coordinate info
     *
     * @param geoCoordinateInfo information about place
     */
    void storeGeoCoordinateInfo(GeoCoordinateInfo geoCoordinateInfo);

    /**
     * Create scroll iterator for all  geo coordinate data
     *
     * @return scroll iterator
     */
    Iterator<GeoCoordinateInfo> getGeoCoordinateInfoScrollIterator();

    /**
     * Update information about places
     *
     * @return number of updated entities
     */
    long updateGeoCoordinateData(List<GeoCoordinateInfo> geoCoordinateData);
}
