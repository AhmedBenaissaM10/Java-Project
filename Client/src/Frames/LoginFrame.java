package Frames;

import ClassesRemote.GameRemote;
import ClassesRemote.User;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


public class LoginFrame extends JFrame {
    public LoginFrame( GameRemote quizz) {
        this.setTitle("Flag Game - Login");
        this.setSize(450, 320);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // --- Colors ---
        Color darkBg    = new Color(28, 30, 45);
        Color goldColor = new Color(255, 200, 50);
        Color textColor = new Color(220, 220, 240);

        getContentPane().setBackground(darkBg);

        // --- Title ---
        JLabel titleLabel = new JLabel("🚩 Flag Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        titleLabel.setForeground(goldColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(darkBg);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // --- Center panel ---
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(darkBg);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(darkBg);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setPreferredSize(new Dimension(380, 180));

        // --- Username label ---
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        usernameLabel.setForeground(textColor);
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Username field wrapper ---
        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setBackground(darkBg);
        fieldWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        fieldWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField usernameField = getJTextField(textColor);
        fieldWrapper.add(usernameField, BorderLayout.CENTER);

        // --- Error label ---
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(220, 80, 80));
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Login button ---
        JButton loginBtn = new JButton("Enter");
        loginBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        loginBtn.setBackground(new Color(60, 130, 80));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginBtn.setPreferredSize(new Dimension(0, 50));
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(new Color(80, 160, 100));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginBtn.setBackground(new Color(60, 130, 80));
            }
        });

        // --- Login action ---
        Runnable doLogin = () -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                errorLabel.setText("Please enter a username.");
                return;
            }
            try {
                User user = quizz.getUserByUsername(username);
                if (quizz.getConnectedUsers().contains(username)){
                    errorLabel.setText("This user is already connected.");
                    return;
                }
                if (user == null) {
                    quizz.AddUser(username);
                    user = quizz.getUserByUsername(username);
                    if (user == null) {
                        errorLabel.setText("Something went wrong. Try again.");
                        return;
                    }
                    quizz.notifyServer("👤 New player registered: " + username + " | ✅ " + username + " connected");
                } else {
                    quizz.notifyServer("✅ " + username + " connected");
                }
                User finalUser = user;
                this.dispose();
                SwingUtilities.invokeLater(() -> new MainMenu(finalUser, quizz).setVisible(true));
            } catch (RemoteException e) {
                errorLabel.setText("Server unreachable. Try again.");
                e.printStackTrace();
            }
        };


        loginBtn.addActionListener(e -> doLogin.run());
        usernameField.addActionListener(e -> doLogin.run());

        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(fieldWrapper);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(errorLabel);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(loginBtn);

        centerPanel.add(formPanel);
        this.add(centerPanel, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private static JTextField getJTextField(Color textColor) {
        JTextField usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        usernameField.setBackground(new Color(50, 54, 75));
        usernameField.setForeground(textColor);
        usernameField.setCaretColor(textColor);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 90, 130), 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        usernameField.setPreferredSize(new Dimension(0, 50));
        return usernameField;
    }

    public static void main(String[] args) {
        try {
            String url = "rmi://localhost:9005/Game";
            GameRemote quizz = (GameRemote) Naming.lookup(url);
            SwingUtilities.invokeLater(() -> new LoginFrame(quizz));
        } catch (NotBoundException e) {
            JOptionPane.showMessageDialog(null, "Server not found. Make sure the server is running.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (MalformedURLException e) {
            JOptionPane.showMessageDialog(null, "Invalid server URL.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(null, "Cannot reach the server. Check your connection.", "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}