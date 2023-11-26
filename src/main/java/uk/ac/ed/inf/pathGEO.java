package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.mapbox.geojson.*;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class flightpathMove {
    private String orderNo;
    private double fromLongitude;
    private double fromLatitude;
    private double angle;
    private double toLongitude;
    private double toLatitude;


    public flightpathMove(String orderNo, double fromLongitude, double fromLatitude, double angle, double toLongitude, double toLatitude) {
        this.orderNo = orderNo;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;
        this.angle = angle;
        this.fromLatitude = fromLatitude;
        this.fromLongitude = fromLongitude;
    }
}


public class pathGEO {
    public static LngLat apple = new LngLat(-3.186874, 55.944494); //SPECIFIED IN SPEC
    public static ArrayList<Restaurant> visitedList = new ArrayList<>();

    public static List<flightpathMove> flightFile(List<String> orderNos, List<List<Cell>> route){
        List<flightpathMove> toReturn = new ArrayList<>();
        final int[] count = {-1};
        route.forEach(path -> {
            count[0]++;
            final int[] iter1 = {0};
            final int[] iter2 = {0};
            Collections.reverse(path);
            path.forEach(cell -> {
                try {
                    toReturn.add(new flightpathMove(orderNos.get(count[0]),cell.coords.lng(),cell.coords.lat(), ((180-cell.angle)%360), path.get(iter1[0] +1).coords.lng(),path.get(iter1[0] +1).coords.lat()));
                    iter1[0]++;
                } catch (Exception e){
                    //reached Restaurant
                }
            });
            toReturn.add(new flightpathMove(orderNos.get(count[0]),path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng(),999,path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng()));
            Collections.reverse(path);
            path.forEach(cell -> {
                try {
                    toReturn.add(new flightpathMove(orderNos.get(count[0]),cell.coords.lng(),cell.coords.lat(), cell.angle, path.get(iter2[0] +1).coords.lng(),path.get(iter2[0] +1).coords.lat()));
                    iter2[0]++;
                } catch (Exception e){
                    //reached Appleton
                }
            });
            toReturn.add(new flightpathMove(orderNos.get(count[0]),path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng(),999,path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng()));
        });
        return toReturn;
    }

    public static String droneFile(List<List<Cell>> route){
        List<Point> dronePath = new ArrayList<>();
        route.forEach(e -> {
            Collections.reverse(e);
            e.forEach(cell -> {
                dronePath.add(Point.fromLngLat(cell.coords.lng(), cell.coords.lat()));

            });
            Collections.reverse(e);
            e.forEach(cell -> {
                dronePath.add(Point.fromLngLat(cell.coords.lng(), cell.coords.lat()));
            });
        });

        Geometry geometry = LineString.fromLngLats(dronePath);
        Feature feature = Feature.fromGeometry(geometry);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String geoJson = featureCollection.toJson();
        return geoJson;
    }

    public static void main(List<String> OrderNums, List<Restaurant> visits, String BASEURL, String date) {
        List<List<Cell>> route = iterat(visits, BASEURL);


//        Path resultfiles = Paths.get("resultFiles/");
//        try {
//            Files.createDirectory(resultfiles);
//        } catch (IOException ignored) {
//            System.out.println("Directory already exists");
//        }

        String flightFileName = "flightpath-"+date+".json";
        List<flightpathMove> flights = flightFile(OrderNums,route);
        try (Writer writer = new FileWriter("resultFiles/"+flightFileName)) {
//            System.out.println("Writing");
            Gson gson = new GsonBuilder().create();
            gson.toJson(flights, writer);
        } catch (IOException e) {
            System.out.println("Unable to write flight");;
        }

        String droneFileName = "drone-"+date+".geojson";
        String droneJSON = droneFile(route);
        try (Writer writer = new FileWriter("resultFiles/"+droneFileName)) {
//            System.out.println("Writing");
            writer.write(droneJSON);
        } catch (IOException e) {
            System.out.println("Unable to write drone");;
        }
    }

    public static List<List<Cell>> iterat(List<Restaurant> visits,String BASEURL){
        List<List<Cell>> toGoTo = new ArrayList<>();
        for (Restaurant restToGo:visits) {
            toGoTo.add(addToPath(restToGo,toGoTo,BASEURL));
        }
        return toGoTo;
    }

    public static List<Cell> addToPath(Restaurant restrnt,List<List<Cell>> toGoTo,String BASEURL) {

        if (visitedList.contains(restrnt)){
            int index = visitedList.indexOf(restrnt);
            visitedList.add(restrnt);
            return toGoTo.get(index);
        } else {
            LngLat restLoc = restrnt.location();
            NamedRegion[] NoFlyZones = new RESThandler(BASEURL).NoFlyZones();
            // Find the start and goal positions
            Cell start, goal;
            start = new Cell(restLoc);
            goal = new Cell(apple);

            // initialize the global variable
            new AStar();
            AStar.openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
            AStar.closedSet = new HashSet<>();

            // Run A* algorithm to find the shortest path
            if (!AStar.findShortestPath(NoFlyZones, start, goal)) {
                System.out.println("No path found!");
            }

            visitedList.add(restrnt);
            return AStar.path;
        }
    }
}