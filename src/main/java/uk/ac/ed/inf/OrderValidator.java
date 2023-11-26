package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

public class OrderValidator implements OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);

        //    MAX_PIZZA_COUNT_EXCEEDED
        //checks not too many pizzas in order
        if (orderToValidate.getPizzasInOrder().length > 4) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        }

        //    CARD_NUMBER_INVALID
        //checks format of card number and that all characters are digits
        if (orderToValidate.getCreditCardInformation().getCreditCardNumber().length() != 16 ||(!(orderToValidate.getCreditCardInformation().getCreditCardNumber().chars().allMatch(Character::isDigit)))){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
        }

        //    CVV_INVALID
        //checks format of card cvv and that all characters are digits
        if ((orderToValidate.getCreditCardInformation().getCvv().length() != 3)||(!(orderToValidate.getCreditCardInformation().getCvv().chars().allMatch(Character::isDigit)))){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
        }

        //    EXPIRY_DATE_INVALID
        //checks format of card expiry and that all necessary characters are digits
        if ((orderToValidate.getCreditCardInformation().getCreditCardExpiry().length() != 5)||(!(orderToValidate.getCreditCardInformation().getCreditCardExpiry().matches("\\d{2}/\\d{2}")))) {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
        }
        //parses expiry date as type LocalDate. if it can not, then invalid
        String shortDate = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        String longDate = "20" + shortDate.charAt(3) + shortDate.charAt(4) + "-" + shortDate.charAt(0) + shortDate.charAt(1) + "-01";
        try
        {
            LocalDate cardDate = LocalDate.parse(longDate);
            LocalDate today = LocalDate.now();
            //checks to see if expiry date is valid on today's date, sets invalid if not
            if (cardDate.getYear() == today.getYear()) {
                if (cardDate.getMonthValue() < today.getMonthValue()) {
                    orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
                }
            }
            if (cardDate.getYear() < today.getYear()){
                orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            }

        }
        catch(Exception DateTimeParseException)
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
        }

        //    PIZZA_NOT_DEFINED
        //look through restaurants and check that we can find same amount of pizzas
        //as there are in the order
        int pizzaFound = 0;
        for (int k = 0; k < orderToValidate.getPizzasInOrder().length; k++) {
            for (Restaurant definedRestaurant : definedRestaurants) {
                if (Arrays.asList(definedRestaurant.menu()).contains(orderToValidate.getPizzasInOrder()[k])) {
                    pizzaFound += 1;
                }
            }
        }
        if (!(pizzaFound == orderToValidate.getPizzasInOrder().length)){
            //we set order validation code and status here as the code below relies on the pizza
            //existing and, as it does not, we break here to prevent errors
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        //to prevent warning of variable not initialized
        Restaurant current = new Restaurant("invalid",
                new LngLat(55.945535152517735, -3.1912869215011597),
                new DayOfWeek[]{DayOfWeek.SUNDAY, DayOfWeek.SUNDAY},
                new Pizza[]{new Pizza("invalid", -1)});

        //find the restaurant and set current to it
        for (Restaurant definedRestaurant : definedRestaurants) {
            if (Arrays.asList(definedRestaurant.menu()).contains(orderToValidate.getPizzasInOrder()[0])) {
                current = definedRestaurant;
            }
        }

        //    RESTAURANT_CLOSED
        //checks current restaurant and see if open days include that of the order day
        if (!(Arrays.asList(current.openingDays()).contains(orderToValidate.getOrderDate().getDayOfWeek()))){
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
        }

        //    PIZZA_FROM_MULTIPLE_RESTAURANTS
        //checks if any pizzas are in a different restaurant to that of the first
        for (int i = 1; i < orderToValidate.getPizzasInOrder().length; i++) {
            if (!(Arrays.asList(current.menu()).contains(orderToValidate.getPizzasInOrder()[i]))){
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            }
        }

        //    TOTAL_INCORRECT
        int totalExp = 0;
        //add each pizza price and delivery cost and compare to order price
        for (Pizza pizza : orderToValidate.getPizzasInOrder()) {
            totalExp += pizza.priceInPence();
        }
        totalExp += SystemConstants.ORDER_CHARGE_IN_PENCE;
        if (!(totalExp == orderToValidate.getPriceTotalInPence())){
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
        }

        //if there are no errors raised, the order is valid and this is returned
        if (orderToValidate.getOrderValidationCode() == OrderValidationCode.NO_ERROR){
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        } else {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        return orderToValidate;
    }
}
