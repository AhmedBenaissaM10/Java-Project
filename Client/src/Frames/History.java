package Frames;

import ClassesRemote.Game;
import ClassesRemote.GameRemote;
import ClassesRemote.User;

import javax.swing.table.DefaultTableCellRenderer;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class History extends JFrame {
    User player;
    public History(User player, GameRemote game) throws RemoteException {
        this.player = player;
        this.setTitle("Game History");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                new MainMenu(player, game).setVisible(true);
            }
        });
        this.setLayout(new BorderLayout());

        // --- Colors ---
        Color darkBg      = new Color(28, 30, 45);
        Color goldColor   = new Color(255, 200, 50);
        Color rowEven     = new Color(40, 43, 60);
        Color rowOdd      = new Color(50, 54, 75);
        Color headerBg    = new Color(20, 22, 35);
        Color textColor   = new Color(220, 220, 240);

        getContentPane().setBackground(darkBg);

        // --- Fetch data ---
        ArrayList<Game> list = game.showHistorty(player.getUser_id());

        // --- Top panel ---
        JLabel userLabel = new JLabel("Player : " + player.getUsername(), SwingConstants.CENTER);
        userLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        userLabel.setForeground(goldColor);
        userLabel.setOpaque(true);
        userLabel.setBackground(darkBg);
        userLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JLabel gamesLabel = new JLabel("Games : " + list.size());
        gamesLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        gamesLabel.setForeground(textColor);
        gamesLabel.setOpaque(true);
        gamesLabel.setBackground(darkBg);
        gamesLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 0));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.setBackground(darkBg);
        topPanel.add(userLabel);
        topPanel.add(gamesLabel);
        this.add(topPanel, BorderLayout.NORTH);

        // --- Table model ---
        String[] columns = {"#", "Score", "Time Spent (s)", "Played At"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        int round = 1;
        for (Game g : list) {
            tableModel.addRow(new Object[]{
                    round++,
                    g.getScore(),
                    g.getTimeSpent(),
                    formatPlayedAt(g.getPlayedAt())
            });
        }

        // --- Table ---
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? rowEven : rowOdd);
                    c.setForeground(textColor);
                }
                return c;
            }
        };

        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(rowEven);
        table.setSelectionBackground(new Color(80, 90, 130));
        table.setSelectionForeground(Color.WHITE);

        // --- Header ---
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(goldColor);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

        // --- Center all cells ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        scrollPane.getViewport().setBackground(darkBg);
        scrollPane.setBackground(darkBg);
        this.add(scrollPane, BorderLayout.CENTER);

        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    String formatPlayedAt(Timestamp playedAt) {
        if (playedAt == null) return "N/A";
        LocalDateTime played = playedAt.toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long minutes = Duration.between(played, now).toMinutes();
        long hours   = Duration.between(played, now).toHours();
        long days    = Duration.between(played, now).toDays();

        if (minutes < 60)      return minutes + " minutes ago";
        else if (hours < 24)   return hours + " hours ago";
        else                   return days + " days ago";
    }

}