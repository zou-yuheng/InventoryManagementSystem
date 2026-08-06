package zyhinventory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ZYHFilterDialog extends JDialog {
    private String filterType;
    private ZYHDataManager dataManager;
    private JComboBox<String> fieldComboBox;
    private JComboBox<String> conditionComboBox;
    private JTextField valueField;
    private JTextField valueField2;

    private JComboBox<String> yearComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> yearComboBox2;
    private JComboBox<String> monthComboBox2;
    private JComboBox<String> dayComboBox2;
    private JLabel dateSeparatorLabel;
    private JButton addConditionBtn;
    private JButton clearAllBtn;
    private JButton startFilterBtn;
    private JButton cancelBtn;
    private JPanel conditionPanel;
    private JTable resultTable;
    private DefaultTableModel resultTableModel;
    private JLabel resultCountLabel;
    private JLabel timeCostLabel;

    private List<FilterCondition> conditions = new ArrayList<>();

    private static final Color COLOR_1 = new Color(59, 130, 246);
    private static final Color COLOR_2 = new Color(34, 197, 94);
    private static final Color COLOR_3 = new Color(239, 68, 68);
    private static final Color COLOR_4 = new Color(75, 85, 99);
    private static final Color BACKGROUND = new Color(249, 250, 251);

    public static class FilterCondition {
        String field;
        String fieldDisplayName;
        String condition;
        String value;
        String value2;

        public FilterCondition(String field, String fieldDisplayName, String condition, String value, String value2) {
            this.field = field;
            this.fieldDisplayName = fieldDisplayName;
            this.condition = condition;
            this.value = value;
            this.value2 = value2;
        }

        @Override
        public String toString() {
            if ("Between".equals(condition)) {
                return fieldDisplayName + " " + condition + " " + value + " to " + value2;
            }
            return fieldDisplayName + " " + condition + " " + value;
        }
    }

    public ZYHFilterDialog(Frame parent, String filterType, ZYHDataManager dataManager) {
        super(parent, "Filter Data", true);
        this.filterType = filterType;
        this.dataManager = dataManager;
        initComponents();
        setSize(1250, 750);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(BACKGROUND);
        topPanel.setBorder(new EmptyBorder(15, 15, 10, 15));
        topPanel.setLayout(new BorderLayout(15, 8));

        JPanel titlePanel = new JPanel(new BorderLayout(15, 0));
        titlePanel.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel(getTitleByType());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonsPanel.setBackground(BACKGROUND);

        startFilterBtn = createButton("Confirm Filter", COLOR_2);
        startFilterBtn.addActionListener(e -> performFilter());

        cancelBtn = createButton("Cancel", COLOR_4);
        cancelBtn.addActionListener(e -> dispose());

        actionButtonsPanel.add(startFilterBtn);
        actionButtonsPanel.add(cancelBtn);
        titlePanel.add(actionButtonsPanel, BorderLayout.EAST);

        topPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(BACKGROUND);
        inputPanel.setLayout(new BorderLayout(5, 5));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(BACKGROUND);
        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5));

        JLabel fieldLabel = new JLabel("Field:");
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(fieldLabel);

        fieldComboBox = new JComboBox<>(getFieldsByType());
        fieldComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fieldComboBox.setPreferredSize(new Dimension(120, 28));
        leftPanel.add(fieldComboBox);

        JLabel conditionLabel = new JLabel("Condition:");
        conditionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(conditionLabel);

        conditionComboBox = new JComboBox<>(new String[]{"Contains", "Equals", "Greater Than", "Less Than", "Between"});
        conditionComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        conditionComboBox.setPreferredSize(new Dimension(80, 28));
        leftPanel.add(conditionComboBox);

        JLabel valueLabel = new JLabel("Value:");
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        leftPanel.add(valueLabel);

        valueField = new JTextField();
        valueField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueField.setPreferredSize(new Dimension(150, 28));
        leftPanel.add(valueField);

        valueField2 = new JTextField();
        valueField2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        valueField2.setPreferredSize(new Dimension(150, 28));
        valueField2.setVisible(false);
        leftPanel.add(valueField2);

        yearComboBox = new JComboBox<>(getYearOptions());
        yearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yearComboBox.setPreferredSize(new Dimension(80, 28));
        yearComboBox.setVisible(false);
        leftPanel.add(yearComboBox);

        monthComboBox = new JComboBox<>(getMonthOptions());
        monthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthComboBox.setPreferredSize(new Dimension(60, 28));
        monthComboBox.setVisible(false);
        leftPanel.add(monthComboBox);

        dayComboBox = new JComboBox<>(getDayOptions(2026, 1));
        dayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dayComboBox.setPreferredSize(new Dimension(60, 28));
        dayComboBox.setVisible(false);
        leftPanel.add(dayComboBox);

        dateSeparatorLabel = new JLabel("to");
        dateSeparatorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateSeparatorLabel.setVisible(false);
        leftPanel.add(dateSeparatorLabel);

        yearComboBox2 = new JComboBox<>(getYearOptions());
        yearComboBox2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        yearComboBox2.setPreferredSize(new Dimension(80, 28));
        yearComboBox2.setVisible(false);
        leftPanel.add(yearComboBox2);

        monthComboBox2 = new JComboBox<>(getMonthOptions());
        monthComboBox2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        monthComboBox2.setPreferredSize(new Dimension(60, 28));
        monthComboBox2.setVisible(false);
        leftPanel.add(monthComboBox2);

        dayComboBox2 = new JComboBox<>(getDayOptions(2026, 1));
        dayComboBox2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dayComboBox2.setPreferredSize(new Dimension(60, 28));
        dayComboBox2.setVisible(false);
        leftPanel.add(dayComboBox2);

        addConditionBtn = createButton("Add Condition", COLOR_1);
        addConditionBtn.addActionListener(e -> addCondition());

        inputPanel.add(leftPanel, BorderLayout.CENTER);
        inputPanel.add(addConditionBtn, BorderLayout.EAST);

        yearComboBox.addActionListener(e -> updateDayOptions());
        monthComboBox.addActionListener(e -> updateDayOptions());
        yearComboBox2.addActionListener(e -> updateDayOptions2());
        monthComboBox2.addActionListener(e -> updateDayOptions2());
        fieldComboBox.addActionListener(e -> updateDateFieldVisibility());
        conditionComboBox.addActionListener(e -> updateDateFieldVisibility());

        updateDateFieldVisibility();

        topPanel.add(inputPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel conditionPanelContainer = new JPanel();
        conditionPanelContainer.setBackground(BACKGROUND);
        conditionPanelContainer.setBorder(new EmptyBorder(0, 15, 10, 15));
        conditionPanelContainer.setLayout(new BorderLayout(5, 5));

        JPanel conditionHeaderPanel = new JPanel(new BorderLayout());
        conditionHeaderPanel.setBackground(COLOR_4);
        conditionHeaderPanel.setPreferredSize(new Dimension(0, 30));

        JLabel conditionTitleLabel = new JLabel(" Current Filter Conditions ");
        conditionTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        conditionTitleLabel.setForeground(Color.WHITE);
        conditionHeaderPanel.add(conditionTitleLabel, BorderLayout.WEST);

        clearAllBtn = createSmallButton("Clear All Conditions", COLOR_3);
        clearAllBtn.addActionListener(e -> clearAllConditions());
        conditionHeaderPanel.add(clearAllBtn, BorderLayout.EAST);

        conditionPanelContainer.add(conditionHeaderPanel, BorderLayout.NORTH);

        conditionPanel = new JPanel();
        conditionPanel.setBackground(BACKGROUND);
        conditionPanel.setLayout(new BoxLayout(conditionPanel, BoxLayout.Y_AXIS));
        conditionPanel.add(new JLabel(" (No filter conditions yet)"));

        JScrollPane conditionScrollPane = new JScrollPane(conditionPanel);
        conditionScrollPane.setPreferredSize(new Dimension(0, 150));
        conditionScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_4, 1));
        conditionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        conditionPanelContainer.add(conditionScrollPane, BorderLayout.CENTER);

        add(conditionPanelContainer, BorderLayout.CENTER);

        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(BACKGROUND);
        resultPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        resultPanel.setLayout(new BorderLayout(5, 5));

        JLabel resultTitleLabel = new JLabel("Filter Results");
        resultTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resultPanel.add(resultTitleLabel, BorderLayout.NORTH);

        resultTableModel = new DefaultTableModel(getColumnsByType(), 0);
        resultTable = new JTable(resultTableModel);
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultTable.setRowHeight(25);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setBorder(BorderFactory.createLineBorder(COLOR_4, 1));
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);

        JPanel resultFooterPanel = new JPanel();
        resultFooterPanel.setBackground(BACKGROUND);
        resultFooterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 25, 5));

        resultCountLabel = new JLabel("Matching: 0 records");
        resultCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultFooterPanel.add(resultCountLabel);

        timeCostLabel = new JLabel("Time: --");
        timeCostLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultFooterPanel.add(timeCostLabel);

        resultPanel.add(resultFooterPanel, BorderLayout.SOUTH);
        add(resultPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 32));
        return btn;
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 24));
        return btn;
    }

    private void updateValueField2Visibility() {
        String condition = (String) conditionComboBox.getSelectedItem();
        boolean show = "Between".equals(condition);
        valueField2.setVisible(show);
        if (show) {
            valueField2.setText("");
        }
        revalidate();
        repaint();
    }

    private String getTitleByType() {
        switch (filterType) {
            case "inventory": return "Inventory Management - Filter Data";
            case "inbound": return "Inbound Management - Filter Data";
            case "outbound": return "Outbound Management - Filter Data";
            default: return "Filter Data";
        }
    }

    private String[] getFieldsByType() {
        switch (filterType) {
            case "inventory":
                return new String[]{"Barcode", "Product Name", "Purchase Price", "Sale Price", "Stock Quantity", "Status"};
            case "inbound":
                return new String[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"};
            case "outbound":
                return new String[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"};
            default:
                return new String[]{};
        }
    }

    private String[] getColumnsByType() {
        switch (filterType) {
            case "inventory":
                return new String[]{"Barcode", "Product Name", "Purchase Price", "Sale Price", "Stock Quantity", "Status"};
            case "inbound":
                return new String[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"};
            case "outbound":
                return new String[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"};
            default:
                return new String[]{};
        }
    }

    private String getFieldKey(String fieldName) {
        switch (filterType) {
            case "inventory":
                switch (fieldName) {
                    case "Barcode": return "barcode";
                    case "Product Name": return "name";
                    case "Purchase Price": return "buyPrice";
                    case "Sale Price": return "sellPrice";
                    case "Stock Quantity": return "stockQuantity";
                    case "Status": return "status";
                }
                break;
            case "inbound":
            case "outbound":
                switch (fieldName) {
                    case "Record ID": return "recordId";
                    case "Product Barcode": return "barcode";
                    case "Product Name": return "itemName";
                    case "Quantity": return "quantity";
                    case "Date": return "date";
                }
                break;
        }
        return "";
    }



    private void updateConditionPanel() {
        conditionPanel.removeAll();

        if (conditions.isEmpty()) {
            conditionPanel.add(new JLabel(" (No filter conditions yet)"));
        } else {
            for (int i = 0; i < conditions.size(); i++) {
                final int index = i;
                FilterCondition fc = conditions.get(i);

                JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
                rowPanel.setBackground(BACKGROUND);

                JLabel conditionLabel = new JLabel((i + 1) + ". " + fc.toString());
                conditionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                rowPanel.add(conditionLabel);

                long matchCount = countMatches(fc);
                JLabel countLabel = new JLabel("(Match: " + matchCount + " records)");
                countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                countLabel.setForeground(COLOR_4);
                rowPanel.add(countLabel);

                JButton removeBtn = new JButton("Delete");
                removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                removeBtn.setBackground(COLOR_3);
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setBorderPainted(false);
                removeBtn.setPreferredSize(new Dimension(60, 28));
                removeBtn.addActionListener(e -> removeCondition(index));
                rowPanel.add(removeBtn);

                conditionPanel.add(rowPanel);
            }
        }

        conditionPanel.revalidate();
        conditionPanel.repaint();
    }

    private void removeCondition(int index) {
        conditions.remove(index);
        updateConditionPanel();
    }

    private long countMatches(FilterCondition fc) {
        long count = 0;
        switch (filterType) {
            case "inventory":
                for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                    ZYHProduct p = dataManager.getInventoryList().get(i);
                    if (matchField(p, fc)) count++;
                }
                break;
            case "inbound":
                for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
                    ZYHInboundRecord r = dataManager.getInboundQueue().get(i);
                    if (matchField(r, fc)) count++;
                }
                break;
            case "outbound":
                for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
                    ZYHOutboundRecord r = dataManager.getOutboundStack().get(i);
                    if (matchField(r, fc)) count++;
                }
                break;
        }
        return count;
    }

    private boolean matchField(Object obj, FilterCondition fc) {
        String fieldValue = getFieldValue(obj, fc.field);
        if (fieldValue == null) return false;

        switch (fc.condition) {
            case "Contains":
                return fieldValue.contains(fc.value);
            case "Equals":
                return fieldValue.equals(fc.value);
            case "Greater Than":
                return compareValues(fieldValue, fc.value) > 0;
            case "Less Than":
                return compareValues(fieldValue, fc.value) < 0;
            case "Between":
                return compareValues(fieldValue, fc.value) >= 0 && compareValues(fieldValue, fc.value2) <= 0;
            default:
                return false;
        }
    }

    private String getFieldValue(Object obj, String field) {
        if (obj instanceof ZYHProduct) {
            ZYHProduct p = (ZYHProduct) obj;
            switch (field) {
                case "barcode": return p.getBarcode();
                case "name": return p.getName();
                case "buyPrice": return String.valueOf(p.getBuyPrice());
                case "sellPrice": return String.valueOf(p.getSellPrice());
                case "stockQuantity": return String.valueOf(p.getStockQuantity());
                case "status": return p.getStatus();
            }
        } else if (obj instanceof ZYHInboundRecord) {
            ZYHInboundRecord r = (ZYHInboundRecord) obj;
            switch (field) {
                case "recordId": return r.getRecordId() != null ? r.getRecordId() : "";
                case "barcode": return r.getBarcode();
                case "itemName": return r.getItemName();
                case "quantity": return String.valueOf(r.getQuantity());
                case "date": return r.getDate();
            }
        } else if (obj instanceof ZYHOutboundRecord) {
            ZYHOutboundRecord r = (ZYHOutboundRecord) obj;
            switch (field) {
                case "recordId": return r.getRecordId() != null ? r.getRecordId() : "";
                case "barcode": return r.getBarcode();
                case "itemName": return r.getItemName();
                case "quantity": return String.valueOf(r.getQuantity());
                case "date": return r.getDate();
            }
        }
        return "";
    }

    private int compareValues(String val1, String val2) {
        try {
            if (val1.matches("\\d+(\\.\\d+)?") && val2.matches("\\d+(\\.\\d+)?")) {
                double d1 = Double.parseDouble(val1);
                double d2 = Double.parseDouble(val2);
                return Double.compare(d1, d2);
            } else {
                return val1.compareTo(val2);
            }
        } catch (NumberFormatException e) {
            return val1.compareTo(val2);
        }
    }

    private void clearAllConditions() {
        conditions.clear();
        updateConditionPanel();
    }

    private void performFilter() {
        if (conditions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one filter condition first");
            return;
        }

        long startTime = System.nanoTime();

        resultTableModel.setRowCount(0);
        String dataType = "";

        switch (filterType) {
            case "inventory":
                dataType = "Sequential List";
                for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                    ZYHProduct p = dataManager.getInventoryList().get(i);
                    if (matchAllConditions(p)) {
                        resultTableModel.addRow(new Object[]{
                            p.getBarcode(),
                            p.getName(),
                            p.getBuyPrice(),
                            p.getSellPrice(),
                            p.getStockQuantity(),
                            p.getStatus()
                        });
                    }
                }
                break;
            case "inbound":
                dataType = "Linked List (Inbound Queue)";
                for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
                    ZYHInboundRecord r = dataManager.getInboundQueue().get(i);
                    if (matchAllConditions(r)) {
                        resultTableModel.addRow(new Object[]{
                            r.getRecordId(),
                            r.getBarcode(),
                            r.getItemName(),
                            r.getQuantity(),
                            r.getDate()
                        });
                    }
                }
                break;
            case "outbound":
                dataType = "Linked List (Outbound Stack)";
                for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
                    ZYHOutboundRecord r = dataManager.getOutboundStack().get(i);
                    if (matchAllConditions(r)) {
                        resultTableModel.addRow(new Object[]{
                            r.getRecordId(),
                            r.getBarcode(),
                            r.getItemName(),
                            r.getQuantity(),
                            r.getDate()
                        });
                    }
                }
                break;
        }

        long endTime = System.nanoTime();
        double timeMs = (endTime - startTime) / 1_000_000.0;

        resultCountLabel.setText("Matching: " + resultTableModel.getRowCount() + " records");
        timeCostLabel.setText("Time: Filtered using " + dataType + ", took " + String.format("%.2f", timeMs) + " ms");
    }

    private boolean matchAllConditions(Object obj) {
        for (FilterCondition fc : conditions) {
            if (!matchField(obj, fc)) {
                return false;
            }
        }
        return true;
    }

    private String[] getYearOptions() {
        return new String[]{"2024", "2025", "2026", "2027", "2028"};
    }

    private String[] getMonthOptions() {
        return new String[]{"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    }

    private String[] getDayOptions(int year, int month) {
        int daysInMonth = getDaysInMonth(year, month);
        String[] days = new String[daysInMonth];
        for (int i = 1; i <= daysInMonth; i++) {
            days[i - 1] = String.format("%02d", i);
        }
        return days;
    }

    private int getDaysInMonth(int year, int month) {
        switch (month) {
            case 2:
                return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                return 31;
        }
    }

    private void updateDayOptions() {
        int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
        int month = Integer.parseInt((String) monthComboBox.getSelectedItem());
        String[] days = getDayOptions(year, month);
        dayComboBox.removeAllItems();
        for (String day : days) {
            dayComboBox.addItem(day);
        }
    }

    private void updateDayOptions2() {
        int year = Integer.parseInt((String) yearComboBox2.getSelectedItem());
        int month = Integer.parseInt((String) monthComboBox2.getSelectedItem());
        String[] days = getDayOptions(year, month);
        dayComboBox2.removeAllItems();
        for (String day : days) {
            dayComboBox2.addItem(day);
        }
    }

    private void updateDateFieldVisibility() {
        String field = (String) fieldComboBox.getSelectedItem();
        String condition = (String) conditionComboBox.getSelectedItem();
        boolean isDateField = "Date".equals(field);
        boolean isBetween = "Between".equals(condition);

        valueField.setVisible(!isDateField);
        valueField2.setVisible(!isDateField && isBetween);

        yearComboBox.setVisible(isDateField);
        monthComboBox.setVisible(isDateField);
        dayComboBox.setVisible(isDateField);

        dateSeparatorLabel.setVisible(isDateField && isBetween);
        yearComboBox2.setVisible(isDateField && isBetween);
        monthComboBox2.setVisible(isDateField && isBetween);
        dayComboBox2.setVisible(isDateField && isBetween);

        revalidate();
        repaint();
    }

    private void addCondition() {
        String fieldDisplayName = (String) fieldComboBox.getSelectedItem();
        String condition = (String) conditionComboBox.getSelectedItem();
        String value = "";
        String value2 = "";

        if ("Date".equals(fieldDisplayName)) {
            String year = (String) yearComboBox.getSelectedItem();
            String month = (String) monthComboBox.getSelectedItem();
            String day = (String) dayComboBox.getSelectedItem();
            value = year + "-" + month + "-" + day;

            if ("Between".equals(condition)) {
                String year2 = (String) yearComboBox2.getSelectedItem();
                String month2 = (String) monthComboBox2.getSelectedItem();
                String day2 = (String) dayComboBox2.getSelectedItem();
                value2 = year2 + "-" + month2 + "-" + day2;
            }
        } else {
            value = valueField.getText().trim();
            value2 = valueField2.getText().trim();
        }

        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a filter value");
            return;
        }

        if ("Between".equals(condition) && value2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the range end value");
            return;
        }

        String fieldKey = getFieldKey(fieldDisplayName);
        FilterCondition fc = new FilterCondition(fieldKey, fieldDisplayName, condition, value, value2);
        conditions.add(fc);

        updateConditionPanel();

        if (!"Date".equals(fieldDisplayName)) {
            valueField.setText("");
            valueField2.setText("");
        }
    }
}
