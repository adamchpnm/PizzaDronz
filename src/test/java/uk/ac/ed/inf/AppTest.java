package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

// Main function
public class AppTest {

    public static void main(String[] args){
        String BASEURL = "https://ilp-rest.azurewebsites.net";
        String testDate = "2023-11-15";

        //Add this to pathGEO.addToPath to test if cant reach or another blocker?
        //        LngLat[] testVert1 = {new LngLat(-3.203077534384221,
        //                55.94353648914512),new LngLat( -3.203108688934492,
        //                55.942964616013796),new LngLat( -3.2026829100805685,
        //                55.94298981738041),new LngLat(-3.2027106030149355,
        //                55.94338528283345), new LngLat(-3.2022259766764023,
        //                55.94339109847155), new LngLat(-3.2022294382933296,
        //                55.94356169013989), new LngLat(-3.203077534384221,
        //                55.94353648914512)};
        //        LngLat[] testVert2 = {new LngLat(-3.2024405969123393,
        //                55.94340660683622),new LngLat( -3.2024405969123393,
        //                55.94321856749741),new LngLat( -3.202738295948251,
        //                55.94321081327834),new LngLat(-3.202738295948251,
        //                55.94279256124427), new LngLat(-3.202,
        //                55.94273110008288386), new LngLat(-3.202,
        //                55.9434124224711), new LngLat(-3.2024405969123393,
        //                55.94340660683622)};
        //        NamedRegion bad1 = new NamedRegion("bad1",testVert1);
        //        NamedRegion bad2 = new NamedRegion("bad2",testVert2);
        //
        ////            NoFlyZones[1] = bad1;
        //        NoFlyZones[2] = bad2;

        String[] argus = {testDate,BASEURL};
        App.main(argus);
    }
}