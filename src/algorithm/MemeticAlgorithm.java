package src.algorithm;

import src.object.Trip;
import src.object.Stop;
import src.object.TimeMatrix;
import java.util.*;

public class MemeticAlgorithm {
    private List<Trip> trips;
    private TimeMatrix timeMatrix;
    private int populationSize;
    private int generations;
    private double mutationRate;
    private List<int[]> population;
    private int[] bestSolution;
    private Map<Integer, Stop> stopMap;

    public MemeticAlgorithm(List<Trip> trips, TimeMatrix timeMatrix, List<Stop> stops, int populationSize, int generations, double mutationRate) {
        this.trips = trips;
        this.timeMatrix = timeMatrix;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.population = new ArrayList<>();
        this.bestSolution = null;
        this.stopMap = new HashMap<>();
        for (Stop stop : stops) {
            stopMap.put(stop.getId(), stop);
        }
    }

    public void initializePopulation() {
        for (int i = 0; i < populationSize; i++) {
            List<Integer> tripList = new ArrayList<>();
            for (Trip trip : trips) {
                tripList.add(trip.getIndex());
            }
            Collections.shuffle(tripList);
            int[] solution = tripList.stream().mapToInt(Integer::intValue).toArray();
            population.add(solution);
            updateBestSolution(solution);
        }
    }

    public int evaluateFitness(int[] solution) {
        // if (!chargingSimulation.isFeasible(solution)) {
        //     return Integer.MAX_VALUE;
        // }

        int numTurns = 1;
        for (int i = 0; i < solution.length - 1; i++) {
            Trip tripA = trips.get(solution[i]);
            Trip tripB = trips.get(solution[i + 1]);
            Stop endStopA = stopMap.get(tripA.getEndStopId());
            Stop startStopB = stopMap.get(tripB.getStartStopId());
            int travelTime = timeMatrix.getTravelTime(endStopA.getIndex(), startStopB.getIndex());
            int adjustedEndTime = tripA.getEndTime() + travelTime;
            if (adjustedEndTime > tripB.getStartTime()) {
                numTurns++;
            }
        }
        return numTurns;
    }

    private void updateBestSolution(int[] solution) {
        if (bestSolution == null || evaluateFitness(solution) < evaluateFitness(bestSolution)) {
            bestSolution = solution.clone();
        }
    }

    public int[] selection() {
        Random rand = new Random();
        int tournamentSize = 3;
        int[] best = null;
        for (int i = 0; i < tournamentSize; i++) {
            int[] candidate = population.get(rand.nextInt(population.size()));
            if (best == null || evaluateFitness(candidate) < evaluateFitness(best)) {
                best = candidate;
            }
        }
        return best;
    }

    public int[][] crossover(int[] parent1, int[] parent2) {
        Random rand = new Random();
        int length = parent1.length;
        int crossoverPoint = rand.nextInt(length);
        
        int[] child1 = new int[length];
        int[] child2 = new int[length];
        
        System.arraycopy(parent1, 0, child1, 0, crossoverPoint);
        System.arraycopy(parent2, crossoverPoint, child1, crossoverPoint, length - crossoverPoint);
        
        System.arraycopy(parent2, 0, child2, 0, crossoverPoint);
        System.arraycopy(parent1, crossoverPoint, child2, crossoverPoint, length - crossoverPoint);
        
        return new int[][] { child1, child2 };
    }

    public int[] mutate(int[] solution) {
        Random rand = new Random();
        if (rand.nextDouble() < mutationRate) {
            int i = rand.nextInt(solution.length);
            int j = rand.nextInt(solution.length);
            
            int temp = solution[i];
            solution[i] = solution[j];
            solution[j] = temp;
        }
        return solution;
    }

    public int[] localSearch(int[] solution) {
        int[] bestLocalSolution = solution.clone();
        int bestFitness = evaluateFitness(bestLocalSolution);
        
        for (int i = 0; i < solution.length; i++) {
            for (int j = i + 1; j < solution.length; j++) {
                int[] newSolution = solution.clone();
                int temp = newSolution[i];
                newSolution[i] = newSolution[j];
                newSolution[j] = temp;
                int newFitness = evaluateFitness(newSolution);
                if (newFitness < bestFitness) {
                    bestLocalSolution = newSolution;
                    bestFitness = newFitness;
                }
            }
        }
        return bestLocalSolution;
    }

    public void evolve() {
        for (int gen = 0; gen < generations; gen++) {
            List<int[]> newPopulation = new ArrayList<>();
            
            newPopulation.add(bestSolution.clone());
            
            for (int i = 0; i < (populationSize - 1) / 2; i++) {
                int[] parent1 = selection();
                int[] parent2;
                do {
                    parent2 = selection();
                } while (Arrays.equals(parent1, parent2)); 
                int[][] children = crossover(parent1, parent2);
                newPopulation.add(localSearch(mutate(children[0])));
                newPopulation.add(localSearch(mutate(children[1])));
            }
            
            population = newPopulation;
            for (int[] solution : population) {
                updateBestSolution(solution);
            }
        }
    }

    public int[] getBestSolution() {
        return bestSolution;
    }
}
