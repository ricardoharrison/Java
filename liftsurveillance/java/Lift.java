package liftsurveillance.java;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.GridLayout;

public class Lift {
    static final int TRAVELLING_TIME = 1900;
    static final int LAPSE = 100;
    static final int MAX_LENGTH = (int) (Math.pow(2, 14));
    String server;
    int port;
    int code;
    int floor = 0;
    LiftState direction = LiftState.STATIONARY;
    String infoMsg = "Currently at FLOOR ";
    JLabel labelInfo;

    public Lift(int code, int port, String server) {
        this.code = code;
        this.port = port;
        this.server = server;
        start();
    }

    private void start() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Lift No. " + code);
            JPanel panel = new JPanel();

            panel.setLayout(new GridLayout(3, 1, 10, 10));

            frame.setResizable(true);
            frame.setSize(300, 200);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setLocation(300 * (code - 1), 100);

            labelInfo = new JLabel(infoMsg + floor);
            JButton buttonUp = new JButton("Go up");
            JButton buttonDown = new JButton("Go down");

            buttonUp.addActionListener(li -> {
                goUp();
            });

            buttonDown.addActionListener(li -> {
                goDown();
            });

            panel.add(buttonUp);
            panel.add(buttonDown);
            panel.add(labelInfo);

            frame.add(panel);
            frame.setVisible(true);

            new Thread(() -> {
                while (true) {
                    try {
                        DatagramSocket socket = new DatagramSocket();
                        InetAddress ipAddress = InetAddress.getByName(server);
                        byte[] sentData = new byte[MAX_LENGTH];
                        String sentence = code + ";" + floor + ";" + direction.toString();

                        sentData = sentence.getBytes();

                        DatagramPacket sendPacket = new DatagramPacket(sentData, sentData.length, ipAddress, port);
                        socket.send(sendPacket);
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(LAPSE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        });
    }

    void goUp() {
        if (direction == LiftState.STATIONARY) {
            floor++;
            direction = LiftState.GOING_UP;
            labelInfo.setForeground(Color.blue);
            labelInfo.setText("Going up...");
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TRAVELLING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                direction = LiftState.STATIONARY;
                labelInfo.setForeground(Color.black);
                labelInfo.setText(infoMsg + floor);
            });
            thread.start();
        }
    }

    void goDown() {
        if (direction == LiftState.STATIONARY) {
            floor--;
            direction = LiftState.GOING_DOWN;
            labelInfo.setForeground(Color.blue);
            labelInfo.setText("Going down...");
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(TRAVELLING_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                direction = LiftState.STATIONARY;
                labelInfo.setForeground(Color.black);
                labelInfo.setText(infoMsg + floor);
            });
            thread.start();
        }
    }
}
