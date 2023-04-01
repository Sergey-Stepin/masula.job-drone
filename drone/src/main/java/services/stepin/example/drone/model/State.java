package services.stepin.example.drone.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public enum State {

    IDLE, LOADING, LOADED, DELIVERING, DELIVERED, RETURNING;

    public static List<State> availableStates(){

        return Arrays.asList(
                IDLE,
                RETURNING);
    }

    public static boolean isAvailable(State state){
        return availableStates().stream()
                .anyMatch(availableState -> availableState.equals(state));
    }
}
