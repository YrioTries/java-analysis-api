package ru.analysis.hotelling.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.analysis.hotelling.dto.HotellingRequest;
import ru.analysis.hotelling.service.HotellingService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/hotelling")
public class HotellingController {
    private final HotellingService hotellingService;

    @GetMapping
    public void getHotellingModel(HotellingRequest hotellingRequest) {
        hotellingService.getHotellingModel(hotellingRequest);
    }
}
