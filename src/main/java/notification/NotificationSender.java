package notification;

import model.FlightState;

public interface NotificationSender {
    void send(FlightState status, String flightNumber);
}
