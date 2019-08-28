package com.andersen.peakwork.challenge.third.party.connectors.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NominatimDTO {

    @JsonProperty(value = "place_id")
    private Long placeId;

    @JsonProperty(value = "osm_type")
    private String osmType;

    @JsonProperty(value = "osm_id")
    private Long osmId;

    @JsonProperty(value = "lat")
    private Double lat;

    @JsonProperty(value = "lon")
    private Double lon;

    @JsonProperty(value = "display_name")
    private String displayName;

    @JsonProperty(value = "type")
    private String type;

    @JsonProperty(value = "address")
    private AddressDTO address;
}
