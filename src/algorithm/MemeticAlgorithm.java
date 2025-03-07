package src.algorithm;

import src.object.Trip;
import src.object.Stop;
import src.object.TimeMatrix;
import src.object.Solution;
import java.util.*;

public class MemeticAlgorithm {
    private List<Trip> trips;
    private TimeMatrix timeMatrix;
    private int populationSize;
    private int generations;
    private double mutationRate;
    private List<Solution> population;
    private Solution bestSolution;
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
            int[] tripsArray = tripList.stream().mapToInt(Integer::intValue).toArray();
            Solution solution = new Solution(tripsArray);
            solution.setFitness(evaluateFitness(solution));
            population.add(solution);
            updateBestSolution(solution);
        }
    }

    public int evaluateFitness(Solution solution) {
        int[] trips = solution.getTrips();
        int numTurns = 1;
        for (int i = 0; i < trips.length - 1; i++) {
            Trip tripA = this.trips.get(trips[i]);
            Trip tripB = this.trips.get(trips[i + 1]);
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

    private void updateBestSolution(Solution solution) {
        if (bestSolution == null || solution.getFitness() < bestSolution.getFitness()) {
            bestSolution = new Solution(solution.getTrips().clone());
            bestSolution.setFitness(solution.getFitness());
        }
    }

    public Solution selection() {
        Random rand = new Random();
        int tournamentSize = 3;
        Solution best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Solution candidate = population.get(rand.nextInt(population.size()));
            if (best == null || candidate.getFitness() < best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }

    public Solution[] crossover(Solution parent1, Solution parent2) {
        Random rand = new Random();
        int length = parent1.getTrips().length;
        int crossoverPoint = rand.nextInt(length);
        
        int[] child1Trips = new int[length];
        int[] child2Trips = new int[length];
        
        System.arraycopy(parent1.getTrips(), 0, child1Trips, 0, crossoverPoint);
        System.arraycopy(parent2.getTrips(), crossoverPoint, child1Trips, crossoverPoint, length - crossoverPoint);
        
        System.arraycopy(parent2.getTrips(), 0, child2Trips, 0, crossoverPoint);
        System.arraycopy(parent1.getTrips(), crossoverPoint, child2Trips, crossoverPoint, length - crossoverPoint);
        
        Solution child1 = new Solution(child1Trips);
        Solution child2 = new Solution(child2Trips);
        
        return new Solution[] { child1, child2 };
    }

    public Solution mutate(Solution solution) {
        Random rand = new Random();
        int[] trips = solution.getTrips().clone();
        if (rand.nextDouble() < mutationRate) {
            int i = rand.nextInt(trips.length);
            int j = rand.nextInt(trips.length);
            
            int temp = trips[i];
            trips[i] = trips[j];
            trips[j] = temp;
        }
        Solution mutatedSolution = new Solution(trips);
        mutatedSolution.setFitness(evaluateFitness(mutatedSolution));
        return mutatedSolution;
    }

    public Solution localSearch(Solution solution) {
        Solution bestLocalSolution = new Solution(solution.getTrips().clone());
        bestLocalSolution.setFitness(solution.getFitness());
        int bestFitness = bestLocalSolution.getFitness();
        
        for (int i = 0; i < solution.getTrips().length; i++) {
            for (int j = i + 1; j < solution.getTrips().length; j++) {
                int[] newTrips = solution.getTrips().clone();
                int temp = newTrips[i];
                newTrips[i] = newTrips[j];
                newTrips[j] = temp;
                Solution newSolution = new Solution(newTrips);
                newSolution.setFitness(evaluateFitness(newSolution));
                int newFitness = newSolution.getFitness();
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
            List<Solution> newPopulation = new ArrayList<>();
            
            newPopulation.add(new Solution(bestSolution.getTrips().clone()));
            newPopulation.get(0).setFitness(bestSolution.getFitness());
            
            for (int i = 0; i < (populationSize - 1) / 2; i++) {
                Solution parent1 = selection();
                Solution parent2;
                do {
                    parent2 = selection();
                } while (Arrays.equals(parent1.getTrips(), parent2.getTrips())); 
                Solution[] children = crossover(parent1, parent2);
                newPopulation.add(localSearch(mutate(children[0])));
                newPopulation.add(localSearch(mutate(children[1])));
            }
            
            population = newPopulation;
            for (Solution solution : population) {
                updateBestSolution(solution);
            }
        }
    }

    public Solution getBestSolution() {
        return bestSolution;
    }
}