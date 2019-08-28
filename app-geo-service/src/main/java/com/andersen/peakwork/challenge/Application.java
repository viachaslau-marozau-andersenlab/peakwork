package com.andersen.peakwork.challenge;

import com.andersen.peakwork.challenge.repository.impl.DatastoreGeoCoordinateRepositoryImpl;
import com.andersen.peakwork.challenge.repository.interfaces.GeoCoordinateRepository;
import com.andersen.peakwork.challenge.services.impl.GeoCoordinateServiceImpl;
import com.andersen.peakwork.challenge.services.interfaces.GeoCoordinateService;
import com.andersen.peakwork.challenge.third.party.connectors.impl.RestNominatimConnectorImpl;
import com.andersen.peakwork.challenge.third.party.connectors.interfaces.NominatimConnector;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public Datastore datastore() {
        return DatastoreOptions.getDefaultInstance().getService();
    }

    @Bean
    public GeoCoordinateRepository repository() {
        return new DatastoreGeoCoordinateRepositoryImpl();
    }

    @Bean
    public NominatimConnector connector() {
        return new RestNominatimConnectorImpl();
    }

    @Bean
    public GeoCoordinateService geoCoordinateService() {
        return new GeoCoordinateServiceImpl();
    }
}
