/*
Nezifa Mussa
CMSC 335 Project 3
Dr. Mihaela Dinsoreanu
07/09/2024
 */
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import net.miginfocom.swing.*;
public class Stoplight extends JPanel implements Runnable {
    private final int DISTANCE_BETWEEN_LIGHTS = Main.DISTANCE_BETWEEN_LIGHTS;
    final static JPanel[] images = new JPanel[4];
    private static int staticLightNumberCounter;
    final int lightNumber;
    private TrafficLightColor color;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    public final AtomicBoolean suspended = new AtomicBoolean(false);
    Thread thread;
    String threadName;
    public final int xPosition;
    private static int xTotalDistance;

    public Stoplight() {
        getPictures();
        initComponents();
        incrementStaticLightCounter();
        this.lightNumber = staticLightNumberCounter;
        xPosition = lightNumber * DISTANCE_BETWEEN_LIGHTS;
        xTotalDistance = xPosition;
        this.threadName = "Light " + lightNumber;
        thread = new Thread(this, threadName);
        lightNumberTF.setText(String.valueOf(lightNumber));
        color = TrafficLightColor.MAINTENANCE;
        updatePanel();
    }

    private synchronized void incrementStaticLightCounter() {
        staticLightNumberCounter += 1;
    }

    protected void getPictures() {
        String[] filenames = {"red.jpg", "yellow.jpg", "green.jpg", "all.jpg"};
        for (int i = 0; i < 4; i++) {
            String path = "/resources/" + filenames[i];
            JPanel jPanel = new JPanel();
            JLabel picture = new JLabel();
            ImageIcon icon;
            icon = new ImageIcon(getClass().getResource(path));
            picture.setIcon(icon);
            jPanel.add(picture);
            images[i] = jPanel;
        }
    }

    @Override
    public void run() {

        isRunning.set(true);
        while (isRunning.get()) {
            SwingUtilities.invokeLater(() -> updatePanel());
            try {
                synchronized (this) {
                    while (suspended.get()) {

                        SwingUtilities.invokeLater(() -> lightStatusTF.setText("MAINTENANCE"));
                        wait();
                    }
                }
                switch (getTrafficLightColor()) {
                    case GREEN:
                        setTrafficLightColor(TrafficLightColor.YELLOW);

                        Thread.sleep(1750);
                        break;
                    case YELLOW:
                        setTrafficLightColor(TrafficLightColor.RED);

                        Thread.sleep(5000);
                        break;
                    case RED:
                        setTrafficLightColor(TrafficLightColor.GREEN);

                        Thread.sleep(6000);
                        break;
                    case MAINTENANCE:
                        setTrafficLightColor(TrafficLightColor.RED);

                        Thread.sleep(lightNumber * 500L);
                    default:
                        break;
                }
            } catch (InterruptedException exc) {
                suspended.set(true);
            }
        }
    }

    public static int getStaticLightNumberCounter() {
        return staticLightNumberCounter;
    }

    private synchronized void updatePanel() {
        lightImagePanel.removeAll();
        lightImagePanel.add(images[getTrafficLightColor().ordinal()]);
        lightImagePanel.updateUI();
        lightStatusTF.setText(getTrafficLightColor().toString());
        this.updateUI();
    }

    public synchronized void resume() {
        suspended.set(false);
        notify();

    }

    public synchronized void stop() {
        thread.interrupt();
        isRunning.set(false);

    }

    public void interrupt() {
        thread.interrupt();
    }

    public void suspend() {
        interrupt();
        suspended.set(true);

        lightStatusTF.setText("MAINTENANCE");
    }

    synchronized void setTrafficLightColor(TrafficLightColor c) {
        this.color = c;
    }

    public TrafficLightColor getTrafficLightColor() {
        return this.color;
    }

    public static synchronized int getxTotalDistance() {
        return xTotalDistance;
    }

    private void initComponents() {
        infoPanel = new JPanel();
        l1 = new JLabel();
        lightNumberTF = new JTextField();
        l2 = new JLabel();
        lightStatusTF = new JTextField();
        lightImagePanel = new JPanel();

        setBorder(LineBorder.createBlackLineBorder());
        setMinimumSize(new Dimension(200, 460));
        setMaximumSize(new Dimension(200, 460));
        setPreferredSize(new Dimension(200, 460));
        setLayout(new MigLayout(
                "hidemode 3,alignx center",
                "[0,fill]0" +
                        "[206,fill]",
                "[42]" +
                        "[]0"));

        infoPanel.setBorder(LineBorder.createBlackLineBorder());
        infoPanel.setLayout(new GridLayout(2, 0));

        l1.setText("Light Number:");
        l1.setPreferredSize(new Dimension(30, 13));
        l1.setHorizontalAlignment(SwingConstants.CENTER);
        l1.setMaximumSize(null);
        l1.setMinimumSize(null);
        infoPanel.add(l1);

        lightNumberTF.setColumns(2);
        lightNumberTF.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lightNumberTF);

        l2.setText("Status:");
        l2.setPreferredSize(new Dimension(30, 13));
        l2.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(l2);

        lightStatusTF.setColumns(8);
        lightStatusTF.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(lightStatusTF);

        add(infoPanel, "cell 1 0");

        lightImagePanel.setMinimumSize(new Dimension(129, 377));
        lightImagePanel.setMaximumSize(new Dimension(129, 377));
        lightImagePanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
        lightImagePanel.setLayout(new BorderLayout());
        add(lightImagePanel, "cell 1 1,align center center,grow 0 0");
    }

    private JPanel infoPanel;
    private JLabel l1;
    private JTextField lightNumberTF;
    private JLabel l2;
    private JTextField lightStatusTF;
    private JPanel lightImagePanel;
}
