package com.jun.lucky;

import java.applet.Applet;
import java.awt.*;

public class TestApplet extends Applet {

    Button redbutton;
    public void paint(Graphics g)
    {
        setSize(500, 500);
        //设置组件的大小（但是一直不明白为什么组件大小改变，按钮的数量也会不一样）
        g.setColor(Color.red);
        g.drawString("我一边喝着咖啡，一边学Java呢", 5, 30);
        g.setColor(Color.blue);
        g.drawString("我学的很认真的", 10, 50);
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 100));//设置按钮的位置（左对齐）
        redbutton = new Button("我是一个红色按钮");
        redbutton.setBackground(Color.red);
        redbutton.setForeground(Color.white);
        add(redbutton);
    }
}
