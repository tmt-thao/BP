package src.object;

public class Stop {
    private int index;
    private int id;
    private String nazov;
    private double lat;
    private double lon;

    public Stop(int index, int id, String nazov, double lat, double lon) {
        this.index = index;
        this.id = id;
        this.nazov = nazov;
        this.lat = lat;
        this.lon = lon;
    }

    public int getIndex() {
        return index;
    }

    public int getId() {
        return id;
    }

    public String getNazov() {
        return nazov;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "index=" + index +
                ", id=" + id +
                ", nazov='" + nazov + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
