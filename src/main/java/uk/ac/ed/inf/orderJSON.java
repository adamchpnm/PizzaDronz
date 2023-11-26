package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

class shortOrder{
    private String orderNo;
    private String orderStatus;
    private String orderValidationCode;
    private int costInPence;

    public shortOrder(Order order){
        this.orderNo = order.getOrderNo();
        this.orderStatus = order.getOrderStatus().toString();
        this.orderValidationCode = order.getOrderValidationCode().toString();
        this.costInPence = order.getPriceTotalInPence();
    }
}

public class orderJSON {
    public static void main(Order[] orders,String date){
        Path resultfiles = Paths.get("resultFiles/");
        try {
            Files.createDirectory(resultfiles);
        } catch (IOException ignored) {
            System.out.println("Directory already exists");
        }

        String deliveriesFileName = "deliveries-"+date+".json";


        List<shortOrder> shortOrderList = new ArrayList<>();

        for (Order order: orders) {
            shortOrderList.add(new shortOrder(order));
        }

        try (Writer writer = new FileWriter("resultFiles/"+deliveriesFileName)) {
//            System.out.println("Writing");
            Gson gson = new GsonBuilder().create();
            gson.toJson(shortOrderList, writer);
        } catch (IOException e) {
            System.out.println("Unable to write deliveries");;
        }

    }


}
