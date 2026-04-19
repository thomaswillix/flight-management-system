package notification;

import model.FlightState;

import java.util.List;

public class CompositeNotificationSender implements NotificationSender{
    private final List<NotificationSender> senders;

    public CompositeNotificationSender(List<NotificationSender> senders) {
        this.senders = senders;
    }

    @Override
    public void send(FlightState status, String flightNumber) {
        senders.forEach(s -> s.send(status, flightNumber));
    }
}
