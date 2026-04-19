package notification;

import model.CommercialFlight;
import model.Flight;

public class NotificationObserver implements FlightStateListener {
    private final NotificationSender sender;

    public NotificationObserver(NotificationSender sender) {
        this.sender = sender;
    }

    @Override
    public void onStateChange(Flight flight) {
        if (!(flight instanceof CommercialFlight commercialFlight)) return;
        commercialFlight.getPassengers()
                .forEach(p -> sender.send(flight.getFlightState(), flight.getFlightNumber()));
    }

}
