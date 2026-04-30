package ru.analysis.hotelling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.analysis.hotelling.dto.HotellingRequest;

import java.text.NumberFormat;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotellingServiceImpl {
    public void getHotellingModel(HotellingRequest hotellingRequest) {
        RealMatrix matrix = fromRequest(hotellingRequest);
        log.debug("Matrix: \n{}", tableMatrix(matrix));

        matrix = computeCorrelation(matrix);
        log.debug("Correlation matrix: \n{}", tableMatrix(matrix));
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

    public RealMatrix computeCorrelation(RealMatrix inputMatrix) {
       PearsonsCorrelation pearsonsCorrelation = new PearsonsCorrelation(inputMatrix);
        return pearsonsCorrelation.getCorrelationMatrix();
    }

    private String prettyMatrix(RealMatrix matrix) {
        // NumberFormat с точкой и 2 знаками
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        nf.setGroupingUsed(false);  // Без запятых тысяч
        nf.setMaximumFractionDigits(3);

        // MatrixFormat
        RealMatrixFormat fmt = new RealMatrixFormat("  ", "", "", "", " \n ", "|", nf);

        return String.format("Matrix [%dx%d]:\n%s",
                matrix.getRowDimension(), matrix.getColumnDimension(),
                fmt.format(matrix));
    }

    private String tableMatrix(RealMatrix matrix) {
        StringBuilder sb = new StringBuilder();
        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        int width = 15;  // 14 (число) + 1 (│) = 15!

        // Верх рамки
        sb.append("┌").append("─".repeat(width * cols + 1)).append("┐\n");

        // Строки
        for (int i = 0; i < rows; i++) {
            sb.append("│");  // Первый |
            for (int j = 0; j < cols; j++) {
                double val = matrix.getEntry(i, j);
                sb.append(String.format("%14.3f│", val));  // 15 символов!
            }
            sb.append("\n");

            // Разделитель
            if (i < rows - 1) {
                sb.append("├").append("─".repeat(width * cols + 1)).append("┤\n");
            }
        }

        // Низ рамки
        sb.append("└").append("─".repeat(width * cols + 1)).append("┘\n");

        return sb.toString();
    }


}
