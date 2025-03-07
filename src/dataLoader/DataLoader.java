package src.dataLoader;

import src.object.Stop;
import src.object.Trip;
import src.object.TimeMatrix;
import java.io.*;
import java.util.*;

public class DataLoader {
    
    public static List<Stop> loadStops(String filename) throws IOException {
        List<Stop> stops = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int index = 0;
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            double lat = Double.parseDouble(parts[2]);
            double lon = Double.parseDouble(parts[3]);
            stops.add(new Stop(index, id, name, lat, lon));
            index++;
        }

        br.close();
        return stops;
    }
    
    public static List<Trip> loadTrips(String filename) throws IOException {
        List<Trip> trips = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        br.readLine();

        while ((line = br.readLine()) != null) {
            String[] parts = line.split(";");
            int index = Integer.parseInt(parts[0]);
            int startStopId = Integer.parseInt(parts[4]);
            int endStopId = Integer.parseInt(parts[5]);
            int startTime = Integer.parseInt(parts[6]);
            int endTime = Integer.parseInt(parts[7]);
            double distance = Double.parseDouble(parts[9]);
            trips.add(new Trip(index, startStopId, endStopId, startTime, endTime, distance));
        }
        br.close();
        return trips;
    }
    
    public static TimeMatrix loadTimeMatrix(String filename, int size) throws IOException {
        return TimeMatrix.loadFromFile(filename, size);
    }
}
