package com.andersen.peakwork.challenge.controllers;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.services.interfaces.GeoCoordinateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
public class GeoCoordinateController {

    @Autowired
    @Qualifier("geoCoordinateService")
    private GeoCoordinateService geoCoordinateService;

    @GetMapping("/geo-coordinate-info")
    public ResponseEntity<?> resolveGeoCoordinate(
            @RequestParam("lat") @NotNull final Double latitude,
            @RequestParam("lon") @NotNull final Double longitude
    ) {
        try {
            GeoCoordinateInfo geoCoordinateInfo = geoCoordinateService.getGeoCoordinateInfo(latitude, longitude);
            log.debug("/geo-coordinate-info?lat={}&lon=${} geo coordinate: {}", latitude, longitude, geoCoordinateInfo);
            return new ResponseEntity<>(geoCoordinateInfo, geoCoordinateInfo != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
