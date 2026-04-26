package Frames;

import ClassesRemote.Game;
import ClassesRemote.LeaderEntry;
import ClassesRemote.GameRemote;
import ClassesRemote.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class LeaderBoard extends JFrame {

    private final User player;
    private final GameRemote game;
    private final DefaultTableModel tableModel;
    private final JLabel bottomLabel;
    private final String emojiFont;

    // --- Colors ---
    private final Color darkBg      = new Color(28, 30, 45);
    private final Color goldColor   = new Color(255, 200, 50);
    private final Color silverColor = new Color(180, 180, 180);
    private final Color bronzeColor = new Color(200, 130, 80);
    private final Color rowEven     = new Color(40, 43, 60);
    private final Color rowOdd      = new Color(50, 54, 75);
    private final Color headerBg    = new Color(20, 22, 35);
    private final Color textColor   = new Color(220, 220, 240);
    private final Color playerRow   = new Color(60, 80, 120);

    // track player rank to re-render correctly
    private int finalPlayerRank = -1;

    public LeaderBoard(User player, GameRemote game) throws RemoteException {
        this.player = player;
        this.game   = game;

        // --- Emoji font ---
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))       emojiFont = "Segoe UI Emoji";
        else if (os.contains("mac"))  emojiFont = "Apple Color Emoji";
        else                          emojiFont = "Noto Color Emoji";

        this.setTitle("LeaderBoard");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                new MainMenu(player, game).setVisible(true);
            }
        });
        this.setLayout(new BorderLayout());
        getContentPane().setBackground(darkBg);

        // --- Title ---
        JLabel titleLabel = new JLabel("🏆 LeaderBoard", SwingConstants.CENTER);
        titleLabel.setFont(new Font(emojiFont, Font.BOLD, 24));
        titleLabel.setForeground(goldColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(darkBg);
        this.add(titleLabel, BorderLayout.NORTH);

        // --- Table model ---
        String[] columns = {"Rank", "Username", "Best Score", "Time Spent (s)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        // --- Table ---
        JTable table = getJTable();
        table.getTableHeader().setFont(new Font(emojiFont, Font.BOLD, 14));
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(goldColor);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));

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

        // --- Bottom label ---
        bottomLabel = new JLabel("", SwingConstants.CENTER);
        bottomLabel.setFont(new Font(emojiFont, Font.BOLD, 14));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(darkBg);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(bottomLabel);
        this.add(bottomPanel, BorderLayout.SOUTH);

        this.setLocationRelativeTo(null);
        this.setVisible(true);

        // --- Initial load ---
        loadLeaderboard();

        // --- Auto refresh every 2 seconds ---
        Thread t = new Thread(() -> {
            while (isVisible()) {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(this::loadLeaderboard);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private JTable getJTable() {
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) {
                    if (row == finalPlayerRank) {
                        c.setBackground(playerRow);
                        c.setForeground(Color.WHITE);
                    } else {
                        c.setBackground(row % 2 == 0 ? rowEven : rowOdd);
                        c.setForeground(textColor);
                    }
                }
                if (row != finalPlayerRank) {
                    if (row == 0)      c.setForeground(goldColor);
                    else if (row == 1) c.setForeground(silverColor);
                    else if (row == 2) c.setForeground(bronzeColor);
                }
                return c;
            }
        };

        table.setFont(new Font(emojiFont, Font.PLAIN, 14));
        table.setRowHeight(32);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(80, 90, 130));
        table.setSelectionForeground(Color.WHITE);
        table.setBackground(rowEven);
        return table;
    }

    void loadLeaderboard() {
        try {
            ArrayList<User> usersList = game.getAllUsers();
            ArrayList<LeaderEntry> leaderboard = new ArrayList<>();

            for (User user : usersList) {
                if (user.getBest_game_id() != 0) {
                    Game bestGame = game.getGame(user.getBest_game_id());
                    if (bestGame != null) {
                        leaderboard.add(new LeaderEntry(
                                user.getUsername(),
                                bestGame.getScore(),
                                bestGame.getTimeSpent()
                        ));
                    }
                }
            }

            leaderboard.sort((a, b) -> {
                if (b.score != a.score) return b.score - a.score;
                return a.time - b.time;
            });

            // find player rank
            finalPlayerRank = -1;
            for (int i = 0; i < leaderboard.size(); i++) {
                if (leaderboard.get(i).username.equals(player.getUsername())) {
                    finalPlayerRank = i;
                    break;
                }
            }

            // update table
            tableModel.setRowCount(0);
            int rank = 1;
            for (LeaderEntry entry : leaderboard) {
                String medal = rank == 1 ? "🥇 1" : rank == 2 ? "🥈 2" : rank == 3 ? "🥉 3" : String.valueOf(rank);
                tableModel.addRow(new Object[]{medal, entry.username, entry.score, entry.time});
                rank++;
            }

            // update bottom label
            if (player.getBest_game_id() == 0) {
                bottomLabel.setText("🎮  You haven't played yet — jump in and claim your spot on the board!");
                bottomLabel.setForeground(new Color(180, 180, 100));
                bottomLabel.setFont(new Font(emojiFont, Font.ITALIC, 13));
            } else {
                bottomLabel.setText("📍  Your rank : #" + (finalPlayerRank + 1) + " out of " + leaderboard.size());
                bottomLabel.setForeground(new Color(100, 180, 255));
                bottomLabel.setFont(new Font(emojiFont, Font.BOLD, 14));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}