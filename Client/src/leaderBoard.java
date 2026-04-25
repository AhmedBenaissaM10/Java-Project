import Classes.Game;
import Classes.LeaderEntry;
import Classes.User;
import Database.GameImplementation;
import Database.UserImplementation;
import Database.databaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

public class leaderBoard extends JFrame {

    public leaderBoard(User player, UserImplementation userDAO, GameImplementation gameDAO) {
        this.setTitle("LeaderBoard");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // go back to main menu when closed
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent e) {
                new MainMenu(player, userDAO, gameDAO).setVisible(true);
            }
        });
        this.setLayout(new BorderLayout());

        // --- Detect emoji-compatible font ---
        String emojiFont;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win"))       emojiFont = "Segoe UI Emoji";
        else if (os.contains("mac"))  emojiFont = "Apple Color Emoji";
        else                          emojiFont = "Noto Color Emoji";

        // --- Background ---
        Color darkBg      = new Color(28, 30, 45);
        Color goldColor   = new Color(255, 200, 50);
        Color silverColor = new Color(180, 180, 180);
        Color bronzeColor = new Color(200, 130, 80);
        Color rowEven     = new Color(40, 43, 60);
        Color rowOdd      = new Color(50, 54, 75);
        Color headerBg    = new Color(20, 22, 35);
        Color textColor   = new Color(220, 220, 240);

        getContentPane().setBackground(darkBg);

        // --- Title ---
        JLabel titleLabel = new JLabel("🏆 LeaderBoard", SwingConstants.CENTER);
        titleLabel.setFont(new Font(emojiFont, Font.BOLD, 24));
        titleLabel.setForeground(goldColor);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(18, 0, 18, 0));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(darkBg);
        this.add(titleLabel, BorderLayout.NORTH);

        // --- Build leaderboard data ---
        ArrayList<User> usersList = userDAO.getAllUsers();
        ArrayList<LeaderEntry> leaderboard = new ArrayList<>();

        for (User user : usersList) {
            if (user.getBest_game_id() != 0) {
                Game bestGame = gameDAO.getGame(user.getBest_game_id());
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
            if (b.score != a.score) {
                return b.score - a.score;
            }
            return a.time - b.time;
        });

        // --- Table model ---
        String[] columns = {"Rank", "Username", "Best Score", "Time Spent (s)"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        int rank = 1;
        for (LeaderEntry entry : leaderboard) {
            String medal = rank == 1 ? "🥇 1" : rank == 2 ? "🥈 2" : rank == 3 ? "🥉 3" : String.valueOf(rank);
            tableModel.addRow(new Object[]{medal, entry.username, entry.score, entry.time});
            rank++;
        }

        // --- Table ---
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                // Alternate row colors
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? rowEven : rowOdd);
                    c.setForeground(textColor);
                }
                // Gold / Silver / Bronze for top 3
                if (row == 0) c.setForeground(goldColor);
                else if (row == 1) c.setForeground(silverColor);
                else if (row == 2) c.setForeground(bronzeColor);
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

        // --- Header styling ---
        table.getTableHeader().setFont(new Font(emojiFont, Font.BOLD, 14));
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

    public static void main(String[] args) {
        Connection conn = databaseConnection.makeConnection();
        UserImplementation userDAO = new UserImplementation(conn);
        GameImplementation gameDAO = new GameImplementation(conn);
        User user = userDAO.getUser(1);
        SwingUtilities.invokeLater(() -> new leaderBoard(user,userDAO, gameDAO));
    }
}