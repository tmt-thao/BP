package src.object;

public class Trip {
    private int index;
    private int startStopId;
    private int endStopId;
    private int startTime;
    private int endTime;
    private double distance;

    public Trip(int index, int startStopId, int endStopId, int startTime, int endTime, double distance) {
        this.index = index;
        this.startStopId = startStopId;
        this.endStopId = endStopId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }

    public int getStartStopId() {
        return startStopId;
    }

    public int getEndStopId() {
        return endStopId;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "index=" + index +
                ", startStopId=" + startStopId +
                ", endStopId=" + endStopId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", distance=" + distance +
                '}';
    }
}