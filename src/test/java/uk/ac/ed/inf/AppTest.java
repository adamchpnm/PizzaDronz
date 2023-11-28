package uk.ac.ed.inf;

// Main function
public class AppTest {

    public static void main(String[] args){
        String BASEURL = "https://ilp-rest.azurewebsites.net";
        String testDate = "2023-04-01";
        String[] argus = {testDate,BASEURL};
        App.main(argus);
    }
}