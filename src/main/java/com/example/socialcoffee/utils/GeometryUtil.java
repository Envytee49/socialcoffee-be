package com.example.socialcoffee.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtil {

    public static final int SRID = 4326; //LatLng

    private static final double EARTH_RADIUS_KM = 6371.0;

    private static final WKTReader wktReader = new WKTReader();

    public static Point parseLocation(Double x,
                                      Double y) {
        if (ObjectUtils.anyNull(x,
                y)) return null;
        Geometry geometry = GeometryUtil.wktToGeometry(String.format("POINT (%s %s)",
                x,
                y));
        Point p = (Point) geometry;
        p.setSRID(SRID);
        return p;
    }

    private static Geometry wktToGeometry(String wellKnownText) {
        Geometry geometry = null;

        try {
            geometry = wktReader.read(wellKnownText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("###geometry :" + geometry);
        return geometry;
    }

    public static Double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (ObjectUtils.anyNull(lat1, lon1, lat2, lon2)) return null;
        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Differences in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Haversine formula
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;

        // Round to 2 decimal places
        return Math.round(distance * 100.0) / 100.0;
    }
}
