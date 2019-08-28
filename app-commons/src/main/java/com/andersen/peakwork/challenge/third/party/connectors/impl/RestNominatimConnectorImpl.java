package com.andersen.peakwork.challenge.third.party.connectors.impl;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.third.party.connectors.dto.NominatimDTO;
import com.andersen.peakwork.challenge.third.party.connectors.interfaces.NominatimConnector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
public class RestNominatimConnectorImpl implements NominatimConnector {

    private final static String URI_REVERSE = "/reverse";
    private final static String URI_LOOKUP = "/lookup";

    private final static String LATITUDE_PARAM = "lat";
    private final static String LONGITUDE_PARAM = "lon";
    private final static String FORMAT_PARAM = "format";
    private final static String LANGUAGE_PARAM = "accept-language";
    private final static String OSM_IDS_PARAM = "osm_ids";

    private final static String FORMAT_VALUE = "json";
    private final static String LANGUAGE_VALUE = "en";
    private final static String USER_AGENT_VALUE = "andersen-peakwork-service";

    @Value("${nominatim.url}")
    private String url;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public GeoCoordinateInfo getGeoCoordinateInfoByFullOsmId(long placeId, String osmId) {
        log.debug("call getGeoCoordinateInfoByFullOsmId({}, {})", placeId, osmId);
        ParameterizedTypeReference<List<NominatimDTO>> typeReference = new ParameterizedTypeReference<List<NominatimDTO>>() {
        };
        ResponseEntity<List> response = sendGetQuery(buildOsmUrlString(osmId), typeReference);
        List<NominatimDTO> dtoList = response.getBody();
        log.debug("NominatimDTO list size: {})", dtoList != null ? dtoList.size() : "null");
        if (dtoList != null && dtoList.size() > 0) {
            for (NominatimDTO nominatimDTO : dtoList) {
                if (nominatimDTO.getPlaceId().equals(placeId)) {
                    GeoCoordinateInfo geoCoordinateInfo = convertDtoToGeoCoordinateInfo(nominatimDTO);
                    log.debug("getGeoCoordinateInfoByFullOsmId({}, {}) result: {}", placeId, osmId, geoCoordinateInfo);
                    return geoCoordinateInfo;
                }
            }
        }
        log.debug("getGeoCoordinateInfoByFullOsmId({}, {}) don't find anything", placeId, osmId);
        return null;
    }

    @Override
    public GeoCoordinateInfo getGeoCoordinateInfoByGeoCoordinate(double latitude, double longitude) {
        log.debug("call getGeoCoordinateInfoByGeoCoordinate({}, {})", latitude, longitude);
        ParameterizedTypeReference<NominatimDTO> typeReference = new ParameterizedTypeReference<NominatimDTO>() {
        };
        ResponseEntity<NominatimDTO> response = sendGetQuery(buildGeoCoordinateUrlString(latitude, longitude), typeReference);
        GeoCoordinateInfo geoCoordinateInfo = response.getBody() != null ? convertDtoToGeoCoordinateInfo(response.getBody()) : null;
        log.debug("getGeoCoordinateInfoByGeoCoordinate({}, {}) result: {}", latitude, longitude, geoCoordinateInfo);
        return geoCoordinateInfo;
    }

    private GeoCoordinateInfo convertDtoToGeoCoordinateInfo(NominatimDTO dto) {
        return GeoCoordinateInfo.builder()
                .placeId(dto.getPlaceId())
                .latitude(dto.getLat())
                .longitude(dto.getLon())
                .country(dto.getAddress() != null ? dto.getAddress().getCountry() : null)
                .displayName(dto.getDisplayName())
                .type(dto.getType())
                .osmId(dto.getOsmId())
                .osmType(dto.getOsmType())
                .build();
    }

    private String buildGeoCoordinateUrlString(double latitude, double longitude) {
        String query = UriComponentsBuilder.fromHttpUrl(url + URI_REVERSE)
                .queryParam(FORMAT_PARAM, FORMAT_VALUE)
                .queryParam(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .queryParam(LATITUDE_PARAM, latitude)
                .queryParam(LONGITUDE_PARAM, longitude)
                .toUriString();
        log.info("Third party system query: {}", query);
        return query;
    }

    private String buildOsmUrlString(String osmId) {
        String query = UriComponentsBuilder.fromHttpUrl(url + URI_LOOKUP)
                .queryParam(FORMAT_PARAM, FORMAT_VALUE)
                .queryParam(LANGUAGE_PARAM, LANGUAGE_VALUE)
                .queryParam(OSM_IDS_PARAM, osmId)
                .toUriString();
        log.info("Third party system query: {}", query);
        return query;
    }

    private HttpEntity getHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, USER_AGENT_VALUE);
        return new HttpEntity<>(headers);
    }

    private <T> ResponseEntity<T> sendGetQuery(String url, ParameterizedTypeReference typeReference) {
        return restTemplate.exchange(url, HttpMethod.GET, getHeaders(), typeReference);
    }
}
