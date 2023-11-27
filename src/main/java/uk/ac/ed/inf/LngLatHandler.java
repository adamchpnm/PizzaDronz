package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

public class LngLatHandler implements LngLatHandling {
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        //we find the differences, add the square of the differences and square root to find distance
        double latDif = endPosition.lat() - startPosition.lat();
        double lonDif = endPosition.lng() - startPosition.lng();
        double squared = (pwr2(latDif)+pwr2(lonDif));
        return Math.sqrt(squared);
        //return (Math.sqrt((Math.pow((endPosition.lat())-startPosition.lat(),2))+(Math.pow(endPosition.lng()-startPosition.lng(),2))));
    }

    public double pwr2 (double num){
        return (num * num);
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        //we use predefined distance to see if distance is within range
        return distanceTo(startPosition, otherPosition) <= SystemConstants.DRONE_IS_CLOSE_DISTANCE;
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        //use ray-tracing algorithm to see if within region
        int counter = 0;
        for (int i = 0; i < region.vertices().length; i++) {
            //for each edge of region (start -> end)
            LngLat start = region.vertices()[i];
            LngLat end = region.vertices()[((i+1)%region.vertices().length)];
            //if a horizontal line passes through edge from point, add to counter. this takes into account
            if (((position.lat() < start.lat()) != (position.lat() < end.lat()))&&(position.lng() < (start.lng() + (((position.lat()-start.lat())/(end.lat()-start.lat()))*(end.lng()-start.lng()))))){
                counter ++;
            }
        }
        return ((counter%2)==1);
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        LngLat nextPosition = startPosition;
        //if angle is one of the valid angles, change position, otherwise it does not move
        if ((angle % 22.5) == 0){
            double nextPositionLng;
            double nextPositionLat;
            //use polar coordinates to move one step in the direction given
            nextPositionLng = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(Math.toRadians(angle)));
            nextPositionLat = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(Math.toRadians(angle)));
            nextPosition = new LngLat(nextPositionLng,nextPositionLat);
        }
        return nextPosition;
    }
}
