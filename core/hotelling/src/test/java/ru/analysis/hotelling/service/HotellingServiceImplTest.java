package ru.analysis.hotelling.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.analysis.hotelling.dto.HotellingRequest;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class HotellingServiceImplTest {

    @InjectMocks
    private HotellingServiceImpl service;

    public HotellingRequest createTestRequest() {
        Map<String, Map<String, Double>> matrix = new HashMap<>();

        matrix.put("Владимирская область", Map.of(
                "avgSalary", 37.708,
                "foodCost", 4.8319375,
                "creditDebt", 140_771_000.0,
                "domesticTours", 331_880.0,
                "foreignTours", 848_874.6,
                "exportAgreements", 3_330_000.0,
                "importAgreements", 7_326_000.0
        ));

        matrix.put("Московская область", Map.of(
                "avgSalary", 63.414,
                "foodCost", 5.158864167,
                "creditDebt", 1_619_815_000.0,
                "domesticTours", 2_102_160.0,
                "foreignTours", 8_999_312.5,
                "exportAgreements", 12_284_000.0,
                "importAgreements", 22_422_000.0
        ));

        matrix.put("Рязанская область", Map.of(
                "avgSalary", 40.282,
                "foodCost", 4.5594783,
                "creditDebt", 133_595_000.0,
                "domesticTours", 229_680.0,
                "foreignTours", 1_097_486.8,
                "exportAgreements", 3_700_000.0,
                "importAgreements", 3_774_000.0
        ));

        matrix.put("Ленинградская область", Map.of(
                "avgSalary", 52.513,
                "foodCost", 5.562871667,
                "creditDebt", 319_378_000.0,
                "domesticTours", 342_240.0,
                "foreignTours", 1_101_846.1,
                "exportAgreements", 1_332_000.0,
                "importAgreements", 17_834_000.0
        ));

        matrix.put("Новгородская область", Map.of(
                "avgSalary", 39.473,
                "foodCost", 5.1330742,
                "creditDebt", 68_507_000.0,
                "domesticTours", 83_920.0,
                "foreignTours", 177_146.1,
                "exportAgreements", 888_000.0,
                "importAgreements", 1_036_000.0
        ));

        matrix.put("Краснодарский край", Map.of(
                "avgSalary", 40.955,
                "foodCost", 5.067718333,
                "creditDebt", 685_054_000.0,
                "domesticTours", 6_978_080.0,
                "foreignTours", 6_151_104.4,
                "exportAgreements", 3_478_000.0,
                "importAgreements", 10_508_000.0
        ));

        matrix.put("Ставропольский край", Map.of(
                "avgSalary", 37.09,
                "foodCost", 4.824344167,
                "creditDebt", 279_964_000.0,
                "domesticTours", 2_030_600.0,
                "foreignTours", 1_005_413.1,
                "exportAgreements", 1_480_000.0,
                "importAgreements", 3_256_000.0
        ));

        matrix.put("Самарская область", Map.of(
                "avgSalary", 42.729,
                "foodCost", 4.846261667,
                "creditDebt", 382_883_000.0,
                "domesticTours", 2_421_640.0,
                "foreignTours", 3_344_772.0,
                "exportAgreements", 10_434_000.0,
                "importAgreements", 12_210_000.0
        ));

        matrix.put("Тюменская область", Map.of(
                "avgSalary", 83.771,
                "foodCost", 5.6789425,
                "creditDebt", 917_536_000.0,
                "domesticTours", 1_005_440.0,
                "foreignTours", 5_153_088.9,
                "exportAgreements", 1_998_000.0,
                "importAgreements", 10_434_000.0
        ));

        matrix.put("Новосибирская область", Map.of(
                "avgSalary", 46.444,
                "foodCost", 5.01774,
                "creditDebt", 433_993_000.0,
                "domesticTours", 947_400.0,
                "foreignTours", 3_038_564.2,
                "exportAgreements", 19_610_000.0,
                "importAgreements", 12_432_000.0
        ));

        matrix.put("Сахалинская область", Map.of(
                "avgSalary", 94.587,
                "foodCost", 6.782341667,
                "creditDebt", 98_577_000.0,
                "domesticTours", 93_360.0,
                "foreignTours", 225_758.9,
                "exportAgreements", 370_000.0,
                "importAgreements", 666_000.0
        ));

        return HotellingRequest.builder()
                .name("Анализ регионов ЦФО и СФО 2021")
                .matrix(matrix)
                .build();
    }

    @Test
    void getHotellingModel_shouldCreateMatrixAndComputeCorrelation() {
        System.setProperty("logging.level.ru.analysis.hotelling", "DEBUG");
        HotellingRequest request = createTestRequest();

        service.getHotellingModel(request);
    }

}
