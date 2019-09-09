package com.jun.lucky;

import java.applet.Applet;
import java.awt.*;

public class TestApplet2 extends Applet {

    private Integer unitW = 50;
    private Integer padding = 10;
    private Integer totalWidth = unitW * 3 + padding*4;
    private Button[] buttons = new Button[9];
    private String[] textArr = new String[]{"ɳ��","������","���ͼ�","СС�ͼ�","ʳ��","��̷һ��","����ţ����","������","��ţ��"};

    @Override
    public void init() {
        super.init();
        Button button = null;
        for(int i =0 ;i < 8; i++){
            button = createBtn(textArr[i]);
            buttons[i] = button;
        }
        button = createBtn("��ʼ");
        buttons[8] = button;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        setSize(totalWidth, totalWidth);

        setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));//���ð�ť��λ�ã�����룩
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
