package com.eventapp.eventapp.integration.nominatim;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NominatimSearchResult(
        @JsonProperty("display_name")
        String displayName,

        @JsonProperty("lat")
        String latitude,

        @JsonProperty("lon")
        String longitude
) {
}