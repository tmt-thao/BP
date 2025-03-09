package src.test;

import src.algorithm.MemeticAlgorithm;
import src.dataLoader.DataLoader;
import src.object.Trip;
import src.object.TimeMatrix;
import src.object.Stop;
import src.object.Solution;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemeticAlgorithmTest {
    public static void main(String[] args) {
        System.out.println("hi");

        try {
            // Načítajte dáta zo súborov CSV pomocou DataLoader
            List<Trip> trips = DataLoader.loadTrips("data/spoje_id_T4_3.csv");
            List<Stop> stops = DataLoader.loadStops("data/ZastavkyAll.csv");
            TimeMatrix timeMatrix = DataLoader.loadTimeMatrix("data/matrixTime.txt", stops.size());

            // Inicializujte memetický algoritmus
            int populationSize = 10;
            int generations = 800;
            double mutationRate = 0.1;
            MemeticAlgorithm algorithm = new MemeticAlgorithm(trips, timeMatrix, stops, populationSize, generations, mutationRate);

            // Meranie času trvania výpočtu
            LocalDateTime startDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("Začiatok výpočtu: " + startDateTime.format(formatter));
            long startTime = System.nanoTime();

            // Inicializujte populáciu
            algorithm.initializePopulation();

            // Spustite evolúciu
            algorithm.evolve();

            long endTime = System.nanoTime();
            LocalDateTime endDateTime = LocalDateTime.now();
            System.out.println("Koniec výpočtu: " + endDateTime.format(formatter));
            double duration = (endTime - startTime) / 1_000_000_000.0; // Trvanie v sekundách

            // Získajte a vypíšte najlepšie riešenie
            Solution bestSolution = algorithm.getBestSolution();
            System.out.println("Najlepšie riešenie: ");
            for (int tripIndex : bestSolution.getTrips()) {
                System.out.print(tripIndex + " ");
            }
            System.out.println();
            System.out.println("Fitness najlepšieho riešenia: " + bestSolution.getFitness());
            System.out.println("Trvanie výpočtu: " + duration + " sekúnd");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("bye");
    }
}