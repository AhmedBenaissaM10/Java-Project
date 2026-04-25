import Classes.User;
import Database.GameImplementation;
import Database.UserImplementation;
import Database.databaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MainMenu extends JFrame {

    public MainMenu(User player, UserImplementation userDAO, GameImplementation gameDAO) {
        this.setTitle("Flag Game");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // --- Colors ---
        Color darkBg    = new Color(28, 30, 45);
        Color goldColor = new Color(255, 200, 50);
        Color btnColor  = new Color(50, 54, 75);
        Color textColor = new Color(220, 220, 240);

        getContentPane().setBackground(darkBg);

        // --- Welcome label ---
        JLabel welcomeLabel = new JLabel("Welcome, " + player.getUsername() + " 👋", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
        welcomeLabel.setForeground(goldColor);
        welcomeLabel.setOpaque(true);
        welcomeLabel.setBackground(darkBg);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        this.add(welcomeLabel, BorderLayout.NORTH);

        // --- Buttons panel ---
        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.setBackground(darkBg);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 80, 10, 80));

        JButton playBtn    = createMenuButton("▶   Play",          btnColor, textColor);
        JButton historyBtn = createMenuButton("📜   My History",   btnColor, textColor);
        JButton leaderBtn  = createMenuButton("🏆   LeaderBoard",  btnColor, textColor);

        playBtn.addActionListener(e -> {
            this.dispose();
            new flag(player, userDAO, gameDAO).setVisible(true);
        });
        historyBtn.addActionListener(e -> {
            this.dispose();
            new history(player, userDAO, gameDAO).setVisible(true);
        });
        leaderBtn.addActionListener(e -> {
            this.dispose();
            new leaderBoard(player, userDAO, gameDAO).setVisible(true);
        });

        btnPanel.add(playBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(historyBtn);
        btnPanel.add(Box.createVerticalStrut(20));
        btnPanel.add(leaderBtn);

        // --- Wrapper to prevent stretching ---
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(darkBg);
        wrapperPanel.add(btnPanel);

        this.add(wrapperPanel, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    JButton createMenuButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        btn.setPreferredSize(new Dimension(400, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(80, 90, 130));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    public static void main(String[] args) {
        Connection conn = databaseConnection.makeConnection();
        UserImplementation userDAO = new UserImplementation(conn);
        GameImplementation gameDAO = new GameImplementation(conn);
        User user = userDAO.getUser(1);
        SwingUtilities.invokeLater(() -> new MainMenu(user, userDAO, gameDAO));
    }
}