package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class RESThandler {
    private final String url;

    public RESThandler(String url){
        this.url = url;
    }

    // A method to check the server is still up
    public String isAlive(){

        // Instantiate the client and prepare a request for the necessary URL
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/isAlive")).build();

        // Instantiate the response
        HttpResponse<String> response;

        try {

            // Send the request and store the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the response
            Gson gson = new GsonBuilder().create();
            return String.valueOf(gson.fromJson(response.body(),boolean.class));
            
        } catch (Exception e) {
            // If unable to reach, return error
            return "error";
        }
    }

    public Order[] Orders(String date){

        // Instantiate the client and prepare a request for the necessary URL
        HttpClient client = HttpClient.newBuilder().build();

        // Instantiate the response
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/orders/" + date)).build();
        HttpResponse<String> response;

        try {

            // Send the request and store the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the response (we must take into account the date variable type)
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();
            return gson.fromJson(response.body(),Order[].class);

        } catch (Exception e) {
            // If unable to reach, check the date format and return necessary error
            if (!(date.matches("\\d{2}-\\d{2}"))){
                System.err.println("Incorrect date format");
            } else {
                System.err.println("Orders on specified date not found");
            }
            return null;
        }
    }

    public NamedRegion[] NoFlyZones() {

        // Instantiate the client and prepare a request for the necessary URL
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/noFlyZones")).build();

        // Instantiate the response
        HttpResponse<String> response;

        try {

            // Send the request and store the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the response
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(),NamedRegion[].class);

        } catch (Exception e) {
            // If unable to reach, return error
            System.err.println("Unable to access No Fly Zones - quitting program");
            return null;
        }
    }

    public Restaurant[] restaurants() {

        // Instantiate the client and prepare a request for the necessary URL
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/restaurants")).build();

        // Instantiate the response
        HttpResponse<String> response;

        try {

            // Send the request and store the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the response
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(),Restaurant[].class);

        } catch (Exception e) {
            // If unable to reach, return error
            System.err.println("Unable to access restaurants - quitting program");
            return null;
        }
    }

    public NamedRegion Central() {

        // Instantiate the client and prepare a request for the necessary URL
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/centralArea")).build();

        // Instantiate the response
        HttpResponse<String> response;

        try {

            // Send the request and store the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Use a GsonBuilder to deserialize the response
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(response.body(),NamedRegion.class);

        } catch (Exception e) {
            // If unable to reach, return error
            System.err.println("Unable to access Central zone - quitting program");
            return null;
        }
    }
}
