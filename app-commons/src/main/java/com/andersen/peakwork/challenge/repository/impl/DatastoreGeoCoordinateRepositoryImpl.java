package com.andersen.peakwork.challenge.repository.impl;

import com.andersen.peakwork.challenge.model.GeoCoordinateInfo;
import com.andersen.peakwork.challenge.repository.interfaces.GeoCoordinateRepository;
import com.google.cloud.datastore.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.google.cloud.datastore.StructuredQuery.PropertyFilter.eq;

@Slf4j
@Repository
public class DatastoreGeoCoordinateRepositoryImpl implements GeoCoordinateRepository {

    private static final int BATCH_SIZE = 500;
    private static final String DATASTORE_KIND = "GeoCoordinateInfo";

    private static final String PLACE_ID_PROPERTY = "place_id";
    private static final String LATITUDE_PROPERTY = "latitude";
    private static final String LONGITUDE_PROPERTY = "longitude";
    private static final String COUNTRY_PROPERTY = "country";
    private static final String DISPLAY_NAME_PROPERTY = "display_name";
    private static final String TYPE_PROPERTY = "type";
    private static final String OMS_ID_PROPERTY = "oms_id";
    private static final String OMS_TYPE_PROPERTY = "oms_type";

    @Autowired
    @Qualifier("datastore")
    private Datastore datastore;
    private KeyFactory keyFactory;

    @PostConstruct
    public void init() {
        keyFactory = datastore.newKeyFactory().setKind(DATASTORE_KIND);
    }

    @Override
    public GeoCoordinateInfo getGeoCoordinateInfoByPlaceId(long placeId) {
        Key key = keyFactory.newKey(placeId);
        Entity entity = datastore.get(key);
        if (entity != null) {
            return convertToGeoCoordinateInfo(entity);
        }
        return null;
    }

    @Override
    public List<GeoCoordinateInfo> getGeoCoordinateDataByCountryName(String countryName) {
        log.debug("call getGeoCoordinateDataByCountryName({})", countryName);
        List<GeoCoordinateInfo> result = new ArrayList<>();
        EntityQuery query = Query.newEntityQueryBuilder()
                .setKind(DATASTORE_KIND)
                .setFilter(eq(COUNTRY_PROPERTY, countryName))
                .build();
        QueryResults<Entity> entities = datastore.run(query);

        while (entities.hasNext()) {
            Entity entity = entities.next();
            result.add(convertToGeoCoordinateInfo(entity));
        }
        log.debug("getGeoCoordinateDataByCountryName({}) result: {}",countryName, result);
        return result;
    }

    @Override
    public void storeGeoCoordinateInfo(GeoCoordinateInfo geoCoordinateInfo) {
        log.debug("call storeGeoCoordinateInfo({})", geoCoordinateInfo);
        Key key = keyFactory.newKey(geoCoordinateInfo.getPlaceId());
        Entity entity = convertToEntity(key, geoCoordinateInfo);
        datastore.put(entity);
    }

    @Override
    public Iterator<GeoCoordinateInfo> getGeoCoordinateInfoScrollIterator() {
        return new GeoCoordinateInfoIterator(datastore);
    }

    @Override
    public long updateGeoCoordinateData(List<GeoCoordinateInfo> geoCoordinateData) {
        List<Entity> entities = new ArrayList<>();
        for (GeoCoordinateInfo geoCoordinateInfo : geoCoordinateData) {
            Key key = keyFactory.newKey(geoCoordinateInfo.getPlaceId());
            Entity entity = convertToEntity(key, geoCoordinateInfo);
            entities.add(entity);
        }
        List<Entity> batchUpdate = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            if (i > 0 && i % BATCH_SIZE == 0) {
                datastore.update(batchUpdate.toArray(new Entity[0]));
                batchUpdate.clear();
            }
            batchUpdate.add(entities.get(i));
        }
        datastore.update(batchUpdate.toArray(new Entity[0]));
        return entities.size();
    }

    private GeoCoordinateInfo convertToGeoCoordinateInfo(Entity entity) {
        return GeoCoordinateInfo.builder()
                .placeId(entity.getLong(PLACE_ID_PROPERTY))
                .latitude(entity.getDouble(LATITUDE_PROPERTY))
                .longitude(entity.getDouble(LONGITUDE_PROPERTY))
                .country(entity.getString(COUNTRY_PROPERTY))
                .displayName(entity.getString(DISPLAY_NAME_PROPERTY))
                .type(entity.getString(TYPE_PROPERTY))
                .osmId(entity.getLong(OMS_ID_PROPERTY))
                .osmType(entity.getString(OMS_TYPE_PROPERTY))
                .build();
    }

    private Entity convertToEntity(Key key, GeoCoordinateInfo geoCoordinateInfo) {
        return Entity.newBuilder(key)
                .set(PLACE_ID_PROPERTY, geoCoordinateInfo.getPlaceId())
                .set(LATITUDE_PROPERTY, geoCoordinateInfo.getLatitude())
                .set(LONGITUDE_PROPERTY, geoCoordinateInfo.getLongitude())
                .set(COUNTRY_PROPERTY, geoCoordinateInfo.getCountry())
                .set(DISPLAY_NAME_PROPERTY, geoCoordinateInfo.getDisplayName())
                .set(TYPE_PROPERTY, geoCoordinateInfo.getType() != null ? geoCoordinateInfo.getType() : "")
                .set(OMS_ID_PROPERTY, geoCoordinateInfo.getOsmId())
                .set(OMS_TYPE_PROPERTY, geoCoordinateInfo.getOsmType())
                .build();
    }

    private class GeoCoordinateInfoIterator implements Iterator<GeoCoordinateInfo> {

        private QueryResults<Entity> innerIterator;
        private Datastore datastore;

        private GeoCoordinateInfoIterator(Datastore datastore) {
            this.datastore = datastore;
        }

        @Override
        public boolean hasNext() {
            if (innerIterator != null && innerIterator.hasNext()) {
                return true;
            }
            Cursor startCursor = null;
            if (innerIterator != null) {
                startCursor = innerIterator.getCursorAfter();
            }
            Query<Entity> query = Query.newEntityQueryBuilder()
                    .setKind(DATASTORE_KIND)
                    .setLimit(BATCH_SIZE)
                    .setStartCursor(startCursor)
                    .build();
            QueryResults<Entity> result = datastore.run(query);
            innerIterator = result;
            return innerIterator.hasNext();
        }

        @Override
        public GeoCoordinateInfo next() {
            hasNext();
            return convertToGeoCoordinateInfo(innerIterator.next());
        }
    }
}
