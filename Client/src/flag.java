import Classes.User;
import Database.GameImplementation;
import Database.UserImplementation;
import Database.databaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.*;
import java.util.List;

public class flag extends JFrame {
    User you;
    ImageIcon image;
    JLabel label, scorelb, timerlb,roundlb;
    int score = 0, timeLeft = 30, timeSpent = 0;
    String currentCountry = "";
    javax.swing.Timer countdownTimer;
    GameImplementation game;
    JButton startBtn, option1, option2, option3, option4;
    String[] countries = {"Algeria", "Argentina", "Brazil", "Egypt", "France", "Germany", "Morocco", "Spain", "Tunisia"};
    int index = 0;

    public flag(User player) {
        this.setTitle("Flag");
        this.setSize(550, 550);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        Connection con = databaseConnection.makeConnection();
        game = new GameImplementation(con);
        this.you = player;
        // --- Top bar: Score + Timer ---
        roundlb = new JLabel("1 / "+countries.length, SwingConstants.CENTER);
        scorelb = new JLabel("Score : "+score, SwingConstants.CENTER);
        timerlb = new JLabel("Time : " + timeLeft, SwingConstants.CENTER);
        roundlb.setFont(new Font("Arial", Font.BOLD, 12));
        scorelb.setFont(new Font("Arial", Font.BOLD, 12));
        timerlb.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(roundlb);
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
                timeSpent++;
                timerlb.setText("Time : " + timeLeft);
                if (timeLeft <= 0) {
                    countdownTimer.stop();
                    for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
                        if (btn.getText().equals(currentCountry)) {
                            btn.setBackground(Color.GREEN);
                        }
                    }
                    new javax.swing.Timer(1000, e1 -> {
                        ((javax.swing.Timer)e1.getSource()).stop();
                        changeFlag();
                    }).start();
                }
            }
        });
        setOptionsEnabled(false);
        changeFlag();
    }

    void startGame() {
        score = 0;
        index = 0;
        timeSpent = 0;
        restartTimer();
        scorelb.setText("Score : "+score);
        roundlb.setText((index+1)+" / "+countries.length);
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
        roundlb.setText((index+1)+" / "+countries.length);
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

        scorelb.setText("Score : "+score);

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
        game.AddGame(you.getUser_id(),score,20);
        if (score == countries.length) message = "Perfect";
        else if (score > countries.length / 2) message = "Good job";
        else message = "Try again";
        JOptionPane.showMessageDialog(this,
                message+ " "+ you.getUsername()+"! Your score: " + score + " out of " + countries.length+" at " + timeSpent + " seconds",
                "End of Classes.Game", JOptionPane.INFORMATION_MESSAGE);

    }

    void setOptionsEnabled(boolean enabled) {
        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setEnabled(enabled);
        }
    }

    public static void main(String[] args) {
        flag dsh = new flag(new User(1,"Ahmed",0));
        dsh.setVisible(true);
    }
}