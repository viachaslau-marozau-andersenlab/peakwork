package com.andersen.peakwork.challenge.services.impl;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.repository.interfaces.GeoCoordinateRepository;
import com.andersen.peakwork.challenge.services.interfaces.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    @Qualifier("repository")
    private GeoCoordinateRepository repository;

    @Override
    public GeoCoordinateInfo getGeoCoordinateInfoById(long placeId) {
        return repository.getGeoCoordinateInfoByPlaceId(placeId);
    }

    @Override
    public List<GeoCoordinateInfo> getGeoCoordinateDataByCountryName(String countryName) {
        return repository.getGeoCoordinateDataByCountryName(countryName);
    }
}
