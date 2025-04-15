import javax.swing.*;
import java.awt.*;


public class Main extends JFrame {
    public static final int DISTANCE_BETWEEN_LIGHTS = 1000;
    private static MainPanel mainPanel;

    public Main(){

        super("Traffic Simulation");
        this.setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel = new MainPanel();

        this.add(mainPanel, BorderLayout.CENTER);
        mainPanel.setVisible(true);
        mainPanel.updateUI();
        pack();
    }


    public static void main(String[] args) {
        Main main = new Main();
        main.setVisible(true);
    }
}
