package ru.analysis.hotelling.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
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

        RealMatrix majorFactor = computeMajorFactor(reducedMatrix, 4);
        log.debug("Major Factor matrix R+: \n{}", tableMatrix(majorFactor));
    }

    protected RealMatrix computeMajorFactor( RealMatrix reducedMatrix, int iterationNum) {
        if (iterationNum <= 0)
            throw  new IllegalArgumentException("Iteration number might be more or equal one");

        RealMatrix tempMatrix = reducedMatrix.copy();

        RealMatrix sums = rowSumsMatrix(reducedMatrix);
        double max = maxVectorValue(sums);
        RealMatrix alphaPrev = divideVector(sums, max);
        RealMatrix alphaCurrent = alphaPrev;
        RealMatrix p;
        RealMatrix d;

        for (int i = 0; i < iterationNum; i++) {
            p = tempMatrix.multiply(sums);
            tempMatrix = tempMatrix.multiply(tempMatrix);
            sums = rowSumsMatrix(tempMatrix);
            max = maxVectorValue(p);
            alphaCurrent = divideVector(p, max);
            d = alphaCurrent.subtract(alphaPrev);
            alphaPrev = alphaCurrent;
        }

        RealMatrix A = computeVectorA(alphaPrev, reducedMatrix.multiply(alphaPrev));
        return reducedMatrix.subtract(A.multiply(A.transpose()));
    }

    protected RealMatrix computeVectorA(RealMatrix U, RealMatrix betta) {
        double maxBetta = maxVectorValue(betta);

        double sumSquaresU = IntStream.range(0, U.getRowDimension())
                .mapToDouble(i -> {
                    double x = U.getEntry(i, 0);
                    return x * x;
                })
                .sum();

        if (sumSquaresU == 0.0) {
            throw new IllegalArgumentException("Vector U norm is zero");
        }

        double scaleFactor = Math.sqrt(maxBetta / sumSquaresU);

        return U.scalarMultiply(scaleFactor);
    }

    protected RealMatrix divideVector(RealMatrix vector, double divisor) {
        RealMatrix result = vector.copy();
        for (int i = 0; i < result.getRowDimension(); i++) {
            result.setEntry(i, 0, result.getEntry(i, 0) / divisor);
        }
        return result;
    }

    protected double maxVectorValue(RealMatrix vector) {
        return IntStream.range(0, vector.getRowDimension())
                .mapToDouble(i -> vector.getEntry(i, 0))
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Empty vector"));
    }

    protected RealMatrix rowSumsMatrix(RealMatrix matrix) {
        double[] sums = IntStream.range(0, matrix.getRowDimension())
                .mapToDouble(i -> Arrays.stream(matrix.getRow(i)).sum())
                .toArray();

        // Возвращаем столбец Nx1
        return MatrixUtils.createColumnRealMatrix(sums);
    }

    protected RealMatrix computeReducedCorrelation(RealMatrix correlationMatrix) {
        RealMatrix reduced = correlationMatrix.copy();
        int n = correlationMatrix.getColumnDimension();

        IntStream.range(0, n)
                .parallel()
                .forEach(j -> {
                    double diagonalElement = correlationMatrix.getEntry(j, j);
                    double maxOffDiagonal = IntStream.range(0, n)
                            .mapToDouble(i -> correlationMatrix.getEntry(i, j))
                            .filter(val -> Math.abs(diagonalElement - val) > 1e-10)
                            .max()
                            .orElse(0.0); // Если Optional пуст возвращаем 0.0

                    reduced.setEntry(j, j, maxOffDiagonal);
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
        final int n = inputMatrix.getRowDimension();
        final int m = inputMatrix.getColumnDimension();

        RealMatrix centered = inputMatrix.copy();

        // 1. ТОЧНОЕ центрирование с Kahan
        for (int j = 0; j < m; j++) {
            double[] column = inputMatrix.getColumn(j);
            double sum = kahanSum(column);
            double mean = sum / n;

            // Центрируем столбец
            for (int i = 0; i < n; i++) {
                centered.setEntry(i, j, inputMatrix.getEntry(i, j) - mean);
            }
        }

        // 2. Ковариация (Commons Math уже использует стабильные алгоритмы)
        RealMatrix covariance = centered.transpose()
                .multiply(centered)
                .scalarMultiply(1.0 / (n - 1));

        // 3. Корреляция (параллельно, но без потоков для стабильности)
        RealMatrix correlation = covariance.copy();
        for (int i = 0; i < m; i++) {
            double stdI = Math.sqrt(covariance.getEntry(i, i));
            for (int j = 0; j < m; j++) {
                double stdJ = Math.sqrt(covariance.getEntry(j, j));
                correlation.setEntry(i, j,
                        (stdI == 0 || stdJ == 0) ? Double.NaN : covariance.getEntry(i, j) / (stdI * stdJ));
            }
        }

        return correlation;
    }

    private double kahanSum(double[] array) {
        double sum = 0.0;
        double compensation = 0.0; // c в алгоритме Kahan

        for (double x : array) {
            double y = x - compensation;
            double t = sum + y;
            compensation = (t - sum) - y; // сохраняем ошибку
            sum = t;
        }
        return sum;
    }

    protected String tableMatrix(RealMatrix matrix) {
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
