package src.test;

import src.algorithm.MemeticAlgorithm;
import src.dataLoader.DataLoader;
import src.object.Trip;
import src.object.TimeMatrix;
import src.object.Stop;

import java.io.IOException;
import java.util.List;

public class MemeticAlgorithmTest {
    public static void main(String[] args) {
        System.out.println("hi");

        try {
            List<Trip> trips = DataLoader.loadTrips("C:\\Users\\vtmtt\\OneDrive - Žilinská univerzita v Žiline\\6. sem\\bp\\BP\\data\\spoje_id_B1_3.csv");
            List<Stop> stops = DataLoader.loadStops("C:\\Users\\vtmtt\\OneDrive - Žilinská univerzita v Žiline\\6. sem\\bp\\BP\\data\\ZastavkyAll.csv");
            TimeMatrix timeMatrix = DataLoader.loadTimeMatrix("C:\\Users\\vtmtt\\OneDrive - Žilinská univerzita v Žiline\\6. sem\\bp\\BP\\data\\matrixTime.txt", stops.size());

            int populationSize = 50;
            int generations = 200;
            double mutationRate = 0.05;
            MemeticAlgorithm algorithm = new MemeticAlgorithm(trips, timeMatrix, stops, populationSize, generations, mutationRate);

            long startTime = System.nanoTime();

            algorithm.initializePopulation();

            algorithm.evolve();

            long endTime = System.nanoTime();
            double duration = (endTime - startTime) / 1_000_000_000.0;

            int[] bestSolution = algorithm.getBestSolution();
            System.out.println("Najlepšie riešenie: ");
            for (int tripIndex : bestSolution) {
                System.out.print(tripIndex + " ");
            }
            System.out.println();
            System.out.println("Fitness najlepšieho riešenia: " + algorithm.evaluateFitness(bestSolution));
            System.out.println("Trvanie výpočtu: " + duration + " sekúnd");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("bye");
    }
}