package com.uvarubber.view;

import com.uvarubber.dao.CollectionDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PaymentFrame extends JFrame {
    private JTextField txtStartDate, txtEndDate, txtGlobalRate;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private CollectionDAO collectionDAO = new CollectionDAO();
    private final Color ENV_GREEN = new Color(34, 139, 34);

    public PaymentFrame() {
        setTitle("Uva Rubber - Payment Summary");
        setSize(1000, 600);
        setLayout(new BorderLayout(10, 10));

        // --- TOP: Filters ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createTitledBorder("Cycle Details"));

        topPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField(10);
        topPanel.add(txtStartDate);

        topPanel.add(new JLabel("End Date:"));
        txtEndDate = new JTextField(10);
        topPanel.add(txtEndDate);

        topPanel.add(new JLabel("Standard Rate:"));
        txtGlobalRate = new JTextField("575", 5); // Default rate
        topPanel.add(txtGlobalRate);

        JButton btnCalculate = new JButton("Generate Summary");
        btnCalculate.setBackground(ENV_GREEN);
        btnCalculate.setForeground(Color.WHITE);
        btnCalculate.addActionListener(e -> generateReport());
        topPanel.add(btnCalculate);

        add(topPanel, BorderLayout.NORTH);

        // --- CENTER: Summary Table ---
        String[] columns = {"Farmer Name", "Bank", "Account No", "Total Dry KG", "Rate (Editable)", "Total Payout"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the RATE column is editable for special cases
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(25);

        // Listener to recalculate total if manager changes a specific rate
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 4) { // If Rate is changed
                int row = e.getFirstRow();
                recalculateRow(row);
            }
        });

        add(new JScrollPane(paymentTable), BorderLayout.CENTER);

        // --- BOTTOM: Actions ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExport = new JButton("Export to Bank List");
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exporting People's Bank Credit List..."));
        bottomPanel.add(btnExport);
        add(bottomPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private void generateReport() {
        tableModel.setRowCount(0);
        String start = txtStartDate.getText();
        String end = txtEndDate.getText();
        double globalRate = Double.parseDouble(txtGlobalRate.getText());

        List<Object[]> data = collectionDAO.getPaymentSummary(start, end);
        for (Object[] row : data) {
            double kg = (double) row[3];
            double total = kg * globalRate;

            // Add row: Name, Bank, Acc, KG, Rate, Total
            tableModel.addRow(new Object[]{row[0], row[1], row[2], kg, globalRate, String.format("%.2f", total)});
        }
    }

    private void recalculateRow(int row) {
        try {
            double kg = (double) tableModel.getValueAt(row, 3);
            double newRate = Double.parseDouble(tableModel.getValueAt(row, 4).toString());
            double newTotal = kg * newRate;
            tableModel.setValueAt(String.format("%.2f", newTotal), row, 5);
        } catch (Exception e) {
            // If user enters invalid rate
        }
    }
}