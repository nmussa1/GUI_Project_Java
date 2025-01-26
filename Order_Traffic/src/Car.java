/*
Nezifa Mussa
CMSC 335 Project 3
Dr. Mihaela Dinsoreanu
07/09/2024
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import net.miginfocom.swing.*;

public class Car extends JPanel implements Runnable {
    private final int DISTANCE_BETWEEN_LIGHTS = Main.DISTANCE_BETWEEN_LIGHTS;
    private static int staticCarNumberCounter;
    private static Integer initialSpeed;
    final int carNumber;
    private int speed;
    private int xPosition;
    private TrafficLightColor nextLightColor;
    private Thread thread;
    private int totalDistance;
    String threadName;
    final DefaultBoundedRangeModel model = new DefaultBoundedRangeModel();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public final AtomicBoolean suspended = new AtomicBoolean(false);
    private CarStatus carStatus;
    private boolean done;
    private MainPanel mainPanel;
    private int nextLightNumber;
    final TrafficLightColor YELLOW = TrafficLightColor.YELLOW;
    final TrafficLightColor RED = TrafficLightColor.RED;

    public Car(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        initComponents();
        staticCarNumberCounter += 1;
        this.carNumber = staticCarNumberCounter;
        setInitialSpeed();
        speed = initialSpeed;
        xPosition = 0;
        totalDistance = Stoplight.getxTotalDistance();
        this.threadName = "Car " + carNumber;
        thread = new Thread(this, threadName);
        speedSpinner.setValue(initialSpeed);
        carStatus = CarStatus.STOPPED;
        carStatusTF.setText(carStatus.getStatus());
        carNumberTF.setText(String.valueOf(carNumber));
        progressBar = new JProgressBar(0, DISTANCE_BETWEEN_LIGHTS * 3);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
    }

    private void setInitialSpeed() {
        if (initialSpeed == null) {
            initialSpeed = 20;
        } else if (initialSpeed == 20) {
            initialSpeed = 50;
        } else if (initialSpeed == 50) {
            initialSpeed = 125;
        } else {
            Random r = new Random();
            initialSpeed = r.nextInt(100 - 35) + 35;
        }
    }

    private double getFPS() {
        return speed * 1.46667;
    }

    @Override
    public void run() {

        model.setMinimum(0);
        model.setMaximum(totalDistance);
        isRunning.set(true);
        carStatus = CarStatus.DRIVING;
        while (isRunning.get()) {
            SwingUtilities.invokeLater(this::updateCarPanel);
            try {
                while (suspended.get()) {
                    if (carStatus == CarStatus.ATLIGHT) {
                        if (checkNextLight() == TrafficLightColor.GREEN || checkNextLight() == TrafficLightColor.MAINTENANCE) {
                            resume();
                        }
                    }

                    Thread.sleep(500);
                }
                if (xPosition > totalDistance) {
                    xPosition = 0;
                }
                totalDistance = Stoplight.getxTotalDistance();
                model.setMaximum(totalDistance);
                model.setValue(getxPosition());
                xPosition += getFPS() / 4;
                Thread.sleep(250);
                nextLightNumber = xPosition / DISTANCE_BETWEEN_LIGHTS;
                if (nextLightNumber >= Stoplight.getStaticLightNumberCounter()) {
                    nextLightNumber = 0;
                }

                nextLightColor = checkNextLight();
                if (checkIfStop() && (nextLightColor == YELLOW || nextLightColor == RED)) {
                    carStatus = CarStatus.ATLIGHT;
                    interrupt();
                    suspend(carStatus);
                }
                progressBar.setValue(getxPosition());
                this.updateUI();
                progressBar.updateUI();
            } catch (InterruptedException exc) {
                model.setValue(getxPosition());
                suspended.set(true);
            }
        }
    }

    private boolean checkIfStop() {
        boolean necessary = false;
        double fps = getFPS();
        int distanceToLight = DISTANCE_BETWEEN_LIGHTS - getxPosition() % DISTANCE_BETWEEN_LIGHTS;
        if (distanceToLight < fps / 2) {
            necessary = true;
        }
        return necessary;
    }

    private TrafficLightColor checkNextLight() {
        try {
            return mainPanel.getIntersections().get(nextLightNumber).getTrafficLightColor();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Driver in " + threadName + " ran a red light!");
            return TrafficLightColor.RED;
        }
    }

    private String getStatus() {
        switch (carStatus) {
            case STOPPED:
            case PULLED:
            case ATLIGHT:
                return carStatus.getStatus();
            case DRIVING:
                return carStatus.getStatus() + speed + " mph";
            default:
                return null;
        }
    }

    private void updateCarPanel() {
        carStatusTF.setText(getStatus());
        xPositionTF.setText(getxPosition() + " / " + totalDistance);
        progressBar.setValue(getxPosition());
        this.updateUI();
        progressBar.updateUI();
    }

    public synchronized int getStaticCarNumberCounter() {
        return staticCarNumberCounter;
    }

    public synchronized void resume() {
        suspended.set(false);
        this.carStatus = CarStatus.DRIVING;
        carStatusTF.setText(getStatus());
        notify();

    }

    public synchronized void stop() {
        thread.interrupt();
        this.carStatus = CarStatus.STOPPED;
        isRunning.set(false);

        carStatusTF.setText(getStatus());
    }

    public void interrupt() {
        thread.interrupt();
    }

    public void suspend(CarStatus carStatus) {
        this.carStatus = carStatus;
        carNumberTF.setText(String.valueOf(carNumber));
        SwingUtilities.invokeLater(this::updateCarPanel);

        suspended.set(true);
    }

    private void speedSpinnerStateChanged(ChangeEvent e) {
        speed = (Integer) speedSpinner.getValue();
    }

    private void pullOverButtonPressed(ActionEvent actionEvent) {

        if (isRunning.get()) {
            if (!suspended.get()) {

                interrupt();
                suspend(CarStatus.PULLED);
            } else {

                resume();
            }
        }
    }

    private synchronized int getxPosition() {
        return xPosition;
    }

    private void initComponents() {
        panel9 = new JPanel();
        label7 = new JLabel();
        carNumberTF = new JLabel();
        label10 = new JLabel();
        xPositionTF = new JTextField();
        label8 = new JLabel();
        carStatusTF = new JTextField();
        label9 = new JLabel();
        speedSpinner = new JSpinner();
        pullOverButton = new JButton();
        progressBar = new JProgressBar(model);

        setBorder(LineBorder.createBlackLineBorder());
        setPreferredSize(new Dimension(100, 82));
        setMinimumSize(new Dimension(300, 82));
        setMaximumSize(new Dimension(2147483647, 82));
        setLayout(new BorderLayout());

        panel9.setPreferredSize(new Dimension(100, 80));
        panel9.setMinimumSize(new Dimension(300, 82));
        panel9.setLayout(new MigLayout(
                "insets 10 10 10 18,hidemode 3",
                "[74,fill]0" +
                        "[42,fill]0" +
                        "[fill]0" +
                        "[fill]0" +
                        "[fill]0" +
                        "[43,fill]0" +
                        "[98,fill]0" +
                        "[fill]0" +
                        "[fill]0" +
                        "[fill]0" +
                        "[78,fill]0" +
                        "[fill]0" +
                        "[59,fill]0" +
                        "[7,fill]0" +
                        "[93,fill]" +
                        "[fill]" +
                        "[198,fill]",
                "[]" +
                        "[]" +
                        "[]"));

        label7.setText("Car # :");
        label7.setHorizontalAlignment(SwingConstants.CENTER);
        panel9.add(label7, "cell 0 0");

        carNumberTF.setText(String.valueOf(carNumber));
        carNumberTF.setMinimumSize(new Dimension(20, 28));
        carNumberTF.setMaximumSize(new Dimension(20, 2147483647));
        panel9.add(carNumberTF, "cell 0 0");

        label10.setText("X Position:");
        label10.setHorizontalAlignment(SwingConstants.CENTER);
        panel9.add(label10, "cell 2 0");

        xPositionTF.setColumns(20);
        xPositionTF.setText("0");
        xPositionTF.setFocusable(false);
        xPositionTF.setHorizontalAlignment(SwingConstants.CENTER);
        panel9.add(xPositionTF, "cell 4 0");

        label8.setText("Status: ");
        label8.setHorizontalAlignment(SwingConstants.RIGHT);
        panel9.add(label8, "cell 6 0");

        carStatusTF.setColumns(25);
        carStatusTF.setText("Driving @ X mph");
        carStatusTF.setFocusable(false);
        panel9.add(carStatusTF, "cell 8 0");

        label9.setText("Speed: ");
        label9.setHorizontalAlignment(SwingConstants.RIGHT);
        panel9.add(label9, "cell 10 0");

        speedSpinner.setModel(new SpinnerNumberModel(15, 0, 225, 5));
        JComponent editor = speedSpinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setBackground(Color.LIGHT_GRAY);
        }
        speedSpinner.addChangeListener(this::speedSpinnerStateChanged);
        panel9.add(speedSpinner, "cell 12 0");

        pullOverButton.setText("Pull Over / Drive");
        pullOverButton.setBackground(Color.ORANGE);
        pullOverButton.setForeground(Color.BLACK);
        pullOverButton.addActionListener(this::pullOverButtonPressed);
        panel9.add(pullOverButton, "cell 14 0");

        progressBar.setMinimumSize(new Dimension(10, 25));
        panel9.add(progressBar, "cell 0 1 17 1");
        add(panel9, BorderLayout.CENTER);
    }

    public JPanel panel9;
    private JLabel label7;
    public JLabel carNumberTF;
    private JLabel label10;
    public JTextField xPositionTF;
    private JLabel label8;
    public JTextField carStatusTF;
    private JLabel label9;
    public JSpinner speedSpinner;
    public JButton pullOverButton;
    public JProgressBar progressBar;
}
