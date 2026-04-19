package notification;

import model.FlightState;

public class EmailSender implements NotificationSender {
    @Override
    public void send(FlightState status, String flightNumber) {
        System.out.println("Asunto: Actualización de tu vuelo #" + flightNumber);
        System.out.println("----------------------------------------------");
        System.out.println("Estimado pasajero,\n");
        System.out.println("Le informamos que el estado actual de su vuelo se encuentra en: " + status + ".");
        System.out.println("\nGracias por volar con nosotros.");
    }
}
