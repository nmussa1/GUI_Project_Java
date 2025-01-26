/*
Nezifa Mussa
CMSC 335 Project 3
Dr. Mihaela Dinsoreanu
07/09/2024
 */
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;

import net.miginfocom.swing.*;

public class MainPanel extends JPanel {
    private final int INITIAL_LIGHTS_AND_CARS = 3;
    private ArrayList<Stoplight> intersections;
    private String timePattern = "hh:mm:ss a";
    private ArrayList<Thread> intersectionThreads;
    private ArrayList<Car> vehicles;
    private ArrayList<Thread> vehicleThreads;
    private static final AtomicBoolean lightsRunningBoolean = new AtomicBoolean(false);
    private static final AtomicBoolean vehiclesRunningBoolean = new AtomicBoolean(false);
    int lightsLoopCounter, vehiclesLoopCounter;

    public MainPanel() {
        initComponents();
        fillMainPanel();
        startClock();
    }

    private void startClock() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String string = new SimpleDateFormat("HH:mm:ss").format(new Date());
                timeLabel.setText(string);
            }
        }, 0, 1000);
    }

    private void fillMainPanel() {
        intersections = new ArrayList<>();
        intersectionThreads = new ArrayList<>();
        vehicles = new ArrayList<>();
        vehicleThreads = new ArrayList<>();

        for (int i = 0; i < INITIAL_LIGHTS_AND_CARS; i++) {
            intersections.add(new Stoplight());
            vehicles.add(new Car(this));
            intersectionThreads.add(new Thread(intersections.get(i)));
            vehicleThreads.add(new Thread(vehicles.get(i)));
            this.stoplightContainer.add(intersections.get(i));
            this.carsContainer.add(vehicles.get(i));
        }
        this.stoplightContainer.updateUI();
        this.carsContainer.updateUI();
    }

    public void startLights(ActionEvent e) {
        lightsLoopCounter = Stoplight.getStaticLightNumberCounter();
        try {
            for (int i = 0; i < lightsLoopCounter; i++) {
                intersectionThreads.get(i).start();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            lightsRunningBoolean.set(true);
        } catch (IllegalThreadStateException ignore) {}
    }

    public void pauseLights(ActionEvent e) {
        if (lightsRunningBoolean.get()) {
            for (Stoplight s : intersections) {
                s.interrupt();
                s.suspend();
            }
            lightsRunningBoolean.set(false);
            pauseLights.setText("Resume Lights");
        } else {
            for (Stoplight s : intersections) {
                s.resume();
                try {
                    Thread.sleep(750);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            lightsRunningBoolean.set(true);
            pauseLights.setText("Pause Lights");
        }
    }

    private void addIntersection(ActionEvent e) {
        intersections.add(new Stoplight());
        intersectionThreads.add(new Thread(intersections.get(intersections.size() - 1)));
        stoplightContainer.add(intersections.get(intersections.size() - 1));
        stoplightContainer.updateUI();
        intersectionThreads.get(intersections.size() - 1).start();
    }

    private void addCar(ActionEvent e) {
        vehicles.add(new Car(this));
        vehicleThreads.add(new Thread(vehicles.get(vehicles.size() - 1)));
        carsContainer.add(vehicles.get(vehicles.size() - 1));
        carsContainer.updateUI();
        vehicleThreads.get(vehicles.size() - 1).start();
    }

    private void startCars(ActionEvent e) {
        vehiclesLoopCounter = vehicles.get(0).getStaticCarNumberCounter();
        if (!vehiclesRunningBoolean.get()) {
            for (int i = 0; i < vehiclesLoopCounter; i++) {
                try {
                    vehicleThreads.get(i).start();
                } catch (IllegalThreadStateException ignore) {}
            }
            vehiclesRunningBoolean.set(true);
        } else {

        }
    }

    private void pauseCars(ActionEvent e) {
        if (vehiclesRunningBoolean.get()) {
            for (Car c : vehicles) {
                c.interrupt();
                c.suspend(CarStatus.PAUSED);
            }
            vehiclesRunningBoolean.set(false);
            pauseCars.setText("Resume Cars");
        } else {
            pauseCars.setText("Pause Cars");
            for (Car c : vehicles) {
                c.resume();
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            vehiclesRunningBoolean.set(true);
        }
    }

    private void exitProgram(ActionEvent e) {
        int i = JOptionPane.showConfirmDialog(this,
                "Thank you for using this program. Goodbye!", "Exit?", JOptionPane.OK_CANCEL_OPTION);
        if (i == 0) {
            boolean grace = false;
            if (vehiclesRunningBoolean.get()) {
                for (Car c : vehicles) {
                    c.interrupt();
                    c.stop();
                }
                vehiclesRunningBoolean.set(false);
                grace = true;
            }
            if (lightsRunningBoolean.get()) {
                for (Stoplight s : intersections) {
                    s.interrupt();
                    s.stop();
                }
                lightsRunningBoolean.set(false);
                grace = true;
            }
            if (grace) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
            System.exit(0);
        }
    }

    public synchronized ArrayList<Stoplight> getIntersections() {
        return intersections;
    }

    private void initComponents() {
        UIManager.put("control", new Color(128, 128, 128));
        UIManager.put("info", new Color(128, 128, 128));
        UIManager.put("nimbusBase", new Color(18, 30, 49));
        UIManager.put("nimbusAlertYellow", new Color(248, 187, 0));
        UIManager.put("nimbusDisabledText", new Color(128, 128, 128));
        UIManager.put("nimbusFocus", new Color(115, 164, 209));
        UIManager.put("nimbusGreen", new Color(176, 179, 50));
        UIManager.put("nimbusInfoBlue", new Color(66, 139, 221));
        UIManager.put("nimbusLightBackground", new Color(18, 30, 49));
        UIManager.put("nimbusOrange", new Color(191, 98, 4));
        UIManager.put("nimbusRed", new Color(169, 46, 34));
        UIManager.put("nimbusSelectedText", new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104, 93, 156));
        UIManager.put("text", new Color(230, 230, 230));

        toolBar1 = new JToolBar();
        label1 = new JLabel();
        timeLabel = new JLabel();
        splitPane2 = new JSplitPane();
        panel7 = new JPanel();
        scrollPane1 = new JScrollPane();
        splitPane3 = new JSplitPane();
        carsContainer = new CarsContainer();
        panel3 = new JPanel();
        stoplightContainer = new StoplightContainer();
        panel8 = new JPanel();
        panel6 = new JPanel();
        startCars = new JButton();
        pauseCars = new JButton();
        addCarButton = new JButton();
        vSpacer1 = new JPanel(null);
        exitButton = new JButton();
        vSpacer4 = new JPanel(null);
        startButton = new JButton();
        pauseLights = new JButton();
        addInterButton = new JButton();

        setPreferredSize(new Dimension(1350, 1120));
        setLayout(new BorderLayout());

        label1.setText("Current Time: ");
        label1.setFont(label1.getFont().deriveFont(label1.getFont().getSize() + 4f));
        toolBar1.add(label1);

        timeLabel.setText("Current Time");
        timeLabel.setFont(timeLabel.getFont().deriveFont(timeLabel.getFont().getStyle() | Font.BOLD, timeLabel.getFont().getSize() + 4f));
        toolBar1.add(timeLabel);
        toolBar1.setBackground(new Color(45, 45, 45));
        label1.setForeground(new Color(255, 255, 255));
        timeLabel.setForeground(new Color(255, 255, 255));
        add(toolBar1, BorderLayout.NORTH);

        splitPane2.setResizeWeight(1.0);
        panel7.setLayout(new GridLayout());
        scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        splitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane3.setResizeWeight(1.0);
        splitPane3.setTopComponent(carsContainer);

        panel3.setLayout(new MigLayout("insets 10,hidemode 3", "[grow,fill]", "[grow,fill]"));
        panel3.add(stoplightContainer, "cell 0 0");
        splitPane3.setBottomComponent(panel3);
        scrollPane1.setViewportView(splitPane3);
        panel7.add(scrollPane1);
        splitPane2.setLeftComponent(panel7);

        panel8.setMinimumSize(new Dimension(120, 181));
        panel8.setPreferredSize(new Dimension(80, 181));
        panel8.setMaximumSize(new Dimension(120, 2147483647));
        panel8.setLayout(new MigLayout("insets 05 5 5 5,hidemode 3", "[150,grow,fill]", "[grow,fill]"));

        panel6.setMinimumSize(new Dimension(100, 196));
        panel6.setLayout(new GridLayout(9, 0, 4, 4));

        startCars.setText("Start Cars");
        startCars.setBackground(new Color(34, 139, 34));
        startCars.setForeground(new Color(255, 255, 255));
        startCars.addActionListener(e -> startCars(e));
        panel6.add(startCars);

        pauseCars.setText("Pause Cars");
        pauseCars.setBackground(new Color(255, 69, 0));
        pauseCars.setForeground(new Color(255, 255, 255));
        pauseCars.addActionListener(e -> pauseCars(e));
        panel6.add(pauseCars);

        addCarButton.setText("Add Car");
        addCarButton.setBackground(new Color(30, 144, 255));
        addCarButton.setForeground(new Color(255, 255, 255));
        addCarButton.addActionListener(e -> addCar(e));
        panel6.add(addCarButton);
        panel6.add(vSpacer1);

        exitButton.setText("EXIT PROGRAM");
        exitButton.setBackground(new Color(128, 0, 0));
        exitButton.setForeground(new Color(255, 255, 255));
        exitButton.addActionListener(e -> exitProgram(e));
        panel6.add(exitButton);
        panel6.add(vSpacer4);

        startButton.setText("Start Lights");
        startButton.setBackground(new Color(34, 139, 34));
        startButton.setForeground(new Color(255, 255, 255));
        startButton.addActionListener(e -> startLights(e));
        panel6.add(startButton);

        pauseLights.setText("Pause Lights");
        pauseLights.setBackground(new Color(255, 69, 0));
        pauseLights.setForeground(new Color(255, 255, 255));
        pauseLights.addActionListener(e -> pauseLights(e));
        panel6.add(pauseLights);

        addInterButton.setText("Add Intersection");
        addInterButton.setBackground(new Color(30, 144, 255));
        addInterButton.setForeground(new Color(255, 255, 255));
        addInterButton.addActionListener(e -> addIntersection(e));
        panel6.add(addInterButton);

        panel8.add(panel6, "cell 0 0");
        splitPane2.setRightComponent(panel8);
        add(splitPane2, BorderLayout.CENTER);
    }

    protected JToolBar toolBar1;
    private JLabel label1;
    private JLabel timeLabel;
    protected JSplitPane splitPane2;
    protected JPanel panel7;
    protected JScrollPane scrollPane1;
    protected JSplitPane splitPane3;
    public CarsContainer carsContainer;
    protected JPanel panel3;
    public StoplightContainer stoplightContainer;
    protected JPanel panel8;
    protected JPanel panel6;
    private JButton startCars;
    private JButton pauseCars;
    protected JButton addCarButton;
    protected JPanel vSpacer1;
    private JButton exitButton;
    protected JPanel vSpacer4;
    protected JButton startButton;
    protected JButton pauseLights;
    protected JButton addInterButton;

    public static class StoplightContainer extends JPanel {
        private StoplightContainer() {
            initComponents();
        }

        private void initComponents() {
            setMinimumSize(new Dimension(610, 460));
            setMaximumSize(new Dimension(50000, 460));
            setPreferredSize(new Dimension(610, 460));
            setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        }
    }

    public static class CarsContainer extends JPanel {
        private CarsContainer() {
            initComponents();
        }

        private void initComponents() {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }
    }
}
