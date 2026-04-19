package notification;

public interface FlightSubject {
    void addListener(FlightStateListener listener);
    void removeListener(FlightStateListener listener);
    void notifyListeners();
}
