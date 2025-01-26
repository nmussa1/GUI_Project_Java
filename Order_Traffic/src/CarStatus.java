/*
Nezifa Mussa
CMSC 335 Project 3
Dr. Mihaela Dinsoreanu
07/09/2024
 */
public enum CarStatus {
    STOPPED("Stopped"),
    PULLED("Pulled Over"),
    DRIVING("Driving at "),
    ATLIGHT("At red light"),
    PAUSED("All Cars Paused");

    private String status;

    CarStatus(String status) {
        this.status=status;
    }

    public synchronized String getStatus() {
        return status;
    }
}
