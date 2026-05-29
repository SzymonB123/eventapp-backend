package com.eventapp.eventapp.integration.nominatim;

import com.eventapp.eventapp.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class NominatimClient {

    private final RestClient.Builder restClientBuilder;

    @Value("${external.nominatim.base-url}")
    private String baseUrl;

    @Value("${external.nominatim.user-agent}")
    private String userAgent;

    public Optional<GeocodingResult> search(String query) {
        try {
            RestClient restClient = restClientBuilder
                    .baseUrl(baseUrl)
                    .defaultHeader("User-Agent", userAgent)
                    .build();

            List<NominatimSearchResult> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", query)
                            .queryParam("format", "json")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ExternalApiException(
                                "Nominatim API returned error status: " + response.getStatusCode()
                        );
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (results == null || results.isEmpty()) {
                return Optional.empty();
            }

            NominatimSearchResult firstResult = results.get(0);

            return Optional.of(new GeocodingResult(
                    firstResult.displayName(),
                    Double.parseDouble(firstResult.latitude()),
                    Double.parseDouble(firstResult.longitude())
            ));
        } catch (RestClientException exception) {
            throw new ExternalApiException("Could not connect to Nominatim API", exception);
        } catch (NumberFormatException exception) {
            throw new ExternalApiException("Invalid coordinates returned by Nominatim API", exception);
        }
    }
}