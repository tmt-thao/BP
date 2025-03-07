package src.object;

import java.io.*;

public class TimeMatrix {
    private int[][] matrix;
    
    public TimeMatrix(int size) {
        this.matrix = new int[size][size];
    }
    
    public static TimeMatrix loadFromFile(String filename, int size) throws IOException {
        TimeMatrix timeMatrix = new TimeMatrix(size);
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int row = 0;
        while ((line = br.readLine()) != null && row < size) {
            String[] parts = line.split(";");
            for (int col = 0; col < size; col++) {
                timeMatrix.matrix[row][col] = Integer.parseInt(parts[col]);
            }
            row++;
        }
        br.close();
        return timeMatrix;
    }
    
    public int getTravelTime(int fromIndex, int toIndex) {
        return matrix[fromIndex][toIndex];
    }
}
