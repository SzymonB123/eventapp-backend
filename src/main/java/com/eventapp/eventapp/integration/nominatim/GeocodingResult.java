package com.eventapp.eventapp.integration.nominatim;

public record GeocodingResult(
        String displayName,
        Double latitude,
        Double longitude
) {
}