import ClassesRemote.GameRemote;

import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerFrame extends JFrame {

    private static JTextArea logArea;
    private final GameRemote Game;

    public ServerFrame(GameRemote Game) {
        this.Game = Game;
        this.setTitle("🖥️ Server Monitor");
        this.setSize(650, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        // --- Colors ---
        Color darkBg    = new Color(28, 30, 45);
        Color goldColor = new Color(255, 200, 50);
        Color textColor = new Color(220, 220, 240);

        getContentPane().setBackground(darkBg);

        // --- Title ---
        JLabel titleLabel = new JLabel("🖥️  Server Monitor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 22));
        titleLabel.setForeground(goldColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(darkBg);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // --- Log area ---
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 22, 35));
        logArea.setForeground(textColor);
        logArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        scrollPane.getViewport().setBackground(new Color(20, 22, 35));
        scrollPane.setBackground(darkBg);
        this.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(darkBg);

        // --- Admin button ---
        JButton adminBtn = new JButton("⚙️  Admin");
        adminBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        adminBtn.setBackground(new Color(80, 90, 130));
        adminBtn.setForeground(Color.WHITE);
        adminBtn.setFocusPainted(false);
        adminBtn.setBorderPainted(false);
        adminBtn.setPreferredSize(new Dimension(120, 35));
        adminBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                adminBtn.setBackground(new Color(100, 110, 160));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                adminBtn.setBackground(new Color(80, 90, 130));
            }
        });
        adminBtn.addActionListener(e -> new AdminFrame(Game)); // opens without closing ServerFrame

        // --- Clear button ---
        JButton clearBtn = new JButton("🗑️  Clear");
        clearBtn.setFont(new Font("Segoe UI Emoji", Font.BOLD, 13));
        clearBtn.setBackground(new Color(140, 50, 50));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setPreferredSize(new Dimension(120, 35));
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                clearBtn.setBackground(new Color(180, 70, 70));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                clearBtn.setBackground(new Color(140, 50, 50));
            }
        });
        clearBtn.addActionListener(e -> logArea.setText(""));

        bottomPanel.add(adminBtn);
        bottomPanel.add(clearBtn);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
        this.setVisible(true);

        log("✅ Server started and ready.");
    }

    public static void log(String message) {
        if (logArea != null) {
            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + time + "]  " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }
}