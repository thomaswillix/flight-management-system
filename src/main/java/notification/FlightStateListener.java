package notification;

import model.Flight;

public interface FlightStateListener {
    void onStateChange(Flight flight);
}
