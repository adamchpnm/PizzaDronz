package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class TestIlpJar {
    public static void main(String[] args) {
        System.out.println("ILP Test Application using the IlpDataObjects.jar file");

        Order order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 10, 13));

        order.setCreditCardInformation(
                new CreditCardInformation(
                        "1212121212121212",
                        "12/23",
                        "222"
                )
        );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        // get a random restaurant

        // and load the order items plus the price
        Pizza pizza1 = new Pizza("Margarita", 1000);
        Pizza pizza2 = new Pizza("Calzone", 1400);
        Pizza pizza3 = new Pizza("Pepperoni", 1000);
        Pizza pizza4 = new Pizza("Pineapple", 1400);
        Pizza pizza5 = new Pizza("WRONG", 1400);

        order.setPizzasInOrder(new Pizza[]{pizza4,pizza3});
        order.setPriceTotalInPence(2400 + SystemConstants.ORDER_CHARGE_IN_PENCE);
        Restaurant civs = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{pizza1,pizza2});
        Restaurant dominos = new Restaurant("Civerinos Slice",
                new LngLat(-3.1912869215011597, 55.945535152517735),
                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},
                new Pizza[]{pizza3,pizza4});

        Order validatedOrder =
                new OrderValidator().validateOrder(order,
                        new Restaurant[]{civs,dominos
                        });

        if (validatedOrder != null){
            System.out.println("order validation resulted in status: " +
                    validatedOrder.getOrderStatus() +
                    " and validation code: " +
                    validatedOrder.getOrderValidationCode());
        } else {
            System.out.println("no order validated");
        }
    }
}