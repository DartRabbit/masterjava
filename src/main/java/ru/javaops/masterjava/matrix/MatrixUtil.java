package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        final ArrayList<Future<Void>> futureResults = new ArrayList<>();

        for (int i = 0; i< matrixSize; i++){
            int finalI = i;
            futureResults.add(completionService.submit(()->calcRow(finalI,matrixA,matrixB,matrixC),null));
        }

        while (!futureResults.isEmpty()) {
            futureResults.remove(completionService.poll());
        }

        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadOptimizedMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i< matrixSize; i++){
            calcRow(i,matrixA,matrixB,matrixC);
        }

        return matrixC;
    }

    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[i][k] * matrixB[k][j];
                }
                matrixC[i][j] = sum;
            }
        }
        return matrixC;
    }

    private static void calcRow(int rowNum, int[][] matrixA, int[][] matrixB, int[][] matrixC){
        final int matrixSize = matrixA.length;
        final int[] thatColumn = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            thatColumn[j] = matrixB[j][rowNum];
        }

        for (int k = 0; k < matrixSize; k++) {
            int[] thisRow = matrixA[k];
            int sum = 0;
            for (int l = 0; l < matrixSize; l++) {
                sum += thisRow[l]*thatColumn[l];
            }
            matrixC[k][rowNum] = sum;
        }
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
