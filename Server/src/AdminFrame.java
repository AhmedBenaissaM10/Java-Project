import ClassesRemote.Question;
import ClassesRemote.User;
import ClassesRemote.GameRemote;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class AdminFrame extends JFrame {

    // --- Colors ---
    static Color darkBg     = new Color(28, 30, 45);
    static Color goldColor  = new Color(255, 200, 50);
    static Color rowEven    = new Color(40, 43, 60);
    static Color rowOdd     = new Color(50, 54, 75);
    static Color headerBg   = new Color(20, 22, 35);
    static Color textColor  = new Color(220, 220, 240);
    static Color btnColor   = new Color(50, 54, 75);
    static String emojiFont = "Segoe UI Emoji";

    private static DefaultTableModel usersModel;
    private static DefaultTableModel connectedModel;
    private static DefaultTableModel questModel;
    private static JLabel allUsersCount;
    private static JLabel connectedCount;

    private final GameRemote Game;
    private boolean showingAllUsers = true;
    private JScrollPane usersScrollPane;
    private JScrollPane connectedScrollPane;

    public AdminFrame(GameRemote Game) {
        this.Game = Game;
        this.setTitle("Admin Panel");
        this.setSize(900, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        getContentPane().setBackground(darkBg);

        // --- Title ---
        JLabel titleLabel = new JLabel("⚙️  Admin Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font(emojiFont, Font.BOLD, 22));
        titleLabel.setForeground(goldColor);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(darkBg);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        this.add(titleLabel, BorderLayout.NORTH);

        // --- Tabs ---
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(darkBg);
        tabs.setForeground(textColor);
        tabs.setFont(new Font(emojiFont, Font.BOLD, 14));
        tabs.addTab("👥  Users", buildUsersTab());
        tabs.addTab("❓  Questions", buildQuestionsTab());
        tabs.setOpaque(true);

        this.add(tabs, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        startAutoRefresh();
    }

    // ===================== AUTO REFRESH =====================
    void startAutoRefresh() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> {
                        loadAllUsers();
                        loadQuestions();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).setDaemon(true); // daemon so it stops when the frame closes
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(2000);
                    SwingUtilities.invokeLater(() -> {
                        loadAllUsers();
                        loadQuestions();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true); // stops automatically when app closes
        t.start();
    }

    // ===================== USERS TAB =====================
    JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);

        // --- Top bar ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(headerBg);

        allUsersCount  = new JLabel();
        connectedCount = new JLabel();
        allUsersCount.setFont(new Font(emojiFont, Font.BOLD, 13));
        connectedCount.setFont(new Font(emojiFont, Font.BOLD, 13));
        allUsersCount.setForeground(textColor);
        connectedCount.setForeground(new Color(100, 200, 100));

        JButton toggleBtn = createBtn("🟢  Show Connected", btnColor);

        topBar.add(allUsersCount);
        topBar.add(new JSeparator(SwingConstants.VERTICAL));
        topBar.add(connectedCount);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(toggleBtn);
        panel.add(topBar, BorderLayout.NORTH);

        // --- All users table ---
        String[] userCols = {"#", "ID", "Username", "Best Score", "Best Time (s)"};
        usersModel = new DefaultTableModel(userCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable allUsersTable = buildStyledTable(usersModel);
        usersScrollPane = new JScrollPane(allUsersTable);
        styleScrollPane(usersScrollPane);

        // --- Connected users table ---
        String[] connCols = {"#", "Username"};
        connectedModel = new DefaultTableModel(connCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable connectedTable = buildStyledTable(connectedModel);
        connectedScrollPane = new JScrollPane(connectedTable);
        styleScrollPane(connectedScrollPane);

        // --- CardLayout ---
        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(darkBg);
        cardPanel.add(usersScrollPane, "all");
        cardPanel.add(connectedScrollPane, "connected");
        panel.add(cardPanel, BorderLayout.CENTER);

        // --- Toggle listener ---
        toggleBtn.addActionListener(e -> {
            showingAllUsers = !showingAllUsers;
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            if (showingAllUsers) {
                cl.show(cardPanel, "all");
                toggleBtn.setText("🟢  Show Connected");
            } else {
                cl.show(cardPanel, "connected");
                toggleBtn.setText("👥  Show All Users");
            }
        });

        // load initial data
        loadAllUsers();
        try {
            refreshConnected(Game.getConnectedUsers());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return panel;
    }

    void loadAllUsers() {
        try {
            ArrayList<User> users = Game.getAllUsers();
            usersModel.setRowCount(0);
            int i = 1;
            for (User u : users) {
                String bestScore = "—";
                String bestTime  = "—";
                if (u.getBest_game_id() != 0) {
                    ClassesRemote.Game g = Game.getGame(u.getBest_game_id());
                    if (g != null) {
                        bestScore = String.valueOf(g.getScore());
                        bestTime  = String.valueOf(g.getTimeSpent());
                    }
                }
                usersModel.addRow(new Object[]{i++, u.getUser_id(), u.getUsername(), bestScore, bestTime});
            }
            allUsersCount.setText("👥  All Users : " + users.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void refreshConnected(ArrayList<String> connected) {
        if (connectedModel == null) return;
        connectedModel.setRowCount(0);
        int i = 1;
        for (String username : connected) {
            connectedModel.addRow(new Object[]{i++, username});
        }
        if (connectedCount != null)
            connectedCount.setText("🟢  Connected : " + connected.size());
    }

    // ===================== QUESTIONS TAB =====================
    JPanel buildQuestionsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(darkBg);

        // --- Table ---
        String[] cols = {"ID", "Country", "Option 1", "Option 2", "Option 3"};
        questModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildStyledTable(questModel);
        JScrollPane scrollPane = new JScrollPane(table);
        styleScrollPane(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom buttons ---
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        bottomBar.setBackground(headerBg);

        JButton addBtn    = createBtn("➕  Add",    new Color(60, 130, 80));
        JButton editBtn   = createBtn("✏️  Edit",   new Color(80, 90, 130));
        JButton deleteBtn = createBtn("🗑️  Delete", new Color(140, 50, 50));

        bottomBar.add(addBtn);
        bottomBar.add(editBtn);
        bottomBar.add(deleteBtn);
        panel.add(bottomBar, BorderLayout.SOUTH);

        loadQuestions();

        // --- Add ---
        addBtn.addActionListener(e -> {
            String[] fields = showQuestionDialog("Add Question", "", "", "", "");
            if (fields != null) {
                try {
                    Game.AddQuestion(fields[0], fields[1], fields[2], fields[3]);
                    loadQuestions();
                    ServerFrame.log("➕ Question added: " + fields[0]);
                } catch (RemoteException ex) { ex.printStackTrace(); }
            }
        });

        // --- Edit ---
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showWarning("Please select a question to edit."); return; }
            int id         = (int) questModel.getValueAt(row, 0);
            String country = (String) questModel.getValueAt(row, 1);
            String opt1    = (String) questModel.getValueAt(row, 2);
            String opt2    = (String) questModel.getValueAt(row, 3);
            String opt3    = (String) questModel.getValueAt(row, 4);
            String[] fields = showQuestionDialog("Edit Question", country, opt1, opt2, opt3);
            if (fields != null) {
                try {
                    Game.updateQuestion(id, fields[0], fields[1], fields[2], fields[3]);
                    loadQuestions();
                    ServerFrame.log("✏️ Question updated: ID " + id);
                } catch (RemoteException ex) { ex.printStackTrace(); }
            }
        });

        // --- Delete ---
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showWarning("Please select a question to delete."); return; }
            int id = (int) questModel.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete question ID " + id + "?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Game.deleteQuestion(id);
                    loadQuestions();
                    ServerFrame.log("🗑️ Question deleted: ID " + id);
                } catch (RemoteException ex) { ex.printStackTrace(); }
            }
        });

        return panel;
    }

    void loadQuestions() {
        try {
            ArrayList<Question> questions = Game.getQuestions();
            questModel.setRowCount(0);
            for (Question q : questions) {
                questModel.addRow(new Object[]{
                        q.getId(),
                        q.getAnswer(),
                        q.getOption1(),
                        q.getOption2(),
                        q.getOption3()
                });
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // --- Question dialog ---
    String[] showQuestionDialog(String title, String country, String opt1, String opt2, String opt3) {
        JTextField countryField = styledField(country);
        JTextField opt1Field    = styledField(opt1);
        JTextField opt2Field    = styledField(opt2);
        JTextField opt3Field    = styledField(opt3);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBackground(darkBg);
        form.add(styledLabel("Country :"));  form.add(countryField);
        form.add(styledLabel("Option 1 :")); form.add(opt1Field);
        form.add(styledLabel("Option 2 :")); form.add(opt2Field);
        form.add(styledLabel("Option 3 :")); form.add(opt3Field);

        int result = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            return new String[]{
                    countryField.getText().trim(),
                    opt1Field.getText().trim(),
                    opt2Field.getText().trim(),
                    opt3Field.getText().trim()
            };
        }
        return null;
    }

    // ===================== HELPERS =====================
    JTable buildStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? rowEven : rowOdd);
                    c.setForeground(textColor);
                }
                return c;
            }
        };
        table.setFont(new Font(emojiFont, Font.PLAIN, 13));
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(rowEven);
        table.setSelectionBackground(new Color(80, 90, 130));
        table.setSelectionForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font(emojiFont, Font.BOLD, 13));
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(goldColor);
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        return table;
    }

    void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        sp.getViewport().setBackground(darkBg);
        sp.setBackground(darkBg);
    }

    JButton createBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font(emojiFont, Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bg.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(bg); }
        });
        return btn;
    }

    JTextField styledField(String value) {
        JTextField f = new JTextField(value);
        f.setBackground(new Color(50, 54, 75));
        f.setForeground(textColor);
        f.setCaretColor(textColor);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 90, 130), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(textColor);
        l.setFont(new Font(emojiFont, Font.BOLD, 13));
        return l;
    }

    void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}