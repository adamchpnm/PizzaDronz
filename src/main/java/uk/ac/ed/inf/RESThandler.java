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

    public boolean isAlive(){

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/isAlive")).build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(response.body(),boolean.class);
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Order[] Orders(String date){
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/orders/" + date)).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

            return gson.fromJson(response.body(),Order[].class);
        } catch (Exception e) {
            System.out.println("No orders on specified date");
            return null;
        }
    }

    public NamedRegion[] NoFlyZones() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/noFlyZones")).build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(response.body(),NamedRegion[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Restaurant[] restaurants() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/restaurants")).build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(response.body(),Restaurant[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NamedRegion Central() {

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(this.url + "/centralArea")).build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(response.body(),NamedRegion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
