import Database.GameImplementation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class flag extends JFrame {
    ImageIcon image;
    JLabel label, scorelb, timerlb;
    int score = 0;
    int timeLeft = 30;
    String currentCountry = "";
    javax.swing.Timer countdownTimer;
    GameImplementation game;
    JButton startBtn, option1, option2, option3, option4;
    String[] countries = {"Algeria", "Argentina", "Brazil", "Egypt", "France", "Germany", "Morocco", "Spain", "Tunisia"};
    int index = 0;

    public flag() {
        this.setTitle("Flag");
        this.setSize(550, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        game = new GameImplementation();
        // --- Top bar: Score + Timer ---
        scorelb = new JLabel(score + " out of " + countries.length, SwingConstants.CENTER);
        timerlb = new JLabel("Time : " + timeLeft, SwingConstants.CENTER);
        scorelb.setFont(new Font("Arial", Font.BOLD, 16));
        timerlb.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel topPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(scorelb);
        topPanel.add(timerlb);
        timerlb.setText("Time : " + timeLeft);
        // --- Flag ---
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flagPanel.add(label);

        // --- Score + Flag stacked ---
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(topPanel);
        topSection.add(flagPanel);

        // --- Options ---
        option1 = new JButton();
        option2 = new JButton();
        option3 = new JButton();
        option4 = new JButton();

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            optionsPanel.add(btn);
            btn.addActionListener(e -> checkAnswer(btn));
            optionsPanel.add(Box.createVerticalStrut(8));
        }

        // --- Center: topSection + options ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(topSection);
        centerPanel.add(optionsPanel);

        // --- Start button ---
        startBtn = new JButton("Start");
        startBtn.addActionListener(e -> startGame());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(startBtn);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // --- Countdown Timer ---
        countdownTimer = new javax.swing.Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeft--;
                timerlb.setText("Time : " + timeLeft);
                if (timeLeft <= 0) {
                    countdownTimer.stop();
                    changeFlag();
                }
            }
        });
        setOptionsEnabled(false);
        changeFlag();
    }

    void startGame() {
        score = 0;
        index = 0;
        restartTimer();
        scorelb.setText(score + " out of " + countries.length);
        changeFlag();
        setOptionsEnabled(true);
        startBtn.setVisible(false);
    }
    void restartTimer(){
        timeLeft = 30;
        timerlb.setText("Time : " + timeLeft);
        countdownTimer.restart();
    }
    void changeFlag() {
        if (index == countries.length) {
            endGame();
            return;}

        if (index != 0) restartTimer();
        currentCountry = countries[index++];
        ImageIcon originalIcon = new ImageIcon("src/flags/" + currentCountry + ".png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(275, 180, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaledImage);
        label.setIcon(image);

        // Shuffle 3 wrong options + 1 correct
        List<String> opts = new java.util.ArrayList<>();
        opts.add(currentCountry);
        List<String> pool = new ArrayList<>(Arrays.asList(countries));
        pool.remove(currentCountry);
        java.util.Collections.shuffle(pool);
        opts.add(pool.get(0));
        opts.add(pool.get(1));
        opts.add(pool.get(2));
        Collections.shuffle(opts);
        option1.setText(opts.get(0));
        option2.setText(opts.get(1));
        option3.setText(opts.get(2));
        option4.setText(opts.get(3));

        // Reset button colors
        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setBackground(null);
        }
    }

    void checkAnswer(JButton chosen) {
        if (chosen.getText().equals(currentCountry)) {
            chosen.setBackground(Color.GREEN);
            score++;
        } else {
            chosen.setBackground(Color.RED);
            for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
                if (btn.getText().equals(currentCountry)) {
                    btn.setBackground(Color.GREEN);
                }
            }
        }

        scorelb.setText(score + " out of " + countries.length);

        countdownTimer.stop();

        new javax.swing.Timer(1000, e -> {
            ((javax.swing.Timer)e.getSource()).stop();
            changeFlag();
        }).start();
    }

    void endGame() {
        setOptionsEnabled(false);
        startBtn.setVisible(true);
        countdownTimer.stop();
        String message;
        game.AddGame(1,score,20);
        if (score == countries.length) message = "Perfect!";
        else if (score > countries.length / 2) message = "Good job!";
        else message = "Try again!";
        JOptionPane.showMessageDialog(this,
                message+"! Your score: " + score + " out of " + countries.length,
                "End of Game", JOptionPane.INFORMATION_MESSAGE);

    }

    void setOptionsEnabled(boolean enabled) {
        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setEnabled(enabled);
        }
    }

    public static void main(String[] args) {
        flag dsh = new flag();
        dsh.setVisible(true);
    }
}