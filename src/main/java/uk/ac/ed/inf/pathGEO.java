package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mapbox.geojson.*;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

// A class that matches the JSON format for the spec
class flightpathMove {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;


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
        // Make use of the list of paths of restaurant to Appleton
        List<flightpathMove> toReturn = new ArrayList<>();
        final int[] count = {-1};

        // Loop through each path in the list
        route.forEach(path -> {
            count[0]++;
            final int[] iter1 = {0};
            final int[] iter2 = {0};
            // Reverse the path (so that it starts by going Appleton -> restaurant)
            Collections.reverse(path);
            path.forEach(cell -> {
                try {
                    // For each cell in the path, create a new flightPathMove
                    toReturn.add(new flightpathMove(orderNos.get(count[0]),cell.coords.lng(),cell.coords.lat(), ((180-cell.angle)%360), path.get(iter1[0] +1).coords.lng(),path.get(iter1[0] +1).coords.lat()));
                    // The angles are reversed as we initially reverse the path
                    iter1[0]++;
                } catch (Exception e){
                    // The path has reached the restaurant
                }
            });
            // Add in the hover step
            toReturn.add(new flightpathMove(orderNos.get(count[0]),path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng(),999,path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng()));
            // Reverse the path again (so that it now goes restaurant -> Appleton)
            Collections.reverse(path);
            path.forEach(cell -> {
                try {
                    toReturn.add(new flightpathMove(orderNos.get(count[0]),cell.coords.lng(),cell.coords.lat(), cell.angle, path.get(iter2[0] +1).coords.lng(),path.get(iter2[0] +1).coords.lat()));
                    // The angles are back to normal as we are using the initial path
                    iter2[0]++;
                } catch (Exception e){
                    //The path has reached Appleton
                }
            });
            // Add in another hover step
            toReturn.add(new flightpathMove(orderNos.get(count[0]),path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng(),999,path.get(path.size()-1).coords.lng(),path.get(path.size()-1).coords.lng()));
        });
        return toReturn;
    }

    public static String droneFile(List<List<Cell>> route){
        // Make use of the list of paths of restaurant to Appleton
        List<Point> dronePath = new ArrayList<>();
        // Loop through each path in the list
        route.forEach(e -> {
            // Reverse the path (so that it starts by going Appleton -> restaurant)
            Collections.reverse(e);

            // Add a point to the list storing all points in order
            e.forEach(cell -> dronePath.add(Point.fromLngLat(cell.coords.lng(), cell.coords.lat())));

            // Reverse the path again (so that it now goes restaurant -> Appleton)
            Collections.reverse(e);

            // And add each coordinate point again
            e.forEach(cell -> dronePath.add(Point.fromLngLat(cell.coords.lng(), cell.coords.lat())));
        });

        // Create a LineString feature from the points
        Geometry geometry = LineString.fromLngLats(dronePath);
        Feature feature = Feature.fromGeometry(geometry);

        // Add it to its own Feature collection
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);

        // Return a string in a geoJSON format
        return featureCollection.toJson();
    }

    public static void main(List<String> OrderNums, List<Restaurant> visits, String BASEURL, String date) {
        List<List<Cell>> route = iterat(visits, BASEURL);

    //Directory will already exist from orderJSON

        String flightFileName = "flightpath-"+date+".json";
        List<flightpathMove> flights = flightFile(OrderNums,route);
        try (Writer writer = new FileWriter("resultFiles/"+flightFileName)) {
//            System.out.println("Writing");
            Gson gson = new GsonBuilder().create();
            gson.toJson(flights, writer);
            System.out.println("Flightpath file written");
        } catch (IOException e) {
            System.err.println("Unable to write flight");
        }

        String droneFileName = "drone-"+date+".geojson";
        String droneJSON = droneFile(route);
        try (Writer writer = new FileWriter("resultFiles/"+droneFileName)) {
            writer.write(droneJSON);
            System.out.println("Drone file written");
        } catch (IOException e) {
            System.err.println("Unable to write drone");
        }
    }

    public static List<List<Cell>> iterat(List<Restaurant> visits,String BASEURL){
        List<List<Cell>> toGoTo = new ArrayList<>();
        // Cycle through the restaurant for every order
        for (Restaurant restToGo:visits) {
            // Add the path to the list of paths
            toGoTo.add(addToPath(restToGo,toGoTo,BASEURL));
        }
        return toGoTo;
    }

    public static List<Cell> addToPath(Restaurant restrnt,List<List<Cell>> toGoTo,String BASEURL) {
        // Check if restaurant has already been put through A* algorithm
        if (visitedList.contains(restrnt)){
            // If it has, find the first occurrence in the list of paths and return
            int index = visitedList.indexOf(restrnt);
            visitedList.add(restrnt);
            return toGoTo.get(index);
        } else {

            LngLat restLoc = restrnt.location();
            NamedRegion[] NoFlyZones = new RESThandler(BASEURL).NoFlyZones();
            NamedRegion Central = new RESThandler(BASEURL).Central();

            // Find the start and goal positions
            Cell start, goal;
            start = new Cell(restLoc);
            goal = new Cell(apple);

            // Run A* algorithm to find the shortest path
            new AStar();
            AStar.openSet = new PriorityQueue<>(Comparator.comparingDouble(c -> c.f));
            AStar.closedSet = new HashSet<>();

            if (!AStar.findShortestPath(NoFlyZones, start, goal, Central)) {
                System.err.println("No path found!");
            }
            // Update the cache array of visited restaurants
            visitedList.add(restrnt);
            return AStar.path;
        }
    }
}
