package com.instagram.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private Double latitude;
    private Double longitude;
    private String placeName;
}
