package com.andersen.peakwork.challenge.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Builder
@Getter
@ToString
public class GeoCoordinateInfo {

    @NonNull
    private Long placeId;

    @NonNull
    private Double latitude;

    @NonNull
    private Double longitude;

    @NonNull
    private String country;

    @NonNull
    private String displayName;

    private String type;

    @NonNull
    private Long osmId;

    @NonNull
    private String osmType;
}
