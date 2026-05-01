package ru.analysis.hotelling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.analysis.hotelling.dto.HotellingRequest;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotellingServiceImpl {
    public void getHotellingModel(HotellingRequest hotellingRequest) {
        RealMatrix matrix = fromRequest(hotellingRequest);
        log.debug("Input matrix A: \n{}", tableMatrix(matrix));

        RealMatrix correlationMatrix = computeCorrelation(matrix);
        log.debug("Correlation matrix C: \n{}", tableMatrix(correlationMatrix));

        // Используем факторный анализ: Метод наибольшей корреляции
        RealMatrix reducedMatrix = computeReducedCorrelation(correlationMatrix);
        log.debug("Reduced matrix R: \n{}", tableMatrix(reducedMatrix));
    }

    protected RealMatrix computeReducedCorrelation(RealMatrix corrMatrix) {
        RealMatrix reduced = corrMatrix.copy();
        int n = corrMatrix.getColumnDimension();

        IntStream.range(0, n)
                .parallel()  // Параллельно!
                .forEach(i -> {
                    double diag = corrMatrix.getEntry(i, i);
                    double maxOffDiag = IntStream.range(0, n)
                            .mapToDouble(j -> corrMatrix.getEntry(j, i))
                            .filter(val -> Math.abs(diag - val) > 1e-10)
                            .max()
                            .orElse(0.0);

                    reduced.setEntry(i, i, maxOffDiag);
                });

        return reduced;
    }


    protected RealMatrix fromRequest(HotellingRequest request) {
        if (request.getMatrix().isEmpty()) {
            throw new IllegalArgumentException("Matrix cannot be empty");
        }

        String[] paramNames = request.getMatrix().values().stream()
                .findFirst()               // Достаём первую строку матрицы || Optional<Map<String, Double>>
                .map(Map::keySet)         // Чтобы узнать имена столбцов в матрице || Optional<Set<String>>
                .stream()                // Распаковываем из Optional
                .flatMap(Set::stream)   // Превращаем Stream<Set<String>> в Stream<String>
                .sorted()              // Сортируем по алфавиту
                .toArray(String[]::new);
        log.debug("Sorted paramNames: {}", Arrays.toString(paramNames));

        List<Map<String, Double>> rows = new ArrayList<>(request.getMatrix().values());
        int numRows = rows.size();

        double[][] matrixData = rows.stream()
                .map(rowMap -> Arrays.stream(paramNames)
                        .mapToDouble(rowMap::get)
                        .toArray())
                .toArray(double[][]::new);

        log.debug("Matrix shape: {}x{}", matrixData.length, matrixData[0].length);
        return MatrixUtils.createRealMatrix(matrixData);
    }

    protected RealMatrix computeCorrelation(RealMatrix inputMatrix) {
       PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation(inputMatrix);
        return pearsonsCorrelation.getCorrelationMatrix();
    }

    private String tableMatrix(RealMatrix matrix) {
        StringBuilder sb = new StringBuilder();
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        int width = 15;

        sb.append("┌").append("─".repeat(width * cols + 1)).append("┐\n");

        for (int i = 0; i < rows; i++) {
            sb.append("│");  // Первый |
            for (int j = 0; j < cols; j++) {
                double val = matrix.getEntry(i, j);
                sb.append(String.format("%14.3f│", val));  // 15 символов!
            }
            sb.append("\n");

            if (i < rows - 1) {
                sb.append("├").append("─".repeat(width * cols + 1)).append("┤\n");
            }
        }

        sb.append("└").append("─".repeat(width * cols + 1)).append("┘\n");

        return sb.toString();
    }


}
