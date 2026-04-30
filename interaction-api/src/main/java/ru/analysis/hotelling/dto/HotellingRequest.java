package ru.analysis.hotelling.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotellingRequest {
    String name;
    Map<String, Map<String, Double>> matrix;
}
