package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App 
{

    public static void main(String args[]){
        // Command line arguments for date and BASEURL
        String BASEURL = args[1];
        String date = args[0];

        // Check if the website is accessible and alive
        if (new RESThandler(BASEURL).isAlive().equals("false")){
            System.err.println("Website is not currently alive");
        } else if (new RESThandler(BASEURL).isAlive().equals("error")){
            System.err.println("Error with URL input");
        } else {

            // Lists to store valid order numbers and restaurants to visit
            List<String> orderNumValid = new ArrayList<>();
            List<Restaurant> restsToVisit = new ArrayList<>();

            // Retrieve restaurants and orders from the REST API
            Restaurant[] restrnts = new RESThandler(BASEURL).restaurants();
            Order[] orderList = new RESThandler(BASEURL).Orders(date);
            List<Order> validatedList = new ArrayList<>();

            // Check if the REST Handler was able to retrieve the Order JSON file
            if (orderList != null) {
                // Check if there are any orders for the specified date
                if (orderList.length == 0) {
                    // If no orders for the date
                    // Create/access directory and add empty JSON files for no orders
                    System.err.println("No orders for date specified");
                    createDir.main();
                    orderJSON.main(validatedList, date);
                    pathGEO.main(orderNumValid, restsToVisit, BASEURL, date);
                } else {
                    // Else create/access directory
                    createDir.main();
                    // And cycle through each order
                    for (Order order : orderList) {
                        Order validatedOrder =
                                new OrderValidator().validateOrder(order, restrnts);
                        // Check the order is non-null
                        if (validatedOrder != null) {
                            // Add valid orders to the list - these will affect flightpath
                            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                                orderNumValid.add(order.getOrderNo());
                                restsToVisit.add(getRestrnt(restrnts, validatedOrder));
                            }
                            // Add all orders to a list to add to Order JSON
                            validatedList.add(validatedOrder);
                        } else {
                            System.err.println("no order validated");
                        }
                    }
                    // Create the Order JSON file from list of all validated orders
                    orderJSON.main(validatedList, date);
                    // Create Flightpath and Drone files from only the valid orders and corresponding restaurants
                    pathGEO.main(orderNumValid, restsToVisit, BASEURL, date);
                }
            }
        }
    }

    public static Restaurant getRestrnt(Restaurant[] restrnts,Order validOrder){
        // This looks at the first pizza in a valid order and returns its restaurant of origin
        for (Restaurant definedRestaurant : restrnts) {
            if (Arrays.asList(definedRestaurant.menu()).contains(validOrder.getPizzasInOrder()[0])) {
                return definedRestaurant;
            }
        }
        return null; //this would never be reached as only valid pizzas are passed to this function
    }
}
