import ClassesRemote.Question;
import ClassesRemote.QuizzRemote;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.rmi.Naming;
import java.util.ArrayList;

/**
 * Simple Swing admin UI to manage Questions through the RMI service (QuizzRemote).
 * Features:
 *  - Add a new question
 *  - Delete an existing question (by id)
 *  - Modify (update) an existing question
 *  - Refresh / list current questions
 *
 * NOTE: Listing requires a remote method that returns all questions.
 *       If your QuizzRemote does not expose getQuestions(), the table will stay empty
 *       but Add / Delete / Update will still work as long as you provide a valid id.
 */
public class QuestionAdminUI extends JFrame {

    private static final String RMI_URL = "rmi://localhost:9003/quizz";

    private QuizzRemote quizz;

    private final JTextField idField      = new JTextField(6);
    private final JTextField countryField = new JTextField(15);
    private final JTextField opt1Field    = new JTextField(15);
    private final JTextField opt2Field    = new JTextField(15);
    private final JTextField opt3Field    = new JTextField(15);

    private final DefaultTableModel tableModel =
            new DefaultTableModel(new Object[]{"ID", "Answer (Country)", "Option 1", "Option 2", "Option 3"}, 0) {
                @Override public boolean isCellEditable(int row, int column) { return false; }
            };
    private final JTable table = new JTable(tableModel);

    public QuestionAdminUI() {
        super("Quizz - Question Administration");

        connectToServer();
        buildUI();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
    }

    /** Use this constructor when the server is running in the same JVM. */
    public QuestionAdminUI(QuizzRemote quizz) {
        super("Quizz - Question Administration");

        this.quizz = quizz;
        buildUI();

        // Don't kill the server when the admin window is closed.
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);
    }

    /** Public alias so callers (e.g. Main) can trigger a reload. */
    public void refresh() {
        onRefresh();
    }

    private void connectToServer() {
        try {
            quizz = (QuizzRemote) Naming.lookup(RMI_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not connect to RMI server at " + RMI_URL + "\n" + e.getMessage(),
                    "Connection error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void buildUI() {
        // ---- Form panel ---------------------------------------------------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Question"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, g, row++, "ID (for delete / update):", idField);
        addRow(form, g, row++, "Answer (Country):",          countryField);
        addRow(form, g, row++, "Option 1:",                  opt1Field);
        addRow(form, g, row++, "Option 2:",                  opt2Field);
        addRow(form, g, row++, "Option 3:",                  opt3Field);

        // ---- Buttons ------------------------------------------------------
        JButton addBtn     = new JButton("Add");
        JButton updateBtn  = new JButton("Modify");
        JButton deleteBtn  = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn   = new JButton("Clear");

        addBtn.addActionListener(e -> onAdd());
        updateBtn.addActionListener(e -> onUpdate());
        deleteBtn.addActionListener(e -> onDelete());
        refreshBtn.addActionListener(e -> onRefresh());
        clearBtn.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(addBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(refreshBtn);
        buttons.add(clearBtn);

        // ---- Table --------------------------------------------------------
        table.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = table.getSelectedRow();
            if (r >= 0) {
                idField.setText(String.valueOf(tableModel.getValueAt(r, 0)));
                countryField.setText(String.valueOf(tableModel.getValueAt(r, 1)));
                opt1Field.setText(String.valueOf(tableModel.getValueAt(r, 2)));
                opt2Field.setText(String.valueOf(tableModel.getValueAt(r, 3)));
                opt3Field.setText(String.valueOf(tableModel.getValueAt(r, 4)));
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Questions"));

        // ---- Layout -------------------------------------------------------
        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridy = row;
        g.gridx = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        p.add(new JLabel(label), g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(field, g);
    }

    // ------------------------------------------------------------------
    //  Actions
    // ------------------------------------------------------------------
    private void onAdd() {
        if (quizz == null) { showNoServer(); return; }
        if (!validateQuestionFields()) return;

        try {
            int res = quizz.AddQuestion(
                    countryField.getText().trim(),
                    opt1Field.getText().trim(),
                    opt2Field.getText().trim(),
                    opt3Field.getText().trim()
            );
            if (res > 0) {
                info("Question added successfully.");
                clearForm();
                onRefresh();
            } else if (res == -1) {
                warn("This question already exists (duplicate).");
            } else {
                warn("Could not add question.");
            }
        } catch (Exception ex) {
            error("Add failed: " + ex.getMessage());
        }
    }

    private void onUpdate() {
        if (quizz == null) { showNoServer(); return; }
        Integer id = parseId();
        if (id == null) return;
        if (!validateQuestionFields()) return;

        try {
            int res = quizz.updateQuestion(
                    id,
                    countryField.getText().trim(),
                    opt1Field.getText().trim(),
                    opt2Field.getText().trim(),
                    opt3Field.getText().trim()
            );
            if (res > 0) {
                info("Question updated.");
                onRefresh();
            } else {
                warn("No question was updated. Check the ID.");
            }
        } catch (Exception ex) {
            error("Update failed: " + ex.getMessage());
        }
    }

    private void onDelete() {
        if (quizz == null) { showNoServer(); return; }
        Integer id = parseId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete question with id " + id + " ?",
                "Confirm delete",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int res = quizz.deleteQuestion(id);
            if (res > 0) {
                info("Question deleted.");
                clearForm();
                onRefresh();
            } else {
                warn("Question was not deleted (maybe the id does not exist).");
            }
        } catch (Exception ex) {
            error("Delete failed: " + ex.getMessage());
        }
    }

    // ... existing code ...
    private void onRefresh() {
        tableModel.setRowCount(0);
        if (quizz == null) return;

        try {
            ArrayList<Question> list = quizz.getQuestions();
            if (list == null) {
                warn("Server returned no questions (null).");
                return;
            }
            for (Question q : list) {
                tableModel.addRow(new Object[]{
                        q.getId(), q.getAnswer(), q.getOption1(), q.getOption2(), q.getOption3()
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            error("Refresh failed: " + ex.getMessage());
        }
    }
    // ... existing code ...
    // ------------------------------------------------------------------
    //  Helpers
    // ------------------------------------------------------------------
    private Integer parseId() {
        String txt = idField.getText().trim();
        if (txt.isEmpty()) {
            warn("Please provide the question ID.");
            return null;
        }
        try {
            return Integer.parseInt(txt);
        } catch (NumberFormatException ex) {
            warn("ID must be a number.");
            return null;
        }
    }

    private boolean validateQuestionFields() {
        if (countryField.getText().trim().isEmpty()
                || opt1Field.getText().trim().isEmpty()
                || opt2Field.getText().trim().isEmpty()
                || opt3Field.getText().trim().isEmpty()) {
            warn("Please fill the answer and the three options.");
            return false;
        }
        return true;
    }

    private void clearForm() {
        idField.setText("");
        countryField.setText("");
        opt1Field.setText("");
        opt2Field.setText("");
        opt3Field.setText("");
        table.clearSelection();
    }

    private void showNoServer() { error("Not connected to the RMI server."); }
    private void info(String msg)  { JOptionPane.showMessageDialog(this, msg, "Info",    JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String msg)  { JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE); }
    private void error(String msg) { JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }

    // ------------------------------------------------------------------
    //  Entry point
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QuestionAdminUI ui = new QuestionAdminUI();
            ui.setVisible(true);
            ui.onRefresh();
        });
    }
}