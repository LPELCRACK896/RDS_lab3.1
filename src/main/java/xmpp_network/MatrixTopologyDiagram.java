package xmpp_network;

import javax.swing.*;
import java.awt.*;
/**
 * @author LPELCRACK896
 */
public class MatrixTopologyDiagram extends JPanel {
    private final MatrixTopology topology;

    public MatrixTopologyDiagram(MatrixTopology topology) {
        this.topology = topology;
    }
    /*
     * #################
     * #################
     * MAIN METHODS
     * #################
     * #################
     */

    /**
     * Displays the network topology as a graph.
     */
    public void displayTopology() {
        MatrixTopologyDiagram.display(topology);
    }

    /*
     * #################
     * #################
     * OVERRIDE JPANEL
     * #################
     * #################
     */

    /**
     * To draw the graph.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();
        int nodeRadius = 20;

        // Draw the nodes
        int size = topology.matrix.length;
        for (int i = 0; i < size; i++) {
            int x = (int) (Math.cos(2 * Math.PI * i / size) * (width / 2 - nodeRadius) + width / 2 - nodeRadius);
            int y = (int) (Math.sin(2 * Math.PI * i / size) * (height / 2 - nodeRadius) + height / 2 - nodeRadius);
            g.fillOval(x, y, nodeRadius * 2, nodeRadius * 2);
            g.drawString(topology.indexToNode.get(i), x + nodeRadius - 6, y + nodeRadius + 6);
        }

        // Draw the edges
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (topology.matrix[i][j] != Long.MAX_VALUE) {
                    int x1 = (int) (Math.cos(2 * Math.PI * i / size) * (width / 2 - nodeRadius) + width / 2);
                    int y1 = (int) (Math.sin(2 * Math.PI * i / size) * (height / 2 - nodeRadius) + height / 2);
                    int x2 = (int) (Math.cos(2 * Math.PI * j / size) * (width / 2 - nodeRadius) + width / 2);
                    int y2 = (int) (Math.sin(2 * Math.PI * j / size) * (height / 2 - nodeRadius) + height / 2);
                    g.drawLine(x1, y1, x2, y2);

                    // Draw the edge weight
                    long weight = topology.matrix[i][j];
                    String weightStr = String.valueOf(weight);
                    g.drawString(weightStr, (x1 + x2) / 2, (y1 + y2) / 2);
                }
            }
        }
    }
    /*
     * #################
     * #################
     * STATIC METHODS
     * #################
     * #################
     */

    /**
     * In a JPanel displays the network graph representation.
     * @param topology the topology as reference.
     */
    public static void display(MatrixTopology topology) {
        JFrame frame = new JFrame("Matrix Topology Diagram");
        MatrixTopologyDiagram diagram = new MatrixTopologyDiagram(topology);
        frame.add(diagram);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

