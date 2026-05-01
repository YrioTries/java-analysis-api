package ru.analysis.hotelling.service;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.analysis.hotelling.dto.HotellingRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
    void getHotellingModel_shouldShowComputingResults() {
        HotellingRequest request = createTestRequest();

        service.getHotellingModel(request);
    }

    @Test
    void computeMatrixCorrelation_shouldShowResults() {
        double[][] dataForOriginalMatrix = {
         //     avgSalary  foodCost    creditDebt        domesticTours  foreignTours  exportAgreements  importAgreements
                {37.708,  4.8319375,     140_771_000.0,    331_880.0,     848_874.6,   3_330_000.0,      7_326_000.0},
                {63.414,  5.158864167, 1_619_815_000.0,  2_102_160.0,   8_999_312.5,   12_284_000.0,    22_422_000.0},
                {40.282,  4.5594783,     133_595_000.0,    229_680.0,   1_097_486.8,   3_700_000.0,      3_774_000.0},
                {52.513,  5.562871667,   319_378_000.0,    342_240.0,   1_101_846.1,   1_332_000.0,     17_834_000.0},
                {39.473,  5.1330742,      68_507_000.0,     83_920.0,     177_146.1,     888_000.0,      1_036_000.0},
                {40.955,  5.067718333,   685_054_000.0,   6_978_080.0,  6_151_104.4,   3_478_000.0,     10_508_000.0},
                {37.09,   4.824344167,   279_964_000.0,   2_030_600.0,  1_005_413.1,   1_480_000.0,      3_256_000.0},
                {42.729,  4.846261667,   382_883_000.0,   2_421_640.0,  3_344_772.0,  10_434_000.0,     12_210_000.0},
                {83.771,  5.6789425,     917_536_000.0,   1_005_440.0,  5_153_088.9,   1_998_000.0,     10_434_000.0},
                {46.444,  5.01774,       433_993_000.0,     947_400.0,  3_038_564.2,   19_610_000.0,    12_432_000.0},
                {94.587,  6.782341667,    98_577_000.0,      93_360.0,    225_758.9,      370_000.0,       666_000.0}
        };
        RealMatrix originalMatrix = MatrixUtils.createRealMatrix(dataForOriginalMatrix);

        double[][] dataForCorrelation = {
                { 1.0,     0.882,  0.247,  -0.3,     0.124,  -0.188,   0.015},
                { 0.882,   1.0,   -0.062,  -0.247,  -0.163,  -0.350,  -0.147},
                { 0.247,  -0.062,  1.0,     0.354,   0.951,   0.386,   0.760},
                {-0.3,    -0.274,  0.354,   1.0,     0.574,   0.085,   0.239},
                { 0.124,  -0.163,  0.951,   0.574,   1.0,     0.446,   0.727},
                {-0.188,  -0.350,  0.386,   0.085,   0.446,   1.0,     0.529},
                { 0.015,  -0.147,  0.760,   0.239,   0.727,   0.529,   1.0  }
        };
        RealMatrix rightCorrelationMatrix = MatrixUtils.createRealMatrix(dataForCorrelation);

        RealMatrix testCorrelationMatrix = service.computeCorrelation(originalMatrix);
        System.out.println(service.tableMatrix(rightCorrelationMatrix));
        System.out.println(service.tableMatrix(testCorrelationMatrix));

        assertEquals(rightCorrelationMatrix, testCorrelationMatrix,
                "❌ Correlation transform of original matrix is NOT SUCCESS");
        System.out.println("✅ Correlation transform of original matrix is SUCCESSFUL");

    }

    @Test
    void computeReducedCorrelation_shouldShowResults() {
        double[][] dataForCorrelation = {
                { 1.0,     0.882,  0.247,  -0.3,     0.124,  -0.188,   0.015},
                { 0.882,   1.0,   -0.062,  -0.247,  -0.163,  -0.350,  -0.147},
                { 0.247,  -0.062,  1.0,     0.354,   0.951,   0.386,   0.760},
                {-0.3,    -0.274,  0.354,   1.0,     0.574,   0.085,   0.239},
                { 0.124,  -0.163,  0.951,   0.574,   1.0,     0.446,   0.727},
                {-0.188,  -0.350,  0.386,   0.085,   0.446,   1.0,     0.529},
                { 0.015,  -0.147,  0.760,   0.239,   0.727,   0.529,   1.0  }
        };
        RealMatrix correlationMatrix = MatrixUtils.createRealMatrix(dataForCorrelation);

        double[][] dataForReduced = {
                { 0.882,   0.882,  0.247,  -0.3,     0.124,  -0.188,   0.015},
                { 0.882,   0.882, -0.062,  -0.247,  -0.163,  -0.350,  -0.147},
                { 0.247,  -0.062,  0.951,   0.354,   0.951,   0.386,   0.760},
                {-0.3,    -0.274,  0.354,   0.574,   0.574,   0.085,   0.239},
                { 0.124,  -0.163,  0.951,   0.574,   0.951,   0.446,   0.727},
                {-0.188,  -0.350,  0.386,   0.085,   0.446,   0.529,   0.529},
                { 0.015,  -0.147,  0.760,   0.239,   0.727,   0.529,   0.760  }
        };
        RealMatrix rightReducedMatrix = MatrixUtils.createRealMatrix(dataForReduced);

        RealMatrix testReducedMatrix = service.computeReducedCorrelation(correlationMatrix);

        assertEquals(rightReducedMatrix, testReducedMatrix,
                "❌ Reduce transform of correlation matrix is NOT SUCCESS");
        System.out.println("✅ Reduce transform of correlation matrix is SUCCESSFUL");

    }
}
