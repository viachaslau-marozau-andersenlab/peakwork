package com.andersen.peakwork.challenge.controllers;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.services.interfaces.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
public class SearchController {

    @Autowired
    @Qualifier("searchService")
    private SearchService searchService;

    @GetMapping("/search-place")
    public ResponseEntity<?> getGeoCoordinateData(
            @RequestParam("country") @NotNull final String country
    ) {
        try {
            List<GeoCoordinateInfo> geoCoordinateData = searchService.getGeoCoordinateDataByCountryName(country);
            log.debug("/search-place?country={} geo coordinates list: {}", country, geoCoordinateData);
            return new ResponseEntity<>(geoCoordinateData, HttpStatus.OK);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search-place/{id}")
    public ResponseEntity<?> getGeoCoordinateInfo(
            @PathVariable @NotNull Long id
    ) {
        try {
            GeoCoordinateInfo geoCoordinateInfo = searchService.getGeoCoordinateInfoById(id);
            log.debug("/search-place/{} geo coordinate: {}", id, geoCoordinateInfo);
            return new ResponseEntity<>(geoCoordinateInfo, geoCoordinateInfo != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
