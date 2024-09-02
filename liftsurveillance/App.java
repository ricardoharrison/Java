package liftsurveillance;

import java.awt.Color;
import java.awt.GridLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import liftsurveillance.java.Lift;
import liftsurveillance.java.LiftState;

public class App {

    private static final int MAX_LENGTH = (int) (Math.pow(2, 14));

    public static void main(String[] args) {

        int port = 8000;
        String server = "localhost";

        JFrame frame = new JFrame("Surveillance Room");
        JPanel panel = new JPanel();

        frame.setResizable(true);
        frame.setSize(600, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLocation(0, 300);

        panel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel label1 = new JLabel("Lift 1 status will be shown here");
        JLabel label2 = new JLabel("Lift 2 status will be shown here");

        panel.add(label1);
        panel.add(label2);

        frame.add(panel);

        frame.setVisible(true);

        Lift lift1 = new Lift(1, port, server);
        Lift lift2 = new Lift(2, port, server);

        try {
            DatagramSocket socket = new DatagramSocket(port);
            byte[] receivedData = new byte[MAX_LENGTH];

            while (true) {
                DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                socket.receive(packet);

                String msg = new String(packet.getData(), 0, packet.getLength());
                String[] splitMsg = msg.split(";");
                switch (splitMsg[0]) {
                    case "1":
                        if (splitMsg[2].equals(LiftState.STATIONARY.toString())) {
                            label1.setForeground(Color.black);
                            label1.setText("Lift No. " + splitMsg[0] + " is currently " + splitMsg[2] + " at floor "
                                    + splitMsg[1]);
                        } else {
                            label1.setForeground(Color.blue);
                            label1.setText("Lift No. " + splitMsg[0] + " is currently " + splitMsg[2] + " to floor "
                                    + splitMsg[1]);
                        }
                        break;
                    case "2":
                        if (splitMsg[2].equals(LiftState.STATIONARY.toString())) {
                            label2.setForeground(Color.black);
                            label2.setText("Lift No. " + splitMsg[0] + " is currently " + splitMsg[2] + " at floor "
                                    + splitMsg[1]);
                        } else {
                            label2.setForeground(Color.blue);
                            label2.setText("Lift No. " + splitMsg[0] + " is currently " + splitMsg[2] + " to floor "
                                    + splitMsg[1]);
                        }
                        break;
                    default:
                        break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
