package com.jun.lucky;

import sun.audio.AudioData;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.audio.ContinuousAudioDataStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class LuckyTable extends JFrame {

    private Properties properties = new Properties();

    private Integer unitW = 200;
    private Integer padding = 10;
    private Integer totalWidth;
    private String beginLabel;
    private String endLabel;
    private JButton[] buttons = new JButton[8];

    private JButton bingo;
    private java.util.List<String> choiceAList;
    private String defaultChoice;

    private JButton btnStart;
    private AtomicBoolean startTurn = new AtomicBoolean(false);
    private AtomicBoolean swaping = new AtomicBoolean(false);
    private Thread luckyThread;
    private AudioStream as4Background;
    private AudioStream as4Bingo;
    private ContinuousAudioDataStream cads4Background;
    private URL url4Back;
    private URL url4Bingo;

    private final int[] position = new int[]{0, 1, 2, 4, 7, 6, 5, 3};
    private final int[] swapBegin = new int[]{0, 2, 7, 5};
    private final int[] swapEnd = new int[]{1, 4, 6, 3};

    public void init() throws IOException {
        properties.load(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("lucky.properties"), "GBK"));
        initParam();
        initMusic();
        setTitle(properties.getProperty("lucky.title"));
        setSize(totalWidth, totalWidth);
        setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));
        JButton button = null;
        int j = 0;
        for (int i = 0; i < 9; i++) {
            button = createBtn(choiceAList.get(j));
            if (i == 4) {
                j--;
                button.setText(beginLabel);
                btnStart = button;
            } else {
                buttons[j] = button;
            }
            add(button);
            j++;
        }
        setLocationRelativeTo(null);
        setVisible(true);
        setStartBtnEvent();
    }

    private void initParam() {
        defaultChoice = properties.getProperty("lucky.default.choice");
        if (defaultChoice == null || defaultChoice.length() == 0) {
            throw new IllegalArgumentException("Missing default choice");
        }
        unitW = getInt("lucky.unit.width");
        if(unitW < 20){
            throw new IllegalArgumentException("Unit width < 20");
        }
        beginLabel = properties.getProperty("lucky.beginBtn.label");
        endLabel = properties.getProperty("lucky.endBtn.label");
        totalWidth = unitW * 3 + padding * 4;
        String choiceList = properties.getProperty("lucky.choice.list");
        String[] array = choiceList.split(",");
        choiceAList = new ArrayList<String>();
        for (String item : array) {
            if (item == null || item.trim().length() == 0) {
                continue;
            }
            choiceAList.add(item.trim());
        }
        if (choiceAList.size() < 8) {
            for (int i = 0; i < 8 - choiceAList.size(); i++) {
                choiceAList.add(defaultChoice);
            }
        }
    }

    private void setStartBtnEvent() {
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (startTurn.get()) {
                    stopTurn();
                } else {
                    startTurn();
                }
            }
        });
        btnStart.setFocusPainted(true);
    }

    private void startTurn() {
        System.out.println("begin to startTurn ... ");
        if (startTurn.get()) {
            System.out.println("startTurn is running");
            return;
        }
        randomChoice();
        playBackgroundMusic();
        startTurn.set(true);
        reset();
        btnStart.setText(endLabel);
        luckyThread = new Thread() {
            @Override
            public void run() {
                while (startTurn.get()) {
                    if (!goNormal()) {
                        break;
                    }
                }
            }
        };
        luckyThread.start();
    }

    private void stopTurn() {
        System.out.println("begin to stopTurn ... ");

        if (startTurn.get()) {
            startTurn.set(false);
            btnStart.setText(beginLabel);
            System.out.println("stop turn done. ");
        } else {
            System.out.println("turn has already stopped.. ");
        }
    }

    private void randomChoice(){
        if (getBool("lucky.choice.shuffle")) {
            Collections.shuffle(choiceAList);
        }
        boolean existDef = false;
        boolean rdBool = randomBool();

        for (int i =0; i< 8; i++) {
            String choice = choiceAList.get(i);
            if(!existDef){
                existDef = defaultChoice.equals(choice);
            }
            buttons[i].setText(choice);
        }
        if(!existDef && rdBool){
            int pos = randomInt(7, 0);
            buttons[pos].setText(defaultChoice);
        }
    }


    private boolean goNormal() {
        boolean result = true;
        int size = getInt("lucky.level.size");
        for(int i =1; i <= size; i++){
            try{
                if(result){
                    result = goNormal(i);
                }
            }catch (IllegalArgumentException e){
                System.out.println(e.getLocalizedMessage());
                break;
            }
        }
        if (result) {
            goSwapLoop();
        }
        return result;
    }

    private boolean goNormal(int index){
        String keySleep = String.format("lucky.level-%d.sleep", index);
        String keyLoop = String.format("lucky.level-%d.loop", index);
        int level1Sleep = getInt(keySleep);
        int level1Loop = getInt(keyLoop);
        if(level1Loop == 0 || level1Sleep == 0){
            throw new IllegalArgumentException(String.format("Missing %s or %s", keySleep, keyLoop));
        }
        System.out.println(String.format("begin to go with loop:%d, wait:%d, index:%d", level1Loop, level1Sleep, index));
        return goNormal(level1Loop, level1Sleep);
    }

    private synchronized void reset() {
        for (JButton button : buttons) {
            resetBtn(button);
        }
        bingo = null;

    }

    private synchronized void goSwapLoop() {
        int swapLoop = getInt("lucky.swap.loop");
        int swapSleep = getInt("lucky.swap.sleep");
        int swapInterval = getInt("lucky.swap.active.interval");
        for (int i = 0; i < swapLoop; i++) {
            boolean result = goSwap(swapSleep, swapInterval);
            if (!result) {
                reset();
                randomBingo();
                return;
            }
        }
        String foreverVal = properties.getProperty("lucky.forever");
        if (Boolean.valueOf(foreverVal)) {
            return;
        }
        stopTurn();
        randomBingo();
    }


    private boolean goSwap(int wait, int interval) {
        System.out.println("Begin to swap - " + wait + ", [" + Thread.currentThread().getName() + "]");
        boolean result = swap(swapBegin, swapEnd, wait, interval);
        if (result) {
            result = swap(swapEnd, swapBegin, wait, interval);
        }
        return result;
    }

    private boolean swap(int[] begin, int[] end, int wait, int interval) {
        int times = getInt("lucky.swap.active.times");
        for (int i = 0; i < times; i++) {
            if (!batchActiveAndReset(interval, begin)) {
                return false;
            }
        }
        boolean result = sleepAndCheck(wait);
        if (result) {
            for (int pos : end) {
                resetBtn(buttons[pos]);
            }
        }
        return sleepAndCheck(wait);
    }

    private boolean batchActiveAndReset(int interval, int[] position) {
        for (int pos : position) {
            activeBtn(buttons[pos]);
        }
        boolean result = sleepAndCheck(interval);
        if (result) {
            for (int pos : position) {
                resetBtn(buttons[pos]);
            }
        }
        result = sleepAndCheck(interval);
        return result;
    }

    private void activeBtn(JButton jButton) {
        jButton.setBackground(Color.red);
        jButton.setForeground(Color.white);
    }

    private Font defaultFont(){
        Font font = new Font("Default", 1,getInt("lucky.fontSize"));
        return font;
    }

    private void resetBtn(JButton jButton) {
        jButton.setBackground(new JButton().getBackground());
        jButton.setForeground(Color.black);
    }

    private boolean goNormal(int loop, int wait) {
        if (loop < 0 || wait < 0) {
            throw new IllegalArgumentException("loop or wait less than 0");
        }
        for (int i = 0; i < loop; i++) {
            for (int pos : position) {
                JButton jButton = buttons[pos];
                activeBtn(jButton);
                if (sleepAndCheck(wait, jButton)) {
                    resetBtn(jButton);
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean sleepAndCheck(int wait, JButton jButton) {
        try {
            Thread.sleep(Long.valueOf(wait));
            if (!startTurn.get()) {
                bingo(jButton);
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean sleepAndCheck(int wait) {
        try {
            Thread.sleep(Long.valueOf(wait));
            if (!startTurn.get()) {
                return false;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }


    private JButton createBtn(String label) {
        JButton button = new JButton();
        button.setFont(defaultFont());
        button.setText(label);
        button.setPreferredSize(new Dimension(unitW - 5, unitW - 13));
        return button;
    }

    private int getInt(String key) {
        String val = properties.getProperty(key);
        if (val == null || val.trim().length() == 0) {
            return 0;
        }
        return Integer.valueOf(val.trim());
    }

    private boolean getBool(String key){
        String val = properties.getProperty(key);
        return Boolean.valueOf(val);
    }

    private boolean randomBool(){
        return randomInt(100, 1) > 50;
    }

    private int randomInt(int max, int min){
        int rdBegin = (int)(Math.random() * Integer.MAX_VALUE % max);
        return rdBegin > min ? rdBegin : min;

    }

    private void randomBingo() {
        java.util.List<JButton> btnList = new ArrayList<JButton>(Arrays.asList(buttons));
        Collections.shuffle(btnList);
        bingo(btnList.get(0));
    }

    private void initMusic(){
        String backgroundUrl = properties.getProperty("lucky.background.music");
        String bingoUrl = properties.getProperty("lucky.bingo.music");
        url4Back = Thread.currentThread().getContextClassLoader().getResource(backgroundUrl);
        url4Bingo = Thread.currentThread().getContextClassLoader().getResource(bingoUrl);
        if(url4Back == null){
            System.out.println("No music found");
            return;
        }
        try {
            as4Background = new AudioStream(url4Back.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void playBackgroundMusic(){
        System.out.println("begin to playBackgroundMusic");
        // Create AudioData source.
        if(cads4Background != null){
            AudioPlayer.player.start(cads4Background);
            return;
        }
        AudioData data = null;
        try {
            data = as4Background.getData();
        } catch(IOException e) {
            e.printStackTrace();
        }
        // Create ContinuousAudioDataStream.
        cads4Background = new ContinuousAudioDataStream(data);

        // Play audio.
        AudioPlayer.player.start(cads4Background);
    }

    private void playBingo(){
        if(url4Bingo != null){
            try {
                as4Bingo =  new AudioStream(url4Bingo.openStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(as4Bingo != null){
            Thread thread = new Thread(){
                @Override
                public void run() {
                    AudioPlayer.player.start(as4Bingo);
                }
            };
            thread.start();
        }
    }

    private void stopBingo(){
        if(as4Bingo != null){
            AudioPlayer.player.stop(as4Bingo);
        }
    }

    private void stopBackgroundMusic(){
        System.out.println("begin to stopBackgroundMusic");
        if(cads4Background != null) {
            AudioPlayer.player.stop(cads4Background);
        }
    }


    private void bingo(JButton jButton) {
        try {
            stopBackgroundMusic();
            playBingo();
            int times = getInt("lucky.swap.active.times");
            int swapInterval = getInt("lucky.swap.active.interval");
            for (int i = 0; i < times; i++) {
                activeBtn(jButton);
                try {
                    Thread.sleep(Long.valueOf(swapInterval));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                resetBtn(jButton);
                try {
                    Thread.sleep(Long.valueOf(swapInterval));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } finally {
            activeBtn(jButton);
            bingo = jButton;
            JOptionPane.showMessageDialog(this, jButton.getText());
            stopBingo();
        }

    }

    public static void main(String[] args) throws IOException {
        LuckyTable testLunky = new LuckyTable();
        testLunky.init();
    }

}
