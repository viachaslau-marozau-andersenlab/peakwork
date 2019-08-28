package com.andersen.peakwork.challenge.third.party.connectors.interfaces;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;

public interface NominatimConnector {

    /**
     * Get geo coordinate information by osmId
     *
     * @param placeId place id
     * @param osmId   osm id
     * @return information about place
     */
    GeoCoordinateInfo getGeoCoordinateInfoByFullOsmId(long placeId, String osmId);

    /**
     * Get geo coordinate information by latitude and longitude
     *
     * @param latitude  place latitude
     * @param longitude place longitude
     * @return information about place
     */
    GeoCoordinateInfo getGeoCoordinateInfoByGeoCoordinate(double latitude, double longitude);
}
