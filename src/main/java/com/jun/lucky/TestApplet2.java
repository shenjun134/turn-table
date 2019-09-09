package com.jun.lucky;

import java.applet.Applet;
import java.awt.*;

public class TestApplet2 extends Applet {

    private Integer unitW = 50;
    private Integer padding = 10;
    private Integer totalWidth = unitW * 3 + padding*4;
    private Button[] buttons = new Button[9];
    private String[] textArr = new String[]{"沙县","辣鸡面","酱油鸡","小小和记","食堂","老谭一罐","淮南牛肉汤","陕西面","黄牛馆"};

    @Override
    public void init() {
        super.init();
        Button button = null;
        for(int i =0 ;i < 8; i++){
            button = createBtn(textArr[i]);
            buttons[i] = button;
        }
        button = createBtn("开始");
        buttons[8] = button;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setSize(totalWidth, totalWidth);

        setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));//设置按钮的位置（左对齐）
        for(Button button : buttons){
            add(button);
        }

    }

    private Button createBtn(String label){
        Button button = new Button();
        button.setLabel(label);
        button.setSize(unitW, unitW);
        button.setBackground(Color.blue);
        return button;
    }
}
