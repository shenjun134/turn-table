package com.jun.lucky;

import javax.swing.*;
import java.awt.*;

public class TestFrame {

    public void service(){

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        frame.setPreferredSize(new Dimension(200,200));

        Container contentPane = frame.getContentPane();
        JButton b1 = new JButton("≤‚ ‘");

        b1.setPreferredSize(new Dimension(150, 150));

        JPanel p1 = new JPanel();
        p1.add(b1);

        contentPane.add(p1);


//
//        frame.pack();
        frame.setBounds(0, 0, 200, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }

    public static void main(String[] args) {

        TestFrame t = new TestFrame();
        t.service();

    }
}
