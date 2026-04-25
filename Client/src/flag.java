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
    JLabel label, scorelb, timerlb, roundlb;
    int score = 0, timeLeft = 30, timeSpent = 0;
    String currentCountry = "";
    javax.swing.Timer countdownTimer;
    GameImplementation game;
    JButton startBtn, option1, option2, option3, option4;
    String[] countries = {"Algeria", "Argentina", "Brazil", "Egypt", "France", "Germany", "Morocco", "Spain", "Tunisia"};
    int index = 0;

    public flag(User player, UserImplementation userDAO, GameImplementation gameDAO) {
        this.setTitle("Flag");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                new MainMenu(player, userDAO, gameDAO).setVisible(true);
            }
        });
        this.setLayout(new BorderLayout());
        this.you = player;
        this.game = gameDAO;

        // --- Colors ---
        Color darkBg    = new Color(28, 30, 45);
        Color goldColor = new Color(255, 200, 50);
        Color btnColor  = new Color(50, 54, 75);
        Color textColor = new Color(220, 220, 240);
        Color headerBg  = new Color(20, 22, 35);

        getContentPane().setBackground(darkBg);

        // --- Top bar: Round + Score + Timer ---
        roundlb = new JLabel("1 / " + countries.length, SwingConstants.CENTER);
        scorelb = new JLabel("Score : " + score, SwingConstants.CENTER);
        timerlb = new JLabel("Time : " + timeLeft, SwingConstants.CENTER);

        for (JLabel lb : new JLabel[]{roundlb, scorelb, timerlb}) {
            lb.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
            lb.setForeground(textColor);
            lb.setOpaque(true);
            lb.setBackground(headerBg);
        }
        timerlb.setForeground(goldColor);

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 0, 0));
        topPanel.setBackground(headerBg);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.add(roundlb);
        topPanel.add(scorelb);
        topPanel.add(timerlb);

        // --- Flag ---
        label = new JLabel();
        label.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel flagPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        flagPanel.setBackground(darkBg);
        flagPanel.add(label);

        // --- Top section ---
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setBackground(darkBg);
        topSection.add(topPanel);
        topSection.add(flagPanel);

        // --- Options ---
        option1 = new JButton();
        option2 = new JButton();
        option3 = new JButton();
        option4 = new JButton();

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(darkBg);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setBackground(btnColor);
            btn.setForeground(textColor);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                Color originalColor = btnColor;
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (btn.getBackground().equals(btnColor))
                        btn.setBackground(new Color(80, 90, 130));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (btn.getBackground().equals(new Color(80, 90, 130)))
                        btn.setBackground(btnColor);
                }
            });
            optionsPanel.add(btn);
            btn.addActionListener(e -> checkAnswer(btn));
            optionsPanel.add(Box.createVerticalStrut(8));
        }

        // --- Center panel ---
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(darkBg);
        centerPanel.add(topSection);
        centerPanel.add(optionsPanel);

        // --- Start button ---
        startBtn = new JButton("▶  Start");
        startBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 15));
        startBtn.setBackground(new Color(60, 130, 80));
        startBtn.setForeground(Color.WHITE);
        startBtn.setFocusPainted(false);
        startBtn.setBorderPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startBtn.setPreferredSize(new Dimension(140, 40));
        startBtn.addActionListener(e -> startGame());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(darkBg);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
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
                // Turn timer red when <= 10
                timerlb.setForeground(timeLeft <= 10 ? Color.RED : goldColor);
                if (timeLeft <= 0) {
                    countdownTimer.stop();
                    for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
                        if (btn.getText().equals(currentCountry)) {
                            btn.setBackground(new Color(60, 130, 80));
                        }
                    }
                    new javax.swing.Timer(1000, e1 -> {
                        ((javax.swing.Timer) e1.getSource()).stop();
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
        scorelb.setText("Score : " + score);
        roundlb.setText((index + 1) + " / " + countries.length);
        changeFlag();
        setOptionsEnabled(true);
        startBtn.setVisible(false);
    }

    void restartTimer() {
        timeLeft = 30;
        timerlb.setText("Time : " + timeLeft);
        timerlb.setForeground(new Color(255, 200, 50));
        countdownTimer.restart();
    }

    void changeFlag() {
        if (index == countries.length) {
            endGame();
            return;
        }
        if (index != 0) restartTimer();
        roundlb.setText((index + 1) + " / " + countries.length);
        currentCountry = countries[index++];
        ImageIcon originalIcon = new ImageIcon("src/flags/" + currentCountry + ".png");
        Image scaledImage = originalIcon.getImage().getScaledInstance(275, 180, Image.SCALE_SMOOTH);
        image = new ImageIcon(scaledImage);
        label.setIcon(image);

        List<String> opts = new ArrayList<>();
        opts.add(currentCountry);
        List<String> pool = new ArrayList<>(Arrays.asList(countries));
        pool.remove(currentCountry);
        Collections.shuffle(pool);
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
            btn.setBackground(new Color(50, 54, 75));
        }
    }

    void checkAnswer(JButton chosen) {
        if (chosen.getText().equals(currentCountry)) {
            chosen.setBackground(new Color(60, 130, 80)); // dark green
            score++;
        } else {
            chosen.setBackground(new Color(160, 50, 50)); // dark red
            for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
                if (btn.getText().equals(currentCountry)) {
                    btn.setBackground(new Color(60, 130, 80)); // show correct
                }
            }
        }
        scorelb.setText("Score : " + score);
        countdownTimer.stop();
        new javax.swing.Timer(1000, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            changeFlag();
        }).start();
    }

    void endGame() {
        setOptionsEnabled(false);
        startBtn.setVisible(true);
        countdownTimer.stop();
        game.AddGame(you.getUser_id(), score, timeSpent);
        String message;
        if (score == countries.length)       message = "🏆 Perfect!";
        else if (score > countries.length / 2) message = "👍 Good job!";
        else                                   message = "😅 Try again!";

        JOptionPane.showMessageDialog(this,
                message + " " + you.getUsername() + "\nScore : " + score + " / " + countries.length + "\nTime Spent : " + timeSpent + " seconds",
                "End of Game", JOptionPane.INFORMATION_MESSAGE);
    }

    void setOptionsEnabled(boolean enabled) {
        for (JButton btn : new JButton[]{option1, option2, option3, option4}) {
            btn.setEnabled(enabled);
        }
    }

    public static void main(String[] args) {
        Connection conn = databaseConnection.makeConnection();
        UserImplementation userDAO = new UserImplementation(conn);
        GameImplementation gameDAO = new GameImplementation(conn);
        User user = userDAO.getUser(1);
        flag dsh = new flag(user, userDAO, gameDAO);
        dsh.setVisible(true);
    }
}