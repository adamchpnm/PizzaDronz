package uk.ac.ed.inf;

import com.google.gson.Gson;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TestREST {
    public static String BASE_API = "https://ilp-rest.azurewebsites.net"; //CHANGE BASE TO MATCH INPUT
    public static LngLat apple = new LngLat(-3.186874, 55.944494); //SPECIFIED IN SPEC
    public static final String testDate = "2023-09-01";

    public static void main(String[] args) throws IOException {
        String[] argsTest = new String[]{testDate,BASE_API};
        getRstrnts();
//        getOrders(argsTest[0]);
//        tester();
    }

    public static void getOrders(String day) throws IOException {
        URL orderURL = new URL(BASE_API+"/orders/"+day);
        System.out.println(orderURL);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(orderURL.openStream(), StandardCharsets.UTF_8))) {
            Order[] orders = new Gson().fromJson(reader.readLine(), Order[].class);
            for (Order order : orders) {
                System.out.println(order);
                System.out.println(order.getOrderNo());
            }
        }
    }
    public static void tester() throws IOException {
        URL restrnt = new URL(BASE_API+"/orders");
//        URL orders = new URL(BASE_API+"/orders/");
        System.out.println(restrnt);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(restrnt.openStream(), StandardCharsets.UTF_8))) {
            Order[] restLists = new Gson().fromJson(reader.readLine(), Order[].class);
            for (Order restList : restLists) {
                System.out.println(restList);
            }
        }

//        Scanner myObj = new Scanner(System.in);
//        System.out.println("Enter date (YYYY-MM-DD)");
//        String date = myObj.nextLine();
//        URL dateOrders = new URL(BASE_API+"/orders/"+date);
//        System.out.println(dateOrders);
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dateOrders.openStream(), "UTF-8"))) {
//            String content = reader.readLine();
//            System.out.println(content);
//        }

    }

    public static void getRstrnts() throws IOException {
        URL restrnt = new URL(BASE_API+"/restaurants");
//        URL orders = new URL(BASE_API+"/orders/");
        System.out.println(restrnt);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(restrnt.openStream(), StandardCharsets.UTF_8))) {
            Restaurant[] restLists = new Gson().fromJson(reader.readLine(), Restaurant[].class);
            for (Restaurant restList : restLists) {
                System.out.println(restList);
                System.out.println(restList.location());
                double distance = new LngLatHandler().distanceTo(restList.location(), apple);
                System.out.println(distance);
            }
        }

//        Scanner myObj = new Scanner(System.in);
//        System.out.println("Enter date (YYYY-MM-DD)");
//        String date = myObj.nextLine();
//        URL dateOrders = new URL(BASE_API+"/orders/"+date);
//        System.out.println(dateOrders);
//
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dateOrders.openStream(), "UTF-8"))) {
//            String content = reader.readLine();
//            System.out.println(content);
//        }

    }
}
