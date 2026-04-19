package notification;

import model.FlightState;

public class SmsSender implements NotificationSender {
    @Override
    public void send(FlightState status, String flightNumber) {
        System.out.println("[SMS] Vuelo " + flightNumber + ": El estado de su viaje ha cambiado a: " + status + ".");
    }
}
