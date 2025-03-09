package src.algorithm;

import src.object.Trip;
import src.object.Stop;
import src.object.TimeMatrix;
import src.object.Solution;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MemeticAlgorithm {
    private List<Trip> trips;
    private TimeMatrix timeMatrix;
    private int populationSize;
    private int generations;
    private double mutationRate;
    private List<Solution> population;
    private Solution bestSolution;
    private Map<Integer, Integer> stopIdToIndex;

    public MemeticAlgorithm(List<Trip> trips, TimeMatrix timeMatrix, List<Stop> stops, int populationSize, int generations, double mutationRate) {
        this.trips = trips;
        this.timeMatrix = timeMatrix;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.population = new ArrayList<>();
        this.bestSolution = null;
        this.stopIdToIndex = stops.stream().collect(Collectors.toMap(Stop::getId, Stop::getIndex));
    }

    public void initializePopulation() {
        int[] basePermutation = trips.stream().mapToInt(Trip::getIndex).toArray();
        for (int i = 0; i < populationSize; i++) {
            int[] shuffledTrips = basePermutation.clone();
            fisherYatesShuffle(shuffledTrips);
            Solution solution = new Solution(shuffledTrips);
            solution.setFitness(evaluateFitness(solution));
            population.add(solution);
            updateBestSolution(solution);
        }
    }

    private void fisherYatesShuffle(int[] array) {
        Random rand = ThreadLocalRandom.current();
        for (int i = array.length - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public int evaluateFitness(Solution solution) {
        int[] trips = solution.getTrips();
        int numTurns = 1;

        for (int i = 0; i < trips.length - 1; i++) {
            Trip tripA = this.trips.get(trips[i]);
            Trip tripB = this.trips.get(trips[i + 1]);

            int travelTime = timeMatrix.getTravelTime(this.stopIdToIndex.get(tripA.getEndStopId()), 
            this.stopIdToIndex.get(tripB.getStartStopId()));
            if (tripA.getEndTime() + travelTime > tripB.getStartTime()) {
                numTurns++;
            }
        }
        return numTurns;
    }

    private void updateBestSolution(Solution solution) {
        if (bestSolution == null || solution.getFitness() < bestSolution.getFitness()) {
            bestSolution = new Solution(solution.getTrips().clone());
            bestSolution.setFitness(solution.getFitness());
        }
    }

    public Solution[] crossover(Solution parent1, Solution parent2) {
        int length = parent1.getTrips().length;
        int crossoverPoint1 = ThreadLocalRandom.current().nextInt(length);
        int crossoverPoint2 = ThreadLocalRandom.current().nextInt(length);
        if (crossoverPoint1 > crossoverPoint2) {
            int temp = crossoverPoint1;
            crossoverPoint1 = crossoverPoint2;
            crossoverPoint2 = temp;
        }
    
        int[] child1 = new int[length];
        int[] child2 = new int[length];
        Arrays.fill(child1, -1);
        Arrays.fill(child2, -1);
    
        // Copy the crossover segment
        System.arraycopy(parent1.getTrips(), crossoverPoint1, child1, crossoverPoint1, crossoverPoint2 - crossoverPoint1);
        System.arraycopy(parent2.getTrips(), crossoverPoint1, child2, crossoverPoint1, crossoverPoint2 - crossoverPoint1);
    
        // Fill the remaining positions using PMX logic
        fillRemainingTrips(child1, parent2.getTrips(), crossoverPoint1, crossoverPoint2);
        fillRemainingTrips(child2, parent1.getTrips(), crossoverPoint1, crossoverPoint2);
    
        Solution child1Solution = new Solution(child1);
        child1Solution.setFitness(evaluateFitness(child1Solution));
        Solution child2Solution = new Solution(child2);
        child2Solution.setFitness(evaluateFitness(child2Solution));
        
        return new Solution[]{child1Solution, child2Solution};
    }
    
    private void fillRemainingTrips(int[] child, int[] parent, int start, int end) {
        Set<Integer> used = new HashSet<>();
        for (int i = start; i < end; i++) {
            used.add(child[i]);
        }
        
        int length = child.length;
        int index = 0;
        for (int i = 0; i < length; i++) {
            if (i >= start && i < end) continue;
            while (used.contains(parent[index])) {
                index++;
            }
            child[i] = parent[index++];
        }
    }

    public Solution mutate(Solution solution) {
        int[] trips = solution.getTrips().clone();
        if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
            inversionMutation(trips);
        }
        Solution mutatedSolution = new Solution(trips);
        mutatedSolution.setFitness(evaluateFitness(mutatedSolution));
        return mutatedSolution;
    }

    private void inversionMutation(int[] array) {
        Random rand = ThreadLocalRandom.current();
        int i = rand.nextInt(array.length);
        int j = rand.nextInt(array.length);
        if (i > j) {
            int temp = i;
            i = j;
            j = temp;
        }
        while (i < j) {
            int temp = array[i];
            array[i++] = array[j];
            array[j--] = temp;
        }
    }

    public Solution localSearch(Solution solution) {
        int[] trips = solution.getTrips().clone();
        for (int i = 0; i < trips.length - 1; i++) {
            for (int j = i + 1; j < trips.length; j++) {
                int temp = trips[i];
                trips[i] = trips[j];
                trips[j] = temp;
                Solution newSolution = new Solution(trips);
                newSolution.setFitness(evaluateFitness(newSolution));
                if (newSolution.getFitness() < solution.getFitness()) {
                    return newSolution;
                }
                temp = trips[i];
                trips[i] = trips[j];
                trips[j] = temp;
            }
        }
        return solution;
    }

    public void evolve() {
        for (int gen = 0; gen < generations; gen++) {
            List<Solution> newPopulation = new ArrayList<>();
            newPopulation.add(new Solution(bestSolution.getTrips().clone()));
            newPopulation.get(0).setFitness(bestSolution.getFitness());
            
            for (int i = 0; i < (populationSize - 1) / 2; i++) {
                Solution parent1 = population.get(ThreadLocalRandom.current().nextInt(population.size()));
                Solution parent2 = population.get(ThreadLocalRandom.current().nextInt(population.size()));
                
                Solution[] children = crossover(parent1, parent2);
                newPopulation.add(localSearch(mutate(children[0])));
                newPopulation.add(localSearch(mutate(children[1])));
            }
            
            population = newPopulation;
            population.parallelStream().forEach(this::updateBestSolution);
        }
    }

    public Solution getBestSolution() {
        return bestSolution;
    }
}
