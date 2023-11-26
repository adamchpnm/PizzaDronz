package uk.ac.ed.inf;

// Main function
public class TestAStar {

    public static void main(String[] args){
        String BASEURL = "https://ilp-rest.azurewebsites.net";
        String testDate = "2023-12-13";
        String[] argus = {testDate,BASEURL};
        App.main(argus);
    }
}