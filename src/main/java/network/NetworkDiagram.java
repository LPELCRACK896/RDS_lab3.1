package network;

import javax.swing.*;
import java.awt.*;

public class NetworkDiagram extends JPanel {
    private final Network network;

    public NetworkDiagram(Network network) {
        this.network = network;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int nodeRadius = 20;
        int nodeDiameter = nodeRadius * 2;

        // Dibuja los nodos
        for (int i = 0; i < network.getSize(); i++) {
            int x = (int) (Math.cos(2 * Math.PI * i / network.getSize()) * (width / 2 - nodeRadius) + width / 2 - nodeRadius);
            int y = (int) (Math.sin(2 * Math.PI * i / network.getSize()) * (height / 2 - nodeRadius) + height / 2 - nodeRadius);
            g.fillOval(x, y, nodeDiameter, nodeDiameter);
            g.drawString(network.getNodes().get(i).getAlias(), x + nodeRadius - 6, y + nodeRadius + 6);
        }

        // Dibuja las aristas
        for (int i = 0; i < network.getSize(); i++) {
            for (int j = 0; j < network.getSize(); j++) {
                if (network.getMatrix()[i][j] != Integer.MAX_VALUE) {
                    int x1 = (int) (Math.cos(2 * Math.PI * i / network.getSize()) * (width / 2 - nodeRadius) + width / 2);
                    int y1 = (int) (Math.sin(2 * Math.PI * i / network.getSize()) * (height / 2 - nodeRadius) + height / 2);
                    int x2 = (int) (Math.cos(2 * Math.PI * j / network.getSize()) * (width / 2 - nodeRadius) + width / 2);
                    int y2 = (int) (Math.sin(2 * Math.PI * j / network.getSize()) * (height / 2 - nodeRadius) + height / 2);
                    g.drawLine(x1, y1, x2, y2);

                    // Dibuja el peso de la arista
                    int weight = network.getMatrix()[i][j];
                    String weightStr = String.valueOf(weight);
                    g.drawString(weightStr, (x1 + x2) / 2, (y1 + y2) / 2);
                }
            }
        }
    }


    public static void display(Network network) {
        JFrame frame = new JFrame("Network Diagram");
        NetworkDiagram diagram = new NetworkDiagram(network);
        frame.add(diagram);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
