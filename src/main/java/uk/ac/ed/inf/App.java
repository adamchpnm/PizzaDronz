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
        String BASEURL = args[1];
        String date = args[0];

        if (new RESThandler(BASEURL).isAlive().equals("false")){
            System.err.println("Website is not currently alive");
        } else if (new RESThandler(BASEURL).isAlive().equals("error")){
            System.err.println("Error with URL input");
        } else {


            List<String> orderNumValid = new ArrayList<>();
            List<Restaurant> restsToVisit = new ArrayList<>();

            Restaurant[] restrnts = new RESThandler(BASEURL).restaurants();
            Order[] orderList = new RESThandler(BASEURL).Orders(date);
            List<Order> validatedList = new ArrayList<>();

            if (orderList != null) {
                if (orderList.length == 0) {
                    System.err.println("No orders for date specified");
                } else {
                    createDir.main();
                    for (Order order : orderList) {
                        Order validatedOrder =
                                new OrderValidator().validateOrder(order, restrnts);
                        if (validatedOrder != null) {
                            if (validatedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {
                                orderNumValid.add(order.getOrderNo());
                                restsToVisit.add(getRestrnt(restrnts, validatedOrder));
                            }
                            validatedList.add(validatedOrder);

                            //create OrderJSON from input validatedOrder
                        } else {
                            System.err.println("no order validated");
                        }
                    }
                    orderJSON.main(validatedList, date);
                    pathGEO.main(orderNumValid, restsToVisit, BASEURL, date);
                }
            }
        }
    }

    public static Restaurant getRestrnt(Restaurant[] restrnts,Order validOrder){
        for (Restaurant definedRestaurant : restrnts) {
            if (Arrays.asList(definedRestaurant.menu()).contains(validOrder.getPizzasInOrder()[0])) {
                return definedRestaurant;
            }
        }
        return null; //this would never be reached
    }
}
