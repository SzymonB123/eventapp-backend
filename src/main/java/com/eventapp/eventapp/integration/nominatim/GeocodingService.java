package com.eventapp.eventapp.integration.nominatim;

import com.eventapp.eventapp.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeocodingService {

    private final NominatimClient nominatimClient;

    public Optional<GeocodingResult> geocode(String location) {
        if (location == null || location.isBlank()) {
            return Optional.empty();
        }

        try {
            return nominatimClient.search(location);
        } catch (ExternalApiException exception) {
            log.warn("Geocoding failed for location: {}", location, exception);
            return Optional.empty();
        }
    }
}