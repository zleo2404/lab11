package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nThread;

    public MultiThreadedSumMatrix(int n){
        this.nThread=n;
    }



    private static class MatrixThread extends Thread {

        private final double[][]matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        MatrixThread(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            /*for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {

                for( double[] m : matrix){

                    this.res += m[i];

                }
                
            }*/
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (int j = 0; j < matrix[i].length; j++) { // Itera sulle colonne della riga `i`
                    this.res += matrix[i][j];
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }

    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % nThread + matrix.length / nThread;
        /*
         * Build a list of workers
         */
        final List<MatrixThread> workers = new ArrayList<>(nThread);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new MatrixThread(matrix, start, size));
        }
        /*
         * Start them
         */
        for (final MatrixThread w: workers) {
            w.start();
        }
        /*
         * Wait for every one of them to finish. This operation is _way_ better done by
         * using barriers and latches, and the whole operation would be better done with
         * futures.
         */
        double sum = 0;
        for (final MatrixThread w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        /*
         * Return the sum
         */
        return sum;
    }
    
}
