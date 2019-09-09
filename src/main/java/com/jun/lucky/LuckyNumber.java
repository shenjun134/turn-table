package com.jun.lucky;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
public class LuckyNumber extends JFrame {
    static boolean start=false;
    boolean pause=false;
    JLabel jl1 = new JLabel();
    JLabel jl2 = new JLabel();
    JLabel jl3 = new JLabel();
    JLabel jl4 = new JLabel();
    JLabel jl5 = new JLabel();
    JLabel jl6 = new JLabel();
    JButton jb1 = new JButton("开始");
    JButton jb2 = new JButton("结束");
    printer1 p1 = new printer1(jl1);
    printer1 p2 = new printer1(jl2);
    printer1 p3 = new printer1(jl3);
    printer1 p4 = new printer1(jl4);
    printer1 p5 = new printer1(jl5);
    printer1 p6 = new printer1(jl6);

    public LuckyNumber() {
        setTitle("软件工程专业学生抽取器");
        setSize(500, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        init();

    }

    public void init() {

        add(jl1);
        add(jl2);
        add(jl3);
        add(jl4);
        add(jl5);
        add(jl6);
        add(jb1);
        add(jb2);
        jb1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                start();
            }

        });
        jb2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                start=pause;
            }

        });


        setVisible(true);
    }
    public void start(){
        start=true;
        Thread t1 = new Thread(p1);
        Thread t2 = new Thread(p2);
        Thread t3 = new Thread(p3);
        Thread t4 = new Thread(p4);
        Thread t5 = new Thread(p5);
        Thread t6 = new Thread(p6);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }
    public static void main(String []args){
        LuckyNumber y=new LuckyNumber();
        y.init();
    }
}
class printer1 implements Runnable {
    JLabel lbl = null;
    public printer1(JLabel lbl) {
        this.lbl = lbl;
    }

    public  void run() {
        while (LuckyNumber.start) {
            int a = (int) (0 + Math.random() * 9);
            try{
                Thread.sleep(100);
            }catch(InterruptedException e){
                e.printStackTrace();
            }

            String num = String.valueOf(a);
            lbl.setText(num);
        }

    }
}
