package src.object;

public class Solution {
    private int[] trips;
    private int fitness;

    public Solution(int[] trips) {
        this.trips = trips;
        this.fitness = Integer.MAX_VALUE;
    }

    public int[] getTrips() {
        return trips;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }
}
