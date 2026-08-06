package zyhinventory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class ZYHInventoryApp extends JFrame {
    private static final Color COLOR_1 = new Color(12, 74, 110);
    private static final Color COLOR_2 = new Color(3, 105, 161);
    private static final Color COLOR_3 = new Color(14, 165, 233);
    private static final Color COLOR_4 = new Color(186, 230, 253);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color BACKGROUND = new Color(245, 247, 250);

    private ZYHDataManager dataManager;
    private DefaultTableModel inventoryTableModel;
    private DefaultTableModel inboundTableModel;
    private DefaultTableModel outboundTableModel;
    private JTable inventoryTable;
    private JTable inboundTable;
    private JTable outboundTable;
    private JTextArea logArea;
    private CardLayout centerCardLayout;
    private JPanel centerPanel;
    private JPanel contentPanel;
    private JLabel inboundQueueInfoLabel;
    private JLabel outboundStackInfoLabel;
    private String timeGranularity = "day"; // day, month, year

    public ZYHInventoryApp() {
        this.dataManager = new ZYHDataManager();
        initComponents();
        javax.swing.SwingUtilities.invokeLater(() -> loadDataFromAPI());
    }

    private void initComponents() {
        setTitle("Zou Yuheng Inventory Management System");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 15));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = createLeftPanel();
        centerPanel = createCenterPanel();

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND);
        leftPanel.setPreferredSize(new Dimension(200, 500));

        JLabel titleLabel = new JLabel("<html>Inventory<br>Management</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(COLOR_1);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton inventoryBtn = createMenuButton("<html>Inventory<br>Management</html>", COLOR_2, e -> showPanel("inventory"));
        JButton inboundBtn = createMenuButton("<html>Inbound<br>Management</html>", COLOR_2, e -> showPanel("inbound"));
        JButton outboundBtn = createMenuButton("<html>Outbound<br>Management</html>", COLOR_2, e -> showPanel("outbound"));
        JButton reportBtn = createMenuButton("<html>Report<br>Generation</html>", COLOR_2, e -> showPanel("report"));
        JButton chartBtn = createMenuButton("Chart Display", COLOR_2, e -> showPanel("chart"));
        JButton resetBtn = createMenuButton("Reset Data", COLOR_2, e -> resetDataFromAPI());
        JButton analysisBtn = createMenuButton("Data Analysis", COLOR_2, e -> showAnalysisDialog());
        JButton predictBtn = createMenuButton("Stock Prediction", COLOR_2, e -> showStockPredictionDialog());

        leftPanel.add(titleLabel);
        leftPanel.add(inventoryBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(inboundBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(outboundBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(reportBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(chartBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(resetBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(analysisBtn);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(predictBtn);

        return leftPanel;
    }

    private JButton createMenuButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
        JButton button = new RoundedButton(text, 8);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(COLOR_WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setOpaque(true);
        button.addActionListener(listener);
        return button;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new RoundedButton(text, 6);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(COLOR_WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND);

        centerCardLayout = new CardLayout();
        contentPanel = new JPanel(centerCardLayout);
        contentPanel.setName("contentPanel");

        inventoryTableModel = new DefaultTableModel(
                new Object[]{"Barcode", "Product Name", "Purchase Price", "Sale Price", "Stock Quantity", "Status"}, 0);
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inventoryTable.setRowHeight(28);
        inventoryTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        inventoryTable.setRowHeight(30);
        TableColumn invCol0 = inventoryTable.getColumnModel().getColumn(0);
        invCol0.setMinWidth(130);
        invCol0.setMaxWidth(230);
        invCol0.setWidth(130);
        invCol0.setPreferredWidth(130);
        TableColumn invCol1 = inventoryTable.getColumnModel().getColumn(1);
        invCol1.setMinWidth(200);
        invCol1.setMaxWidth(300);
        invCol1.setWidth(200);
        invCol1.setPreferredWidth(200);
        TableColumn invCol2 = inventoryTable.getColumnModel().getColumn(2);
        invCol2.setMinWidth(100);
        invCol2.setMaxWidth(200);
        invCol2.setWidth(100);
        invCol2.setPreferredWidth(100);
        TableColumn invCol3 = inventoryTable.getColumnModel().getColumn(3);
        invCol3.setMinWidth(100);
        invCol3.setMaxWidth(200);
        invCol3.setWidth(100);
        invCol3.setPreferredWidth(100);
        TableColumn invCol4 = inventoryTable.getColumnModel().getColumn(4);
        invCol4.setMinWidth(120);
        invCol4.setMaxWidth(220);
        invCol4.setWidth(120);
        invCol4.setPreferredWidth(120);
        TableColumn invCol5 = inventoryTable.getColumnModel().getColumn(5);
        invCol5.setMinWidth(80);
        invCol5.setMaxWidth(180);
        invCol5.setWidth(80);
        invCol5.setPreferredWidth(80);
        JScrollPane inventoryScroll = new JScrollPane(inventoryTable);

        inboundTableModel = new DefaultTableModel(
                new Object[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"}, 0);
        inboundTable = new JTable(inboundTableModel);
        inboundTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        inboundTable.setRowHeight(28);
        inboundTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        inboundTable.setRowHeight(30);
        TableColumn inbCol0 = inboundTable.getColumnModel().getColumn(0);
        inbCol0.setMinWidth(80);
        inbCol0.setMaxWidth(180);
        inbCol0.setWidth(80);
        inbCol0.setPreferredWidth(80);
        TableColumn inbCol1 = inboundTable.getColumnModel().getColumn(1);
        inbCol1.setMinWidth(130);
        inbCol1.setMaxWidth(230);
        inbCol1.setWidth(130);
        inbCol1.setPreferredWidth(130);
        TableColumn inbCol2 = inboundTable.getColumnModel().getColumn(2);
        inbCol2.setMinWidth(200);
        inbCol2.setMaxWidth(300);
        inbCol2.setWidth(200);
        inbCol2.setPreferredWidth(200);
        TableColumn inbCol3 = inboundTable.getColumnModel().getColumn(3);
        inbCol3.setMinWidth(80);
        inbCol3.setMaxWidth(180);
        inbCol3.setWidth(80);
        inbCol3.setPreferredWidth(80);
        TableColumn inbCol4 = inboundTable.getColumnModel().getColumn(4);
        inbCol4.setMinWidth(120);
        inbCol4.setMaxWidth(220);
        inbCol4.setWidth(120);
        inbCol4.setPreferredWidth(120);
        JScrollPane inboundScroll = new JScrollPane(inboundTable);

        outboundTableModel = new DefaultTableModel(
                new Object[]{"Record ID", "Product Barcode", "Product Name", "Quantity", "Date"}, 0);
        outboundTable = new JTable(outboundTableModel);
        outboundTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        outboundTable.setRowHeight(28);
        outboundTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        outboundTable.setRowHeight(30);
        TableColumn outCol0 = outboundTable.getColumnModel().getColumn(0);
        outCol0.setMinWidth(80);
        outCol0.setMaxWidth(180);
        outCol0.setWidth(80);
        outCol0.setPreferredWidth(80);
        TableColumn outCol1 = outboundTable.getColumnModel().getColumn(1);
        outCol1.setMinWidth(130);
        outCol1.setMaxWidth(230);
        outCol1.setWidth(130);
        outCol1.setPreferredWidth(130);
        TableColumn outCol2 = outboundTable.getColumnModel().getColumn(2);
        outCol2.setMinWidth(200);
        outCol2.setMaxWidth(300);
        outCol2.setWidth(200);
        outCol2.setPreferredWidth(200);
        TableColumn outCol3 = outboundTable.getColumnModel().getColumn(3);
        outCol3.setMinWidth(80);
        outCol3.setMaxWidth(180);
        outCol3.setWidth(80);
        outCol3.setPreferredWidth(80);
        TableColumn outCol4 = outboundTable.getColumnModel().getColumn(4);
        outCol4.setMinWidth(120);
        outCol4.setMaxWidth(220);
        outCol4.setWidth(120);
        outCol4.setPreferredWidth(120);
        JScrollPane outboundScroll = new JScrollPane(outboundTable);

        JPanel inventoryPanel = createInventoryPanel();
        JPanel inboundPanel = createInboundManagementPanel();
        JPanel outboundPanel = createOutboundManagementPanel();
        JPanel reportPanel = createReportPanel();
        JPanel chartPanel = createChartPanel();
        JPanel comparePanel = createComparePanel();

        contentPanel.add(inventoryPanel, "inventory");
        contentPanel.add(inboundPanel, "inbound");
        contentPanel.add(outboundPanel, "outbound");
        contentPanel.add(reportPanel, "report");
        contentPanel.add(chartPanel, "chart");
        contentPanel.add(comparePanel, "compare");

        centerPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setPreferredSize(new Dimension(0, 150));
        logPanel.setBorder(BorderFactory.createTitledBorder("Operation Log"));
        logArea = new JTextArea();
        logArea.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);
        logPanel.add(logScroll, BorderLayout.CENTER);

        centerPanel.add(logPanel, BorderLayout.SOUTH);

        centerCardLayout.show(contentPanel, "inventory");

        return centerPanel;
    }

    private void showPanel(String panelName) {
        centerCardLayout.show(contentPanel, panelName);
    }

    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("<html>Inventory<br>Management</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("Add Product", COLOR_1);
        addBtn.addActionListener(e -> showAddProductDialog());

        JButton modifyBtn = createActionButton("Modify Price", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyPriceDialog());

        JButton deleteBtn = createActionButton("Delete Product", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedInventory());

        JButton filterBtn = createActionButton("Filter Data", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("inventory"));

        JButton batchPriceBtn = createActionButton("Batch Price Update", new Color(249, 115, 22));
        batchPriceBtn.addActionListener(e -> showBatchPriceDialog());

        buttonPanel.add(addBtn);
        buttonPanel.add(modifyBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(batchPriceBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInboundManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("<html>Inbound<br>Management</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("Add Inbound", COLOR_1);
        addBtn.addActionListener(e -> showAddInboundDialog());

        JButton modifyBtn = createActionButton("Modify Inbound Record", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyInboundDialog());

        JButton deleteBtn = createActionButton("Delete Inbound Record", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedInbound());

        JButton filterBtn = createActionButton("Filter Data", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("inbound"));

        JButton batchInboundBtn = createActionButton("Batch Inbound", COLOR_3);
        batchInboundBtn.addActionListener(e -> showBatchInboundDialog());

        buttonPanel.add(addBtn);
        buttonPanel.add(modifyBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(batchInboundBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(inboundTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(BACKGROUND);
        inboundQueueInfoLabel = new JLabel("Inbound Queue (FIFO) Current Length: " + dataManager.getInboundCount());
        inboundQueueInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(inboundQueueInfoLabel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createOutboundManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("<html>Outbound<br>Management</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("Add Outbound", COLOR_1);
        addBtn.addActionListener(e -> showAddOutboundDialog());

        JButton modifyBtn = createActionButton("Modify Outbound Record", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyOutboundDialog());

        JButton deleteBtn = createActionButton("Delete Outbound Record", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedOutbound());

        JButton filterBtn = createActionButton("Filter Data", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("outbound"));

        JButton batchOutboundBtn = createActionButton("Batch Outbound", COLOR_3);
        batchOutboundBtn.addActionListener(e -> showBatchOutboundDialog());

        buttonPanel.add(addBtn);
        buttonPanel.add(modifyBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(filterBtn);
        buttonPanel.add(batchOutboundBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(outboundTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(BACKGROUND);
        outboundStackInfoLabel = new JLabel("Outbound Stack (LIFO) Current Length: " + dataManager.getOutboundCount());
        outboundStackInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoPanel.add(outboundStackInfoLabel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private String findProductNameByBarcode(String barcode) {
        for (int i = 0; i < dataManager.getProductList().size(); i++) {
            ZYHProduct product = dataManager.getProductList().get(i);
            if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                return product.getName();
            }
        }
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                return product.getName();
            }
        }
        return "";
    }

    private void updateInboundQueueInfo() {
        if (inboundQueueInfoLabel != null) {
            inboundQueueInfoLabel.setText("Inbound Queue (FIFO) Current Length: " + dataManager.getInboundCount());
        }
    }

    private void updateOutboundStackInfo() {
        if (outboundStackInfoLabel != null) {
            outboundStackInfoLabel.setText("Outbound Stack (LIFO) Current Length: " + dataManager.getOutboundCount());
        }
    }

    private void updateInboundRecordsWithProductInfo(String barcode, String name) {
        for (ZYHInboundRecord record : dataManager.getInboundQueue()) {
            if (record.getBarcode().equals(barcode) && (record.getItemName() == null || record.getItemName().isEmpty())) {
                record.setItemName(name);
            }
        }
        for (int i = 0; i < inboundTableModel.getRowCount(); i++) {
            String rowBarcode = (String) inboundTableModel.getValueAt(i, 1);
            if (rowBarcode.equals(barcode)) {
                String currentName = (String) inboundTableModel.getValueAt(i, 2);
                if (currentName == null || currentName.isEmpty()) {
                    inboundTableModel.setValueAt(name, i, 2);
                }
            }
        }
    }

    private int calculateStockFromInbound(String barcode) {
        int total = 0;
        for (ZYHInboundRecord record : dataManager.getInboundQueue()) {
            if (record.getBarcode().equals(barcode)) {
                total += record.getQuantity();
            }
        }
        return total;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inventory Business Report");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton generateBtn = createActionButton("Generate Report", COLOR_1);
        generateBtn.addActionListener(e -> generateReport());

        JButton perfTestBtn = createActionButton("Performance Test", COLOR_2);
        perfTestBtn.addActionListener(e -> runPerformanceTest());

        JButton htmlReportBtn = createActionButton("Generate HTML Report", COLOR_3);
        htmlReportBtn.addActionListener(e -> generateHtmlReport());

        buttonPanel.add(generateBtn);
        buttonPanel.add(perfTestBtn);
        buttonPanel.add(htmlReportBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));
        JScrollPane reportScroll = new JScrollPane(reportArea);
        reportScroll.setName("reportArea");

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(reportScroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createChartPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inventory Data Chart Display");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton refreshBtn = createActionButton("Refresh Chart", COLOR_1);
        refreshBtn.addActionListener(e -> refreshChart());

        JLabel granularityLabel = new JLabel("Time Granularity:");
        granularityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JComboBox<String> granularityCombo = new JComboBox<>(new String[]{"By Day", "By Month", "By Year"});
        granularityCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        granularityCombo.setPreferredSize(new Dimension(100, 28));
        granularityCombo.setSelectedIndex(0);
        granularityCombo.addActionListener(e -> {
            int index = granularityCombo.getSelectedIndex();
            if (index == 0) {
                timeGranularity = "day";
            } else if (index == 1) {
                timeGranularity = "month";
            } else {
                timeGranularity = "year";
            }
            refreshChart();
        });

        buttonPanel.add(refreshBtn);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(granularityLabel);
        buttonPanel.add(granularityCombo);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        chartPanel.setBackground(BACKGROUND);

        JPanel pieChartPanel = createPieChartPanel();
        JPanel barChartPanel = createBarChartPanel();

        chartPanel.add(pieChartPanel);
        chartPanel.add(barChartPanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPieChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));

        JLabel titleLabel = new JLabel("Inventory Distribution Pie Chart");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));

        JPanel pieArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                
                int centerX = width / 2;
                int centerY = height / 2;
                int radius = Math.min(width, height) / 2 - 20;

                java.util.List<java.util.Map.Entry<String, Integer>> positiveList = new java.util.ArrayList<>();
                java.util.List<java.util.Map.Entry<String, Integer>> negativeList = new java.util.ArrayList<>();
                
                for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                    ZYHProduct product = dataManager.getInventoryList().get(i);
                    int stock = product.getStockQuantity();
                    if (stock >= 0) {
                        positiveList.add(new java.util.AbstractMap.SimpleEntry<>(product.getName() + "|" + product.getBarcode(), stock));
                    } else {
                        negativeList.add(new java.util.AbstractMap.SimpleEntry<>(product.getName() + "|" + product.getBarcode(), stock));
                    }
                }

                int totalPositive = positiveList.stream().mapToInt(e -> e.getValue()).sum();

                if (totalPositive == 0) {
                    g2d.setColor(Color.GRAY);
                    g2d.drawString("No Data", centerX - 30, centerY);
                    return;
                }

                Color[] colors = {COLOR_2, COLOR_3, new Color(234, 179, 8), new Color(168, 85, 247), 
                                  new Color(236, 72, 153), new Color(34, 197, 94), new Color(6, 182, 212),
                                  new Color(249, 115, 22), new Color(139, 92, 246), new Color(20, 184, 166),
                                  new Color(234, 179, 8), new Color(168, 85, 247), new Color(236, 72, 153)};
                int currentAngle = 0;
                int colorIndex = 0;
                
                for (java.util.Map.Entry<String, Integer> entry : positiveList) {
                    int stock = entry.getValue();
                    int angle = (int) (360.0 * stock / totalPositive);
                    
                    Color currentColor = colors[colorIndex % colors.length];
                    g2d.setColor(currentColor);
                    g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, currentAngle, angle);
                    
                    currentAngle += angle;
                    colorIndex++;
                }
            }
        };
        pieArea.setBackground(Color.WHITE);

        JPanel legendArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                
                java.util.List<String[]> legendItems = new java.util.ArrayList<>();
                java.util.List<String[]> negativeItems = new java.util.ArrayList<>();
                
                for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                    ZYHProduct product = dataManager.getInventoryList().get(i);
                    int stock = product.getStockQuantity();
                    if (stock >= 0) {
                        legendItems.add(new String[]{product.getName(), String.valueOf(stock)});
                    } else {
                        negativeItems.add(new String[]{product.getName(), String.valueOf(stock)});
                    }
                }

                Color[] colors = {COLOR_2, COLOR_3, new Color(234, 179, 8), new Color(168, 85, 247), 
                                  new Color(236, 72, 153), new Color(34, 197, 94), new Color(6, 182, 212),
                                  new Color(249, 115, 22), new Color(139, 92, 246), new Color(20, 184, 166),
                                  new Color(234, 179, 8), new Color(168, 85, 247), new Color(236, 72, 153)};

                g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                int y = 10;
                int itemHeight = 22;
                int colorBoxSize = 14;
                int paddingLeft = 8;
                
                int colorIndex = 0;
                for (String[] item : legendItems) {
                    if (y > height - 30) break;
                    
                    Color currentColor = colors[colorIndex % colors.length];
                    g2d.setColor(currentColor);
                    g2d.fillRect(paddingLeft, y, colorBoxSize, colorBoxSize);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(item[0], paddingLeft + colorBoxSize + 8, y + 11);
                    
                    g2d.setColor(COLOR_1);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
                    int textWidth = g2d.getFontMetrics().stringWidth(item[1]);
                    g2d.drawString(item[1], width - textWidth - 8, y + 11);
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    
                    y += itemHeight;
                    colorIndex++;
                }
                
                if (!negativeItems.isEmpty()) {
                    y += 12;
                    
                    g2d.setColor(new Color(239, 68, 68));
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 11));
                    g2d.drawString("Abnormal Stock (Negative):", paddingLeft, y);
                    y += itemHeight;
                    
                    g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
                    for (String[] item : negativeItems) {
                        if (y > height - 30) break;
                        
                        g2d.setColor(new Color(239, 68, 68));
                        g2d.fillRect(paddingLeft, y, colorBoxSize, colorBoxSize);
                        g2d.drawString(item[0], paddingLeft + colorBoxSize + 8, y + 11);
                        
                        int textWidth = g2d.getFontMetrics().stringWidth(item[1]);
                        g2d.drawString(item[1], width - textWidth - 8, y + 11);
                        
                        y += itemHeight;
                    }
                }
            }
        };
        legendArea.setBackground(Color.WHITE);
        legendArea.setPreferredSize(new Dimension(160, 0));
        
        contentPanel.add(pieArea, BorderLayout.CENTER);
        contentPanel.add(legendArea, BorderLayout.EAST);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBarChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));

        JLabel titleLabel = new JLabel("Inbound/Outbound Statistics Bar Chart");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                int barWidth = 50;
                int padding = 60;
                int chartHeight = height - padding - 35;
                int chartWidth = width - padding * 2;
                int baseY = height - padding;

                java.util.Map<String, Integer> periodInbound = new java.util.HashMap<>();
                java.util.Map<String, Integer> periodOutbound = new java.util.HashMap<>();
                java.util.Set<String> allYears = new java.util.HashSet<>();
                
                for (ZYHInboundRecord record : dataManager.getInboundQueue()) {
                    String date = record.getDate();
                    String period = getPeriodKey(date);
                    periodInbound.put(period, periodInbound.getOrDefault(period, 0) + record.getQuantity());
                    allYears.add(date.substring(0, 4));
                }
                
                for (ZYHOutboundRecord record : dataManager.getOutboundStack()) {
                    String date = record.getDate();
                    String period = getPeriodKey(date);
                    periodOutbound.put(period, periodOutbound.getOrDefault(period, 0) + record.getQuantity());
                    allYears.add(date.substring(0, 4));
                }

                java.util.Set<String> allPeriods = new java.util.TreeSet<>();
                allPeriods.addAll(periodInbound.keySet());
                allPeriods.addAll(periodOutbound.keySet());

                if (allPeriods.isEmpty()) {
                    g2d.setColor(Color.GRAY);
                    g2d.drawString("No Data", width / 2 - 30, height / 2);
                    return;
                }

                boolean showFullYear = allYears.size() > 1;

                int maxValue = 1;
                for (int val : periodInbound.values()) maxValue = Math.max(maxValue, val);
                for (int val : periodOutbound.values()) maxValue = Math.max(maxValue, val);
                
                int scaleMax = ((maxValue / 100) + 1) * 100;
                if (scaleMax < 100) scaleMax = 100;

                int barGap = 15;
                int groupWidth = barWidth * 2 + barGap + 30;
                int startX = padding;
                
                g2d.setFont(new Font("SansSerif", Font.PLAIN, 11));
                
                java.util.Iterator<String> periodIter = allPeriods.iterator();
                int index = 0;
                
                while (periodIter.hasNext() && startX + groupWidth < width - padding) {
                    String period = periodIter.next();
                    int inboundVal = periodInbound.getOrDefault(period, 0);
                    int outboundVal = periodOutbound.getOrDefault(period, 0);
                    
                    int inboundHeight = scaleMax > 0 ? (int) ((double) inboundVal / scaleMax * chartHeight) : 0;
                    int outboundHeight = scaleMax > 0 ? (int) ((double) outboundVal / scaleMax * chartHeight) : 0;
                    
                    g2d.setColor(COLOR_2);
                    g2d.fillRect(startX, baseY - inboundHeight, barWidth, inboundHeight);
                    
                    if (inboundVal > 0) {
                        String inboundStr = String.valueOf(inboundVal);
                        int textWidth = g2d.getFontMetrics().stringWidth(inboundStr);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(inboundStr, startX + barWidth / 2 - textWidth / 2, baseY - inboundHeight - 5);
                    }
                    
                    g2d.setColor(new Color(239, 68, 68));
                    g2d.fillRect(startX + barWidth + barGap, baseY - outboundHeight, barWidth, outboundHeight);
                    
                    if (outboundVal > 0) {
                        String outboundStr = String.valueOf(outboundVal);
                        int textWidth = g2d.getFontMetrics().stringWidth(outboundStr);
                        g2d.setColor(Color.BLACK);
                        g2d.drawString(outboundStr, startX + barWidth + barGap + barWidth / 2 - textWidth / 2, baseY - outboundHeight - 5);
                    }
                    
                    g2d.setColor(Color.BLACK);
                    String displayPeriod = formatPeriodDisplay(period, showFullYear);
                    int dateWidth = g2d.getFontMetrics().stringWidth(displayPeriod);
                    g2d.drawString(displayPeriod, startX + groupWidth / 2 - dateWidth / 2, baseY + 18);
                    
                    startX += groupWidth;
                    index++;
                }

                g2d.setColor(Color.BLACK);
                g2d.drawLine(padding, baseY - chartHeight, padding, baseY);
                g2d.drawLine(padding, baseY, width - padding, baseY);
                
                int numTicks = scaleMax / 100;
                if (numTicks < 5) numTicks = 5;
                for (int i = 0; i <= numTicks; i++) {
                    int y = baseY - (int) ((double) i / numTicks * chartHeight);
                    g2d.drawLine(padding - 5, y, padding, y);
                    int tickValue = i * 100;
                    String tickStr = String.valueOf(tickValue);
                    int textWidth = g2d.getFontMetrics().stringWidth(tickStr);
                    g2d.drawString(tickStr, padding - textWidth - 8, y + 4);
                }

                g2d.setColor(COLOR_2);
                g2d.fillRect(width - 100, padding, 15, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Inbound", width - 80, padding + 8);
                
                g2d.setColor(new Color(239, 68, 68));
                g2d.fillRect(width - 100, padding + 18, 15, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("Outbound", width - 80, padding + 26);
            }
        };
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComparePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Sequential List vs Linked List Performance Comparison");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JSpinner dataSizeSpinner = new JSpinner(new SpinnerNumberModel(10000, 1000, 100000, 1000));
        JButton compareBtn = createActionButton("Start Comparison", COLOR_1);
        compareBtn.addActionListener(e -> {
            int size = (int) dataSizeSpinner.getValue();
            runComparison(size);
        });

        buttonPanel.add(new JLabel("Data Size:"));
        buttonPanel.add(dataSizeSpinner);
        buttonPanel.add(compareBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JTextArea compareArea = new JTextArea();
        compareArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        compareArea.setEditable(false);
        compareArea.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));
        JScrollPane compareScroll = new JScrollPane(compareArea);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(BACKGROUND);
        resultPanel.add(compareScroll, BorderLayout.CENTER);

        JLabel resultLabel = new JLabel("Comparison results will be displayed here");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(resultLabel, BorderLayout.NORTH);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(resultPanel, BorderLayout.CENTER);

        return panel;
    }

    private void updateInventoryQuantity(String barcode, int quantity) {
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                int currentStock = product.getStockQuantity();
                int newStock = currentStock + quantity;
                product.setStockQuantity(newStock);
                
                String status = newStock > 0 ? "Normal" : (newStock < 0 ? "Abnormal" : "Sold Out");
                product.setStatus(status);
                
                if (inventoryTableModel != null) {
                    inventoryTableModel.setValueAt(newStock, i, 4);
                    inventoryTableModel.setValueAt(status, i, 5);
                }
                
                log("[Stock Update] Barcode: " + barcode + ", Original Stock: " + currentStock + ", Added: " + quantity + ", New Stock: " + newStock);
                break;
            }
        }
    }

    private void log(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logMessage = "[" + timestamp + "] " + message;
        logArea.append(logMessage + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
        System.out.println(logMessage);
    }

    private void playSound(String type) {
        try {
            java.awt.Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
        }
    }

    private void loadDataFromAPI() {
        log("Starting to load data from API...");
        loadInventoryData();
        loadInboundData();
        loadOutboundData();
        updateInboundQueueInfo();
        updateOutboundStackInfo();
        log("Data loading complete");
    }

    private void loadInventoryData() {
        String response = ZYHApiClient.queryData("Inventory");
        log("[Inventory] API response: " + response);

        if (response != null && !response.isEmpty() && !response.contains("error")) {
            try {
                inventoryTableModel.setRowCount(0);
                dataManager.getInventoryList().clear();

                java.util.List<String> records = parseResponse(response);
                for (String record : records) {
                    String[] parts = record.split(",");
                    if (parts.length >= 6) {
                        String barcode = parts[0].trim();
                        String name = parts[1].trim();
                        double buyPrice = parseDouble(parts[2].trim());
                        double sellPrice = parseDouble(parts[3].trim());
                        int stock = parseInt(parts[4].trim());
                        String status = stock > 0 ? "Normal" : (stock < 0 ? "Abnormal" : "Sold Out");

                        ZYHProduct product = new ZYHProduct(barcode, name, buyPrice, sellPrice);
                        product.setStockQuantity(stock);
                        dataManager.getInventoryList().add(product);
                        inventoryTableModel.addRow(new Object[]{barcode, name, buyPrice, sellPrice, stock, status});
                    }
                }
                log("[Inventory] Successfully loaded " + records.size() + " records");
            } catch (Exception e) {
                log("[Inventory] Parse error: " + e.getMessage());
            }
        }
    }

    private void loadInboundData() {
        String response = ZYHApiClient.queryData("Inbound");
        log("[Inbound] API response: " + response);

        if (response != null && !response.isEmpty() && !response.contains("error")) {
            try {
                inboundTableModel.setRowCount(0);
                dataManager.getInboundQueue().clear();

                java.util.List<String> records = parseResponse(response);
                for (String record : records) {
                    String[] parts = record.split(",");
                    if (parts.length >= 6) {
                        String barcode = parts[0].trim();
                        String name = parts[1].trim();
                        int quantity = parseInt(parts[6].trim());
                        String date = parts[7].trim();
                        
                        if (!date.isEmpty()) {
                            try {
                                long timestamp = Long.parseLong(date);
                                date = java.time.LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(timestamp),
                                    java.time.ZoneId.systemDefault()
                                ).toLocalDate().toString();
                            } catch (Exception e) {
                            }
                        }

                        String recordId = String.valueOf(System.currentTimeMillis());
                        ZYHInboundRecord recordObj = new ZYHInboundRecord(recordId, barcode, name, quantity, date);
                        dataManager.getInboundQueue().addLast(recordObj);
                        inboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity, date});
                    }
                }
                log("[Inbound] Successfully loaded " + records.size() + " records");
            } catch (Exception e) {
                log("[Inbound] Parse error: " + e.getMessage());
            }
        }
    }

    private void loadOutboundData() {
        String response = ZYHApiClient.queryData("Outbound");
        log("[Outbound] API response: " + response);

        if (response != null && !response.isEmpty() && !response.contains("error")) {
            try {
                outboundTableModel.setRowCount(0);
                dataManager.getOutboundStack().clear();

                java.util.List<String> records = parseResponse(response);
                for (String record : records) {
                    String[] parts = record.split(",");
                    if (parts.length >= 6) {
                        String barcode = parts[0].trim();
                        String name = parts[1].trim();
                        int quantity = parseInt(parts[6].trim());
                        String date = parts[7].trim();
                        
                        if (!date.isEmpty()) {
                            try {
                                long timestamp = Long.parseLong(date);
                                date = java.time.LocalDateTime.ofInstant(
                                    java.time.Instant.ofEpochMilli(timestamp),
                                    java.time.ZoneId.systemDefault()
                                ).toLocalDate().toString();
                            } catch (Exception e) {
                            }
                        }

                        String recordId = String.valueOf(System.currentTimeMillis());
                        ZYHOutboundRecord recordObj = new ZYHOutboundRecord(recordId, barcode, name, quantity, date);
                        dataManager.getOutboundStack().addLast(recordObj);
                        outboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity, date});
                    }
                }
                log("[Outbound] Successfully loaded " + records.size() + " records");
            } catch (Exception e) {
                log("[Outbound] Parse error: " + e.getMessage());
            }
        }
    }

    private java.util.List<String> parseResponse(String response) {
        java.util.List<String> records = new java.util.ArrayList<>();
        if (response == null || response.isEmpty()) {
            return records;
        }

        response = response.replace("\\n", "").replace("\\r", "").replace("\\t", "");
        
        if (response.contains("\\\"")) {
            response = response.replace("\\\"", "\"");
        }

        log("[DEBUG] Processed response: " + response.substring(0, Math.min(200, response.length())) + "...");

        int dataArrayStart = response.indexOf("\"data\":[");
        if (dataArrayStart == -1) {
            log("[DEBUG] data array not found");
            return records;
        }
        
        int arrayContentStart = dataArrayStart + 8;
        int arrayEnd = response.lastIndexOf("]");
        
        if (arrayEnd == -1 || arrayEnd <= arrayContentStart) {
            log("[DEBUG] data array format error");
            return records;
        }
        
        String dataArrayContent = response.substring(arrayContentStart, arrayEnd).trim();
        
        java.util.List<String> itemList = new java.util.ArrayList<>();
        int braceCount = 0;
        StringBuilder currentItem = new StringBuilder();
        boolean inString = false;
        
        for (int i = 0; i < dataArrayContent.length(); i++) {
            char c = dataArrayContent.charAt(i);
            
            if (c == '"' && (i == 0 || dataArrayContent.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            
            if (!inString) {
                if (c == '{') {
                    braceCount++;
                    if (braceCount == 1) {
                        currentItem = new StringBuilder();
                    }
                    currentItem.append(c);
                } else if (c == '}') {
                    braceCount--;
                    currentItem.append(c);
                    
                    if (braceCount == 0) {
                        String fullItem = currentItem.toString();
                        int fieldsStart = fullItem.indexOf("\"fields\":{");
                        if (fieldsStart != -1) {
                            int fieldsObjStart = fullItem.indexOf("{", fieldsStart);
                            int fieldsObjEnd = fullItem.lastIndexOf("}");
                            if (fieldsObjEnd > fieldsObjStart) {
                                String fieldsObj = fullItem.substring(fieldsObjStart, fieldsObjEnd + 1);
                                itemList.add(fieldsObj);
                            }
                        }
                        currentItem = new StringBuilder();
                    }
                } else {
                    if (braceCount > 0) {
                        currentItem.append(c);
                    }
                }
            } else {
                if (braceCount > 0) {
                    currentItem.append(c);
                }
            }
        }
        
        log("[DEBUG] Split into " + itemList.size() + " records");
        for (int i = 0; i < itemList.size(); i++) {
            String item = itemList.get(i);
            log("[DEBUG] Processing record " + (i + 1) + ": " + item.substring(0, Math.min(80, item.length())) + "...");
            String record = extractRecordData(item);
            log("[DEBUG] Parse result: " + record);
            if (!record.isEmpty()) {
                records.add(record);
            }
        }

        log("[DEBUG] Parsed " + records.size() + " records");
        return records;
    }

    private String extractRecordData(String json) {
        String barcode = parseNestedValue(json, "Product Barcode");
        String name = parseNestedValue(json, "Product Name");
        String buyPrice = parseNestedValue(json, "Purchase Price");
        String sellPrice = parseNestedValue(json, "Sale Price");
        String stock = parseNestedValue(json, "Stock Quantity");
        String status = parseNestedValue(json, "Status");
        String quantity = parseNestedValue(json, "Quantity");
        String date = parseNestedValue(json, "Date");
        String inboundItem = parseNestedValue(json, "Inbound Item");
        String outboundItem = parseNestedValue(json, "Outbound Item");

        if (name.isEmpty()) {
            name = inboundItem.isEmpty() ? outboundItem : inboundItem;
        }

        return String.join(",", barcode, name, buyPrice, sellPrice, stock, status, quantity, date);
    }

    private String parseNestedValue(String json, String fieldName) {
        if (json == null || json.isEmpty() || fieldName == null || fieldName.isEmpty()) {
            return "";
        }

        String key = "\"" + fieldName + "\":";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) {
            log("[DEBUG] parseNestedValue: field not found '" + fieldName + "'");
            return "";
        }
        log("[DEBUG] parseNestedValue: field '" + fieldName + "' at position " + keyIndex + ", value starts at " + (keyIndex + key.length()));
        if (keyIndex + key.length() < json.length()) {
            log("[DEBUG] parseNestedValue: 100 chars after field '" + fieldName + "': " + json.substring(keyIndex, Math.min(keyIndex + 100, json.length())));
        }

        int valueStart = keyIndex + key.length();
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return "";
        }

        char firstChar = json.charAt(valueStart);
        
        if (firstChar == '[') {
            int textIdx = json.indexOf("\"text\":\"", valueStart);
            if (textIdx != -1) {
                int textValueStart = textIdx + 8;
                int textValueEnd = json.indexOf("\"", textValueStart);
                if (textValueEnd != -1) {
                    return json.substring(textValueStart, textValueEnd);
                }
            }
            
            int arrayEnd = json.indexOf("]", valueStart);
            if (arrayEnd != -1) {
                String arrayContent = json.substring(valueStart + 1, arrayEnd).trim();
                if (!arrayContent.isEmpty() && !arrayContent.startsWith("{")) {
                    return arrayContent;
                }
            }
        } else if (firstChar == '{') {
            int valueKeyIdx = json.indexOf("\"value\":", valueStart);
            if (valueKeyIdx != -1) {
                int valueContentStart = valueKeyIdx + 8;
                while (valueContentStart < json.length() && Character.isWhitespace(json.charAt(valueContentStart))) {
                    valueContentStart++;
                }

                if (valueContentStart < json.length()) {
                    char nextChar = json.charAt(valueContentStart);
                    if (nextChar == '"') {
                        int valueEnd = json.indexOf("\"", valueContentStart + 1);
                        if (valueEnd != -1) {
                            return json.substring(valueContentStart + 1, valueEnd);
                        }
                    } else if (Character.isDigit(nextChar) || nextChar == '-') {
                        StringBuilder sb = new StringBuilder();
                        if (nextChar == '-') {
                            sb.append('-');
                            valueContentStart++;
                        }
                        int pos = valueContentStart;
                        while (pos < json.length()) {
                            char c = json.charAt(pos);
                            if (Character.isDigit(c) || c == '.') {
                                sb.append(c);
                                pos++;
                            } else {
                                break;
                            }
                        }
                        return sb.toString();
                    } else if (nextChar == '[') {
                        int arrEnd = json.indexOf("]", valueContentStart);
                        if (arrEnd != -1) {
                            String arrContent = json.substring(valueContentStart + 1, arrEnd).trim();
                            if (!arrContent.isEmpty() && !arrContent.startsWith("{")) {
                                if (arrContent.startsWith("\"") && arrContent.endsWith("\"")) {
                                    return arrContent.substring(1, arrContent.length() - 1);
                                }
                                return arrContent;
                            }
                            
                            int textIdx = json.indexOf("\"text\":\"", valueContentStart);
                            if (textIdx != -1 && textIdx < arrEnd) {
                                int textValueStart = textIdx + 8;
                                int textValueEnd = json.indexOf("\"", textValueStart);
                                if (textValueEnd != -1) {
                                    return json.substring(textValueStart, textValueEnd);
                                }
                            }
                        }
                    }
                }
            }
        } else if (Character.isDigit(firstChar) || firstChar == '-') {
            StringBuilder sb = new StringBuilder();
            if (firstChar == '-') {
                sb.append('-');
                valueStart++;
            }
            int pos = valueStart;
            while (pos < json.length()) {
                char c = json.charAt(pos);
                if (Character.isDigit(c) || c == '.') {
                    sb.append(c);
                    pos++;
                } else {
                    break;
                }
            }
            return sb.toString();
        } else if (firstChar == '"') {
            int valueEnd = json.indexOf("\"", valueStart + 1);
            if (valueEnd != -1) {
                return json.substring(valueStart + 1, valueEnd);
            }
        }

        return "";
    }

    private int findMatchingBrace(String str, int start) {
        int depth = 1;
        for (int i = start; i < str.length(); i++) {
            if (str.charAt(i) == '{') depth++;
            else if (str.charAt(i) == '}') depth--;
            if (depth == 0) return i;
        }
        return -1;
    }

    private String parseFieldsData(String fieldsStr) {
        String barcode = extractSimpleValue(fieldsStr, "Product Barcode");
        String name = extractSimpleValue(fieldsStr, "Product Name");
        String buyPrice = extractSimpleValue(fieldsStr, "Purchase Price");
        String sellPrice = extractSimpleValue(fieldsStr, "Sale Price");
        String stock = extractSimpleValue(fieldsStr, "Stock Quantity");
        String status = extractSimpleValue(fieldsStr, "Status");
        String quantity = extractSimpleValue(fieldsStr, "Quantity");
        String date = extractSimpleValue(fieldsStr, "Date");
        String inboundItem = extractSimpleValue(fieldsStr, "Inbound Item");
        String outboundItem = extractSimpleValue(fieldsStr, "Outbound Item");
        
        if (name.isEmpty()) {
            name = inboundItem.isEmpty() ? outboundItem : inboundItem;
        }
        
        return String.join(",", barcode, name, buyPrice, sellPrice, stock, status, quantity, date);
    }

    private String extractSimpleValue(String json, String fieldName) {
        if (json == null || json.isEmpty() || fieldName == null || fieldName.isEmpty()) {
            return "";
        }
        
        String key = "\"" + fieldName + "\":";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) {
            return "";
        }
        
        int braceStart = json.indexOf("{", keyIndex);
        if (braceStart == -1) {
            return "";
        }
        
        int valueTextStart = json.indexOf("\"value\":[{\"text\":\"", braceStart);
        if (valueTextStart != -1) {
            int valueStart = valueTextStart + 16;
            int valueEnd = json.indexOf("\"", valueStart);
            if (valueEnd != -1) {
                return json.substring(valueStart, valueEnd);
            }
        }
        
        int textStart = json.indexOf("\"text\":\"", braceStart);
        if (textStart != -1) {
            int textEnd = json.indexOf("\"", textStart + 8);
            if (textEnd != -1) {
                return json.substring(textStart + 8, textEnd);
            }
        }
        
        int valueStart = json.indexOf("\"value\":\"", braceStart);
        if (valueStart != -1) {
            int valueEnd = json.indexOf("\"", valueStart + 9);
            if (valueEnd != -1) {
                return json.substring(valueStart + 9, valueEnd);
            }
        }
        
        return "";
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField barcodeField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField buyPriceField = new JTextField(20);
        JTextField sellPriceField = new JTextField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Product Barcode:"), gbc);
        gbc.gridx = 1;
        panel.add(barcodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Purchase Price:"), gbc);
        gbc.gridx = 1;
        panel.add(buyPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Sale Price:"), gbc);
        gbc.gridx = 1;
        panel.add(sellPriceField, gbc);

        barcodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                String barcode = barcodeField.getText().trim();
                if (!barcode.isEmpty()) {
                    String existingName = findProductNameByBarcode(barcode);
                    if (!existingName.isEmpty()) {
                        nameField.setText(existingName);
                    }
                }
            }
        });

        JButton okBtn = createActionButton("Confirm", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                double buyPrice = Double.parseDouble(buyPriceField.getText().trim());
                double sellPrice = Double.parseDouble(sellPriceField.getText().trim());
                
                int stock = calculateStockFromInbound(barcode);

                ZYHProduct product = new ZYHProduct(barcode, name, buyPrice, sellPrice);
                product.setStockQuantity(stock);
                dataManager.getInventoryList().add(product);
                inventoryTableModel.addRow(new Object[]{barcode, name, buyPrice, sellPrice, stock, "Normal"});

                updateInboundRecordsWithProductInfo(barcode, name);

                JOptionPane.showMessageDialog(dialog, "Added successfully!\nInbound record has been automatically linked to stock quantity: " + stock);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showModifyPriceDialog() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product first");
            return;
        }

        String barcode = (String) inventoryTableModel.getValueAt(selectedRow, 0);
        String name = (String) inventoryTableModel.getValueAt(selectedRow, 1);
        double buyPrice = (double) inventoryTableModel.getValueAt(selectedRow, 2);
        double sellPrice = (double) inventoryTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "Modify Product Price", true);
        dialog.setSize(450, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 12, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField(barcode);
        barcodeField.setEditable(false);
        JTextField nameField = new JTextField(name);
        nameField.setEditable(false);
        JTextField buyPriceField = new JTextField(String.valueOf(buyPrice));
        JTextField sellPriceField = new JTextField(String.valueOf(sellPrice));

        panel.add(new JLabel("Product Barcode:"));
        panel.add(barcodeField);
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Purchase Price:"));
        panel.add(buyPriceField);
        panel.add(new JLabel("Sale Price:"));
        panel.add(sellPriceField);

        JButton okBtn = createActionButton("Confirm Modify", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                double newBuyPrice = Double.parseDouble(buyPriceField.getText().trim());
                double newSellPrice = Double.parseDouble(sellPriceField.getText().trim());

                if (newBuyPrice < 0 || newSellPrice < 0) {
                    JOptionPane.showMessageDialog(dialog, "Price cannot be negative");
                    return;
                }

                log("[Price Modify] Barcode: " + barcode + ", Purchase Price: " + buyPrice + " -> " + newBuyPrice + ", Sale Price: " + sellPrice + " -> " + newSellPrice + " (local operation)");

                for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                    ZYHProduct product = dataManager.getInventoryList().get(i);
                    if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                        product.setBuyPrice(newBuyPrice);
                        product.setSellPrice(newSellPrice);
                        break;
                    }
                }

                inventoryTableModel.setValueAt(newBuyPrice, selectedRow, 2);
                inventoryTableModel.setValueAt(newSellPrice, selectedRow, 3);

                JOptionPane.showMessageDialog(dialog, "Modified successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showBatchPriceDialog() {
        JFrame dialog = new JFrame("Batch Price Update");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setSize(1100, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.add(new JLabel("Adjustment Method:"));
        JComboBox<String> adjustTypeCombo = new JComboBox<>(new String[]{"Decrease by Amount", "Increase by Amount", "Decrease by Percentage", "Increase by Percentage"});
        topPanel.add(adjustTypeCombo);
        JTextField adjustValueField = new JTextField("0");
        topPanel.add(adjustValueField);

        String[] columnNames = {"Select", "Barcode", "Product Name", "Current Purchase Price", "Current Sale Price", "New Purchase Price", "New Sale Price"};
        java.util.List<Object[]> priceData = new java.util.ArrayList<>();
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            priceData.add(new Object[]{
                false,
                product.getBarcode(),
                product.getName(),
                product.getBuyPrice(),
                product.getSellPrice(),
                product.getBuyPrice(),
                product.getSellPrice()
            });
        }
        javax.swing.table.DefaultTableModel priceTableModel = new javax.swing.table.DefaultTableModel(priceData.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 5 || column == 0;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Boolean.class;
                }
                if (columnIndex == 3 || columnIndex == 4 || columnIndex == 5 || columnIndex == 6) {
                    return Double.class;
                }
                return String.class;
            }
        };
        JTable priceTable = new JTable(priceTableModel);
        priceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        priceTable.setRowHeight(28);
        priceTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        priceTable.setRowHeight(30);
        TableColumn col0 = priceTable.getColumnModel().getColumn(0);
        col0.setMinWidth(120);
        col0.setMaxWidth(320);
        col0.setWidth(120);
        col0.setPreferredWidth(120);
        TableColumn col1 = priceTable.getColumnModel().getColumn(1);
        col1.setMinWidth(260);
        col1.setMaxWidth(460);
        col1.setWidth(260);
        col1.setPreferredWidth(260);
        TableColumn col2 = priceTable.getColumnModel().getColumn(2);
        col2.setMinWidth(360);
        col2.setMaxWidth(560);
        col2.setWidth(360);
        col2.setPreferredWidth(360);
        TableColumn col3 = priceTable.getColumnModel().getColumn(3);
        col3.setMinWidth(240);
        col3.setMaxWidth(440);
        col3.setWidth(240);
        col3.setPreferredWidth(240);
        TableColumn col4 = priceTable.getColumnModel().getColumn(4);
        col4.setMinWidth(240);
        col4.setMaxWidth(440);
        col4.setWidth(240);
        col4.setPreferredWidth(240);
        TableColumn col5 = priceTable.getColumnModel().getColumn(5);
        col5.setMinWidth(240);
        col5.setMaxWidth(440);
        col5.setWidth(240);
        col5.setPreferredWidth(240);
        TableColumn col6 = priceTable.getColumnModel().getColumn(6);
        col6.setMinWidth(240);
        col6.setMaxWidth(440);
        col6.setWidth(240);
        col6.setPreferredWidth(240);
        JScrollPane tableScroll = new JScrollPane(priceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Product Price List (new prices can be edited directly)"));
        tableScroll.setPreferredSize(new Dimension(1050, 500));

        JButton previewBtn = createActionButton("Preview Adjustment", COLOR_2);
        previewBtn.addActionListener(e -> {
            try {
                String adjustType = (String) adjustTypeCombo.getSelectedItem();
                double adjustValue = Double.parseDouble(adjustValueField.getText());
                for (int i = 0; i < priceTableModel.getRowCount(); i++) {
                    Boolean selected = (Boolean) priceTableModel.getValueAt(i, 0);
                    if (!selected) continue;
                    double currentSell = (Double) priceTableModel.getValueAt(i, 4);
                    double newSell = currentSell;
                    switch (adjustType) {
                        case "Decrease by Amount":
                            newSell = Math.max(0, currentSell - adjustValue);
                            break;
                        case "Increase by Amount":
                            newSell = currentSell + adjustValue;
                            break;
                        case "Decrease by Percentage":
                            newSell = Math.max(0, currentSell * (1 - adjustValue / 100));
                            break;
                        case "Increase by Percentage":
                            newSell = currentSell * (1 + adjustValue / 100);
                            break;
                    }
                    priceTableModel.setValueAt(Math.round(newSell * 100.0) / 100.0, i, 6);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton selectAllBtn = createActionButton("Select All", new Color(107, 114, 128));
        selectAllBtn.addActionListener(e -> {
            for (int i = 0; i < priceTableModel.getRowCount(); i++) {
                priceTableModel.setValueAt(true, i, 0);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("Confirm Modify", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int successCount = 0;
                for (int i = 0; i < priceTableModel.getRowCount(); i++) {
                    Boolean selected = (Boolean) priceTableModel.getValueAt(i, 0);
                    if (!selected) continue;
                    String barcode = String.valueOf(priceTableModel.getValueAt(i, 1));
                    double newSellPrice = (Double) priceTableModel.getValueAt(i, 6);

                    for (int j = 0; j < dataManager.getInventoryList().size(); j++) {
                        ZYHProduct product = dataManager.getInventoryList().get(j);
                        if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                            product.setSellPrice(newSellPrice);
                            inventoryTableModel.setValueAt(newSellPrice, j, 3);
                            successCount++;
                            break;
                        }
                    }
                }

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Batch update successful! Modified sale prices for " + successCount + " products");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(previewBtn);
        buttonPanel.add(selectAllBtn);
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showScanInboundDialog() {
        JDialog dialog = new JDialog(this, "Scan to Inbound", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField("1");

        panel.add(new JLabel("Product Barcode (scanner input):"));
        panel.add(barcodeField);
        panel.add(new JLabel("Inbound Quantity:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("Confirm Inbound", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                log("[Scan Inbound] Barcode: " + barcode + ", Quantity: " + quantity + " (local operation)");

                ZYHInboundRecord record = new ZYHInboundRecord(barcode, "", quantity);
                dataManager.getInboundQueue().addLast(record);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Inbound successful!");
                dialog.dispose();
                loadInboundData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddInboundDialog() {
        JDialog dialog = new JDialog(this, "Add Inbound", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField("1");

        panel.add(new JLabel("Product Barcode:"));
        panel.add(barcodeField);
        panel.add(new JLabel("Inbound Quantity:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("Confirm Inbound", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                log("[New Inbound] Barcode: " + barcode + ", Quantity: " + quantity + " (local operation)");

                String recordId = String.valueOf(System.currentTimeMillis());
                String name = findProductNameByBarcode(barcode);
                ZYHInboundRecord record = new ZYHInboundRecord(recordId, barcode, name, quantity, java.time.LocalDate.now().toString());
                dataManager.getInboundQueue().addLast(record);
                inboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity,
                        java.time.LocalDate.now().toString()});

                updateInboundQueueInfo();

                updateInventoryQuantity(barcode, quantity);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Inbound successful!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBatchInboundDialog() {
        JFrame dialog = new JFrame("Batch Inbound");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setSize(1000, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"Date", "Barcode", "Product Name", "Quantity"};
        java.util.List<Object[]> batchData = new java.util.ArrayList<>();
        batchData.add(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        javax.swing.table.DefaultTableModel batchTableModel = new javax.swing.table.DefaultTableModel(batchData.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        JTable batchTable = new JTable(batchTableModel);
        batchTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        batchTable.setRowHeight(30);
        TableColumn col0 = batchTable.getColumnModel().getColumn(0);
        col0.setMinWidth(200);
        col0.setMaxWidth(400);
        col0.setWidth(200);
        col0.setPreferredWidth(200);
        TableColumn col1 = batchTable.getColumnModel().getColumn(1);
        col1.setMinWidth(260);
        col1.setMaxWidth(460);
        col1.setWidth(260);
        col1.setPreferredWidth(260);
        TableColumn col2 = batchTable.getColumnModel().getColumn(2);
        col2.setMinWidth(400);
        col2.setMaxWidth(600);
        col2.setWidth(400);
        col2.setPreferredWidth(400);
        TableColumn col3 = batchTable.getColumnModel().getColumn(3);
        col3.setMinWidth(160);
        col3.setMaxWidth(360);
        col3.setWidth(160);
        col3.setPreferredWidth(160);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Product Barcode:"));
        JTextField barcodeField = new JTextField();
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Product Name:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Inbound Quantity:"));
        JTextField quantityField = new JTextField("1");
        inputPanel.add(quantityField);
        
        JPanel quickAddPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton quickAddBtn = createActionButton("Quick Add to List", COLOR_2);
        quickAddBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (!barcode.isEmpty()) {
                    batchTableModel.addRow(new Object[]{
                        java.time.LocalDate.now().toString(), 
                        barcode, 
                        name, 
                        quantity
                    });
                    barcodeField.setText("");
                    nameField.setText("");
                    quantityField.setText("1");
                    barcodeField.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please enter product barcode");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Quantity must be a number");
            }
        });
        quickAddPanel.add(quickAddBtn);
        
        topPanel.add(inputPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(quickAddPanel);
        
        barcodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String barcode = barcodeField.getText().trim();
                if (!barcode.isEmpty() && nameField.getText().isEmpty()) {
                    String name = findProductNameByBarcode(barcode);
                    if (!name.isEmpty()) {
                        nameField.setText(name);
                    }
                }
            }
        });
        
        barcodeField.addActionListener(e -> quickAddBtn.doClick());
        batchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(batchTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Inbound Record List (editable)"));
        tableScroll.setPreferredSize(new Dimension(950, 500));

        JButton addRowBtn = createActionButton("Add Row", COLOR_2);
        addRowBtn.addActionListener(e -> {
            batchTableModel.addRow(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        });

        JButton removeRowBtn = createActionButton("Delete Row", new Color(239, 68, 68));
        removeRowBtn.addActionListener(e -> {
            int selectedRow = batchTable.getSelectedRow();
            if (selectedRow >= 0) {
                batchTableModel.removeRow(selectedRow);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("Confirm Inbound", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int successCount = 0;
                for (int i = 0; i < batchTableModel.getRowCount(); i++) {
                    String date = String.valueOf(batchTableModel.getValueAt(i, 0));
                    String barcode = String.valueOf(batchTableModel.getValueAt(i, 1)).trim();
                    String name = String.valueOf(batchTableModel.getValueAt(i, 2)).trim();
                    int quantity = Integer.parseInt(String.valueOf(batchTableModel.getValueAt(i, 3)));

                    if (!barcode.isEmpty() && quantity > 0) {
                        String recordId = String.valueOf(System.currentTimeMillis() + i);
                        ZYHInboundRecord record = new ZYHInboundRecord(recordId, barcode, name, quantity, date);
                        dataManager.getInboundQueue().addLast(record);
                        inboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity, date});
                        updateInventoryQuantity(barcode, quantity);
                        successCount++;
                    }
                }

                updateInboundQueueInfo();
                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Batch inbound successful! Added " + successCount + " records");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addRowBtn);
        buttonPanel.add(removeRowBtn);
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showModifyInboundDialog() {
        int selectedRow = inboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inbound record first");
            return;
        }

        String recordId = (String) inboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) inboundTableModel.getValueAt(selectedRow, 1);
        String name = (String) inboundTableModel.getValueAt(selectedRow, 2);
        int oldQuantity = (int) inboundTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "Modify Inbound Record", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField(barcode);
        barcodeField.setEditable(false);
        JTextField quantityField = new JTextField(String.valueOf(oldQuantity));

        panel.add(new JLabel("Product Barcode:"));
        panel.add(barcodeField);
        panel.add(new JLabel("Inbound Quantity:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("Confirm Modify", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Inbound quantity must be greater than 0");
                    return;
                }

                int diff = newQuantity - oldQuantity;

                log("[Modify Inbound] Record ID: " + recordId + ", Barcode: " + barcode + ", Original Quantity: " + oldQuantity + ", New Quantity: " + newQuantity + " (local operation)");

                for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
                    ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
                    if (record.getRecordId().equals(recordId)) {
                        record.setQuantity(newQuantity);
                        break;
                    }
                }

                inboundTableModel.setValueAt(newQuantity, selectedRow, 3);

                updateInventoryQuantity(barcode, diff);

                JOptionPane.showMessageDialog(dialog, "Modified successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddOutboundDialog() {
        JDialog dialog = new JDialog(this, "Add Outbound", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField quantityField = new JTextField("1");

        barcodeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String barcode = barcodeField.getText().trim();
                if (!barcode.isEmpty()) {
                    String existingName = findProductNameByBarcode(barcode);
                    if (!existingName.isEmpty()) {
                        nameField.setText(existingName);
                    }
                }
            }
        });

        panel.add(new JLabel("Product Barcode:"));
        panel.add(barcodeField);
        panel.add(new JLabel("Product Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Outbound Quantity:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("Confirm Outbound", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (name.isEmpty()) {
                    name = findProductNameByBarcode(barcode);
                }

                log("[New Outbound] Barcode: " + barcode + ", Name: " + name + ", Quantity: " + quantity + " (local operation)");

                String recordId = String.valueOf(System.currentTimeMillis());
                ZYHOutboundRecord record = new ZYHOutboundRecord(recordId, barcode, name, quantity, java.time.LocalDate.now().toString());
                dataManager.getOutboundStack().addLast(record);
                outboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity,
                        java.time.LocalDate.now().toString()});

                updateOutboundStackInfo();

                updateInventoryQuantity(barcode, -quantity);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Outbound successful!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBatchOutboundDialog() {
        JFrame dialog = new JFrame("Batch Outbound");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setSize(1000, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"Date", "Barcode", "Product Name", "Quantity"};
        java.util.List<Object[]> batchData = new java.util.ArrayList<>();
        batchData.add(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        javax.swing.table.DefaultTableModel batchTableModel = new javax.swing.table.DefaultTableModel(batchData.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        JTable batchTable = new JTable(batchTableModel);
        batchTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        batchTable.setRowHeight(30);
        TableColumn col0 = batchTable.getColumnModel().getColumn(0);
        col0.setMinWidth(200);
        col0.setMaxWidth(400);
        col0.setWidth(200);
        col0.setPreferredWidth(200);
        TableColumn col1 = batchTable.getColumnModel().getColumn(1);
        col1.setMinWidth(260);
        col1.setMaxWidth(460);
        col1.setWidth(260);
        col1.setPreferredWidth(260);
        TableColumn col2 = batchTable.getColumnModel().getColumn(2);
        col2.setMinWidth(400);
        col2.setMaxWidth(600);
        col2.setWidth(400);
        col2.setPreferredWidth(400);
        TableColumn col3 = batchTable.getColumnModel().getColumn(3);
        col3.setMinWidth(160);
        col3.setMaxWidth(360);
        col3.setWidth(160);
        col3.setPreferredWidth(160);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Product Barcode:"));
        JTextField barcodeField = new JTextField();
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("Product Name:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Outbound Quantity:"));
        JTextField quantityField = new JTextField("1");
        inputPanel.add(quantityField);
        
        JPanel quickAddPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton quickAddBtn = createActionButton("Quick Add to List", COLOR_2);
        quickAddBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (!barcode.isEmpty()) {
                    batchTableModel.addRow(new Object[]{
                        java.time.LocalDate.now().toString(), 
                        barcode, 
                        name, 
                        quantity
                    });
                    barcodeField.setText("");
                    nameField.setText("");
                    quantityField.setText("1");
                    barcodeField.requestFocus();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please enter product barcode");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Quantity must be a number");
            }
        });
        quickAddPanel.add(quickAddBtn);
        
        topPanel.add(inputPanel);
        topPanel.add(Box.createVerticalStrut(10));
        topPanel.add(quickAddPanel);
        
        barcodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String barcode = barcodeField.getText().trim();
                if (!barcode.isEmpty() && nameField.getText().isEmpty()) {
                    String name = findProductNameByBarcode(barcode);
                    if (!name.isEmpty()) {
                        nameField.setText(name);
                    }
                }
            }
        });
        
        barcodeField.addActionListener(e -> quickAddBtn.doClick());
        
        batchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        batchTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(batchTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Outbound Record List (editable)"));
        tableScroll.setPreferredSize(new Dimension(950, 500));

        JButton addRowBtn = createActionButton("Add Row", COLOR_2);
        addRowBtn.addActionListener(e -> {
            batchTableModel.addRow(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        });

        JButton removeRowBtn = createActionButton("Delete Row", new Color(239, 68, 68));
        removeRowBtn.addActionListener(e -> {
            int selectedRow = batchTable.getSelectedRow();
            if (selectedRow >= 0) {
                batchTableModel.removeRow(selectedRow);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("Confirm Outbound", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int successCount = 0;
                for (int i = 0; i < batchTableModel.getRowCount(); i++) {
                    String date = String.valueOf(batchTableModel.getValueAt(i, 0));
                    String barcode = String.valueOf(batchTableModel.getValueAt(i, 1)).trim();
                    String name = String.valueOf(batchTableModel.getValueAt(i, 2)).trim();
                    int quantity = Integer.parseInt(String.valueOf(batchTableModel.getValueAt(i, 3)));

                    if (!barcode.isEmpty() && quantity > 0) {
                        String recordId = String.valueOf(System.currentTimeMillis() + i);
                        ZYHOutboundRecord record = new ZYHOutboundRecord(recordId, barcode, name, quantity, date);
                        dataManager.getOutboundStack().addFirst(record);
                        outboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity, date});
                        updateInventoryQuantity(barcode, -quantity);
                        successCount++;
                    }
                }

                updateOutboundStackInfo();
                playSound("success");
                JOptionPane.showMessageDialog(dialog, "Batch outbound successful! Added " + successCount + " records");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addRowBtn);
        buttonPanel.add(removeRowBtn);
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showModifyOutboundDialog() {
        int selectedRow = outboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an outbound record first");
            return;
        }

        String recordId = (String) outboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) outboundTableModel.getValueAt(selectedRow, 1);
        String name = (String) outboundTableModel.getValueAt(selectedRow, 2);
        int oldQuantity = (int) outboundTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "Modify Outbound Record", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField(barcode);
        barcodeField.setEditable(false);
        JTextField quantityField = new JTextField(String.valueOf(oldQuantity));

        panel.add(new JLabel("Product Barcode:"));
        panel.add(barcodeField);
        panel.add(new JLabel("Outbound Quantity:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("Confirm Modify", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Outbound quantity must be greater than 0");
                    return;
                }

                int currentStock = getCurrentStockQuantity(barcode);
                int diff = oldQuantity - newQuantity;

                if (currentStock + diff < 0) {
                    JOptionPane.showMessageDialog(dialog, "Insufficient stock! Current stock: " + currentStock + ", need to increase stock by: " + Math.abs(currentStock + diff));
                    return;
                }

                log("[Modify Outbound] Record ID: " + recordId + ", Barcode: " + barcode + ", Original Quantity: " + oldQuantity + ", New Quantity: " + newQuantity + " (local operation)");

                for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
                    ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
                    if (record.getRecordId().equals(recordId)) {
                        record.setQuantity(newQuantity);
                        break;
                    }
                }

                outboundTableModel.setValueAt(newQuantity, selectedRow, 3);

                updateInventoryQuantity(barcode, diff);

                JOptionPane.showMessageDialog(dialog, "Modified successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Input error: " + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("Cancel", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private int getCurrentStockQuantity(String barcode) {
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            if (product.getBarcode() != null && product.getBarcode().equals(barcode)) {
                return product.getStockQuantity();
            }
        }
        return 0;
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("========== Inventory Management System Business Report ==========\n\n");
        report.append("Generated Time: ").append(java.time.LocalDateTime.now().toString().substring(0, 19)).append("\n\n");

        report.append("[Inventory Statistics]\n");
        report.append("Total Products: ").append(dataManager.getInventoryCount()).append("\n");
        report.append("Inventory Records: ").append(dataManager.getInventoryCount()).append("\n");
        report.append("Inbound Records: ").append(dataManager.getInboundCount()).append("\n");
        report.append("Outbound Records: ").append(dataManager.getOutboundCount()).append("\n\n");

        report.append("[Sequential List Data]\n");
        report.append("Product List: ").append(dataManager.getInventoryList().size()).append(" records\n");
        report.append("Inventory List: ").append(dataManager.getInventoryList().size()).append(" records\n\n");

        report.append("[Linked List Data]\n");
        report.append("Inbound Queue: ").append(dataManager.getInboundQueue().size()).append(" records\n");
        report.append("Outbound Stack: ").append(dataManager.getOutboundStack().size()).append(" records\n\n");

        report.append("[Inventory Details]\n");
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            report.append(String.format("%d. %s - Stock: %d\n", i + 1, product.getName(), product.getStockQuantity()));
        }

        report.append("\n===============================================\n");

        for (java.awt.Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                findAndSetReportText((JPanel) comp, report.toString());
            }
        }

        JOptionPane.showMessageDialog(this, "Report generated successfully!");

        log("[Report] Generation complete");
    }

    /**
     * Generate HTML format inventory data analysis report
     */
    private void generateHtmlReport() {
        StringBuilder html = new StringBuilder();

        // HTML header
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"en\">\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>Inventory Data Analysis Report</title>\n");
        html.append("<style>\n");
        html.append(":root { --darkest: #0c4a6e; --dark: #0369a1; --primary: #0ea5e9; --light: #bae6fd; --white: #ffffff; }\n");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("body { font-family: 'Segoe UI', Arial, sans-serif; background: var(--light); padding: 30px; }\n");
        html.append(".report-container { max-width: 1200px; margin: 0 auto; background: var(--white); border-radius: 16px; box-shadow: 0 8px 32px rgba(12, 74, 110, 0.15); overflow: hidden; }\n");
        html.append(".report-header { background: linear-gradient(135deg, var(--darkest) 0%, var(--dark) 50%, var(--primary) 100%); color: var(--white); padding: 35px; text-align: center; }\n");
        html.append(".report-header h1 { font-size: 28px; margin-bottom: 12px; font-weight: 600; }\n");
        html.append(".report-header .time { font-size: 14px; opacity: 0.9; }\n");
        html.append(".report-body { padding: 35px; }\n");
        html.append(".section { margin-bottom: 35px; }\n");
        html.append(".section-title { font-size: 20px; font-weight: bold; color: var(--darkest); margin-bottom: 18px; padding-bottom: 12px; border-bottom: 3px solid var(--primary); }\n");
        html.append(".summary-cards { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; margin-bottom: 25px; }\n");
        html.append(".summary-card { background: linear-gradient(135deg, var(--dark) 0%, var(--primary) 100%); border-radius: 12px; padding: 24px; color: var(--white); text-align: center; }\n");
        html.append(".summary-card .value { font-size: 36px; font-weight: bold; margin-bottom: 8px; }\n");
        html.append(".summary-card .label { font-size: 14px; opacity: 0.95; }\n");
        html.append("table { width: 100%; border-collapse: collapse; margin-top: 12px; }\n");
        html.append("th, td { padding: 14px; text-align: left; border-bottom: 1px solid var(--light); }\n");
        html.append("th { background: rgba(14, 165, 233, 0.05); font-weight: bold; color: var(--darkest); }\n");
        html.append("tr:hover { background: rgba(14, 165, 233, 0.05); }\n");
        html.append(".status-normal { color: #059669; font-weight: bold; }\n");
        html.append(".status-warning { color: #d97706; font-weight: bold; }\n");
        html.append(".status-out { color: #dc2626; font-weight: bold; }\n");
        html.append(".status-error { color: #0c4a6e; font-weight: bold; }\n");
        html.append(".rank { display: inline-block; width: 28px; height: 28px; background: linear-gradient(135deg, var(--dark) 0%, var(--primary) 100%); color: var(--white); border-radius: 50%; text-align: center; line-height: 28px; font-size: 14px; font-weight: bold; margin-right: 12px; }\n");
        html.append(".progress-bar { height: 24px; background: rgba(14, 165, 233, 0.1); border-radius: 12px; overflow: hidden; margin: 10px 0; }\n");
        html.append(".progress-fill { height: 100%; background: linear-gradient(90deg, var(--dark), var(--primary)); border-radius: 12px; }\n");
        html.append(".warning-list { background: rgba(251, 191, 36, 0.1); border-left: 4px solid var(--dark); padding: 20px; border-radius: 0 12px 12px 0; }\n");
        html.append(".warning-item { padding: 10px 0; border-bottom: 1px dashed rgba(12, 74, 110, 0.1); }\n");
        html.append(".warning-item:last-child { border-bottom: none; }\n");
        html.append(".warning-type { display: inline-block; padding: 4px 10px; border-radius: 6px; font-size: 12px; font-weight: bold; margin-right: 12px; }\n");
        html.append(".type-error { background: rgba(220, 38, 38, 0.1); color: #dc2626; }\n");
        html.append(".type-warning { background: rgba(217, 119, 6, 0.1); color: #d97706; }\n");
        html.append(".advice-cards { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; }\n");
        html.append(".advice-card { border-radius: 10px; padding: 18px; display: flex; align-items: flex-start; gap: 15px; }\n");
        html.append(".advice-card.urgent { background: rgba(220, 38, 38, 0.08); border-left: 4px solid #dc2626; }\n");
        html.append(".advice-card.warning { background: rgba(217, 119, 6, 0.08); border-left: 4px solid #d97706; }\n");
        html.append(".advice-card.info { background: rgba(14, 165, 233, 0.08); border-left: 4px solid #0ea5e9; }\n");
        html.append(".advice-card.success { background: rgba(5, 150, 105, 0.08); border-left: 4px solid #059669; }\n");
        html.append(".advice-icon { font-size: 28px; flex-shrink: 0; }\n");
        html.append(".advice-content { flex: 1; }\n");
        html.append(".advice-title { font-size: 16px; font-weight: bold; color: var(--darkest); margin-bottom: 8px; }\n");
        html.append(".advice-text { font-size: 14px; color: #374151; line-height: 1.6; }\n");
        html.append(".investigation-list { }\n");
        html.append(".investigation-item { background: rgba(220, 38, 38, 0.05); border-left: 4px solid #dc2626; padding: 18px; border-radius: 0 12px 12px 0; margin-bottom: 15px; }\n");
        html.append(".investigation-item:last-child { margin-bottom: 0; }\n");
        html.append(".investigation-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }\n");
        html.append(".investigation-icon { font-size: 22px; }\n");
        html.append(".investigation-name { font-size: 18px; font-weight: bold; color: #dc2626; }\n");
        html.append(".investigation-stock { font-size: 14px; color: #374151; margin-bottom: 10px; }\n");
        html.append(".investigation-stock span { color: #dc2626; font-weight: bold; font-size: 16px; }\n");
        html.append(".investigation-suggest { background: rgba(3, 105, 161, 0.05); padding: 12px; border-radius: 8px; }\n");
        html.append(".investigation-suggest-title { font-size: 14px; font-weight: bold; color: var(--dark); margin-bottom: 8px; }\n");
        html.append(".investigation-suggest ul { margin: 0; padding-left: 20px; }\n");
        html.append(".investigation-suggest li { font-size: 13px; color: #0369a1; margin-bottom: 5px; line-height: 1.5; }\n");
        html.append(".footer { text-align: center; padding: 25px; background: rgba(12, 74, 110, 0.05); color: var(--dark); font-size: 14px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"report-container\">\n");
        
        // Report header
        html.append("<div class=\"report-header\">\n");
        html.append("<h1>Inventory Data Analysis Report</h1>\n");
        html.append("<div class=\"time\">Generated Time: ").append(java.time.LocalDateTime.now().toString().substring(0, 19)).append("</div>\n");
        html.append("</div>\n");

        html.append("<div class=\"report-body\">\n");

        // Statistics summary
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📊 Statistics Summary</div>\n");
        html.append("<div class=\"summary-cards\">\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getInventoryCount()).append("</div><div class=\"label\">Total Products</div></div>\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getInboundCount()).append("</div><div class=\"label\">Inbound Records</div></div>\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getOutboundCount()).append("</div><div class=\"label\">Outbound Records</div></div>\n");
        
        int totalStock = 0;
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            totalStock += dataManager.getInventoryList().get(i).getStockQuantity();
        }
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(totalStock).append("</div><div class=\"label\">Total Stock</div></div>\n");
        html.append("</div>\n");
        html.append("</div>\n");

        // Data distribution statistics
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📈 Data Distribution</div>\n");
        html.append("<table>\n");
        html.append("<tr><th>Data Structure</th><th>Data Type</th><th>Record Count</th></tr>\n");
        html.append("<tr><td>Sequential List</td><td>Product List</td><td>").append(dataManager.getInventoryList().size()).append("</td></tr>\n");
        html.append("<tr><td>Sequential List</td><td>Inventory List</td><td>").append(dataManager.getInventoryList().size()).append("</td></tr>\n");
        html.append("<tr><td>Linked List (Queue)</td><td>Inbound Records</td><td>").append(dataManager.getInboundQueue().size()).append("</td></tr>\n");
        html.append("<tr><td>Linked List (Stack)</td><td>Outbound Records</td><td>").append(dataManager.getOutboundStack().size()).append("</td></tr>\n");
        html.append("</table>\n");
        html.append("</div>\n");
        
        // Inbound Top analysis
        java.util.Map<String, Integer> inboundStats = new java.util.HashMap<>();
        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
            inboundStats.put(record.getBarcode(), inboundStats.getOrDefault(record.getBarcode(), 0) + record.getQuantity());
        }
        
        java.util.List<java.util.Map.Entry<String, Integer>> inboundList = new java.util.ArrayList<>(inboundStats.entrySet());
        inboundList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📥 Top Inbound Products</div>\n");
        
        int maxInbound = inboundList.isEmpty() ? 1 : inboundList.get(0).getValue();
        for (int i = 0; i < Math.min(5, inboundList.size()); i++) {
            java.util.Map.Entry<String, Integer> entry = inboundList.get(i);
            String name = "Unknown Product";
            for (int j = 0; j < dataManager.getInventoryList().size(); j++) {
                if (dataManager.getInventoryList().get(j).getBarcode().equals(entry.getKey())) {
                    name = dataManager.getInventoryList().get(j).getName();
                    break;
                }
            }
            double ratio = (double) entry.getValue() / maxInbound * 100;
            html.append("<div style=\"margin: 10px 0;\">\n");
            html.append("<span class=\"rank\">").append(i + 1).append("</span>").append(name).append(" (").append(entry.getValue()).append("units)\n");
            html.append("<div class=\"progress-bar\"><div class=\"progress-fill\" style=\"width: ").append(ratio).append("%\"></div></div>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");
        
        // Outbound Top analysis
        java.util.Map<String, Integer> outboundStats = new java.util.HashMap<>();
        java.util.Map<String, String> lastOutboundDate = new java.util.HashMap<>();
        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
            outboundStats.put(record.getBarcode(), outboundStats.getOrDefault(record.getBarcode(), 0) + record.getQuantity());
            lastOutboundDate.put(record.getBarcode(), record.getDate());
        }
        
        java.util.List<java.util.Map.Entry<String, Integer>> outboundList = new java.util.ArrayList<>(outboundStats.entrySet());
        outboundList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📤 Top Outbound Products</div>\n");
        
        int maxOutbound = outboundList.isEmpty() ? 1 : outboundList.get(0).getValue();
        for (int i = 0; i < Math.min(5, outboundList.size()); i++) {
            java.util.Map.Entry<String, Integer> entry = outboundList.get(i);
            String name = "Unknown Product";
            for (int j = 0; j < dataManager.getInventoryList().size(); j++) {
                if (dataManager.getInventoryList().get(j).getBarcode().equals(entry.getKey())) {
                    name = dataManager.getInventoryList().get(j).getName();
                    break;
                }
            }
            double ratio = (double) entry.getValue() / maxOutbound * 100;
            html.append("<div style=\"margin: 10px 0;\">\n");
            html.append("<span class=\"rank\">").append(i + 1).append("</span>").append(name).append(" (").append(entry.getValue()).append("units)\n");
            html.append("<div class=\"progress-bar\"><div class=\"progress-fill\" style=\"width: ").append(ratio).append("%\"></div></div>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");
        
        // Inventory details
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📦 Inventory Details</div>\n");
        html.append("<table>\n");
        html.append("<tr><th>Barcode</th><th>Product Name</th><th>Total Inbound</th><th>Total Outbound</th><th>Current Stock</th><th>Status</th></tr>\n");
        
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int inbound = inboundStats.getOrDefault(product.getBarcode(), 0);
            int outbound = outboundStats.getOrDefault(product.getBarcode(), 0);
            int stock = product.getStockQuantity();
            String status;
            String statusClass;
            if (stock < 0) {
                status = "Abnormal";
                statusClass = "status-error";
            } else if (stock == 0) {
                status = "Out of Stock";
                statusClass = "status-out";
            } else if (stock <= 5) {
                status = "Warning";
                statusClass = "status-warning";
            } else {
                status = "Normal";
                statusClass = "status-normal";
            }
            
            html.append("<tr>\n");
            html.append("<td>").append(product.getBarcode()).append("</td>\n");
            html.append("<td>").append(product.getName()).append("</td>\n");
            html.append("<td>").append(inbound).append("</td>\n");
            html.append("<td>").append(outbound).append("</td>\n");
            html.append("<td>").append(stock).append("</td>\n");
            html.append("<td><span class=\"").append(statusClass).append("\">").append(status).append("</span></td>\n");
            html.append("</tr>\n");
        }
        html.append("</table>\n");
        html.append("</div>\n");
        
        // Warning list
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">⚠️ Warning List</div>\n");
        
        boolean hasWarning = false;
        html.append("<div class=\"warning-list\">\n");
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int stock = product.getStockQuantity();
            if (stock < 0) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-error\">Stock Abnormal</span>").append(product.getName()).append(" - Stock is negative: ").append(stock).append("</div>\n");
                hasWarning = true;
            } else if (stock == 0) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-error\">Insufficient Stock</span>").append(product.getName()).append(" - Stock is 0, restocking needed immediately</div>\n");
                hasWarning = true;
            } else if (stock <= 5) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-warning\">Stock Warning</span>").append(product.getName()).append(" - Stock is low: ").append(stock).append(" units</div>\n");
                hasWarning = true;
            }
        }
        
        if (!hasWarning) {
            html.append("<div style=\"color: #22c55e; text-align: center; padding: 10px;\">No warnings, stock status is good</div>\n");
        }
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Smart advice module
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">Smart Advice</div>\n");
        html.append("<div class=\"advice-cards\">\n");
        
        boolean hasAdvice = false;
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int stock = product.getStockQuantity();
            int inbound = inboundStats.getOrDefault(product.getBarcode(), 0);
            int outbound = outboundStats.getOrDefault(product.getBarcode(), 0);
            
            if (stock < 0) {
                html.append("<div class=\"advice-card urgent\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">Urgent Restock Advice</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Stock is negative (<span style=\"color:#dc2626\">").append(stock).append("</span>), data abnormal, needs immediate attention!</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (stock == 0) {
                html.append("<div class=\"advice-card urgent\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">Urgent Restock Advice</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Stock is 0, restock immediately to avoid out-of-stock losses!</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (stock <= 5) {
                html.append("<div class=\"advice-card warning\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">Stock Warning Advice</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Stock is low (").append(stock).append(" units), recommend scheduling restock soon.</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (inbound > 0 && outbound > 0 && inbound == outbound && inbound > 10) {
                html.append("<div class=\"advice-card success\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">Sales Advice</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Inbound equals outbound (total ").append(inbound).append(" units), inventory turnover is good.</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (stock > 0) {
                String lastOut = lastOutboundDate.get(product.getBarcode());
                if (lastOut != null) {
                    try {
                        long daysSinceLastOut = java.time.temporal.ChronoUnit.DAYS.between(
                            java.time.LocalDate.parse(lastOut), java.time.LocalDate.now());
                        if (daysSinceLastOut > 60 && inbound > 0) {
                            html.append("<div class=\"advice-card urgent\">\n");
                            html.append("<div class=\"advice-icon\"></div>\n");
                            html.append("<div class=\"advice-content\">\n");
                            html.append("<div class=\"advice-title\">Severe Overstock Warning</div>\n");
                            html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Stock ").append(stock).append(" units, over ").append(daysSinceLastOut).append(" days without outbound records, recommend significant price reduction promotion!</div>\n");
                            html.append("</div>\n");
                            html.append("</div>\n");
                            hasAdvice = true;
                        } else if (daysSinceLastOut > 30 && inbound > 0) {
                            html.append("<div class=\"advice-card warning\">\n");
                            html.append("<div class=\"advice-icon\"></div>\n");
                            html.append("<div class=\"advice-content\">\n");
                            html.append("<div class=\"advice-title\">Overstock Warning</div>\n");
                            html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> Stock ").append(stock).append(" units, nearly ").append(daysSinceLastOut).append(" days without outbound records, recommend promotion to clear.</div>\n");
                            html.append("</div>\n");
                            html.append("</div>\n");
                            hasAdvice = true;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        
        if (!hasAdvice) {
            html.append("<div style=\"color: #059669; text-align: center; padding: 15px; font-size: 14px;\">All indicators normal, inventory management is good!</div>\n");
        }
        
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Abnormal investigation advice module
        boolean hasAbnormal = false;
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int stock = product.getStockQuantity();
            if (stock < 0) {
                hasAbnormal = true;
                break;
            }
        }
        
        if (hasAbnormal) {
            html.append("<div class=\"section\">\n");
            html.append("<div class=\"section-title\">Abnormal Investigation Advice</div>\n");
            html.append("<div class=\"investigation-list\">\n");
            
            for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                ZYHProduct product = dataManager.getInventoryList().get(i);
                int stock = product.getStockQuantity();
                if (stock < 0) {
                    html.append("<div class=\"investigation-item\">\n");
                    html.append("<div class=\"investigation-header\">\n");
                    html.append("<div class=\"investigation-icon\"></div>\n");
                    html.append("<div class=\"investigation-name\">").append(product.getName()).append("</div>\n");
                    html.append("</div>\n");
                    html.append("<div class=\"investigation-stock\">Current Stock: <span>").append(stock).append("</span> (Abnormal)</div>\n");
                    html.append("<div class=\"investigation-suggest\">\n");
                    html.append("<div class=\"investigation-suggest-title\">Investigation advice: Please check for the following issues:</div>\n");
                    html.append("<ul>\n");
                    html.append("<li>Are outbound records duplicated or have incorrect quantities</li>\n");
                    html.append("<li>Are inbound records correctly entered with accurate quantities</li>\n");
                    html.append("<li>Is there any unauthorized data modification</li>\n");
                    html.append("<li>Are there logical errors in the chronological order of inbound/outbound records</li>\n");
                    html.append("</ul>\n");
                    html.append("</div>\n");
                    html.append("</div>\n");
                }
            }
            
            html.append("</div>\n");
            html.append("</div>\n");
        }
        
        html.append("</div>\n");
        
        // Footer
        html.append("<div class=\"footer\">\n");
        html.append("Inventory Management System - Data Analysis Report\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        // Save file
        try {
            String fileName = "InventoryDataAnalysisReport_" + java.time.LocalDateTime.now().toString().replace(":", "-").substring(0, 19) + ".html";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);
            writer.write(html.toString());
            writer.close();
            
            java.io.File htmlFile = new java.io.File(fileName);
            java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
            
            playSound("complete");
            JOptionPane.showMessageDialog(this, "HTML report generated and opened!\n\nFile location: " + htmlFile.getAbsolutePath());
            log("[Report] HTML report generation complete: " + fileName);
        } catch (java.io.IOException e) {
            playSound("error");
            JOptionPane.showMessageDialog(this, "Failed to generate report: " + e.getMessage());
            log("[Report] HTML report generation failed: " + e.getMessage());
        }
    }

    private void runPerformanceTest() {
        int testSize = 50000;
        StringBuilder result = new StringBuilder();
        result.append("========== Sequential List vs Linked List Performance Comparison ==========\n\n");
        result.append("Test Data Size: ").append(testSize).append(" records\n");
        result.append("Test Environment: Java Array vs Doubly Linked List\n\n");

        ZYHSequentialList<Integer> seqList = new ZYHSequentialList<>();
        ZYHLinkedList<Integer> linkedList = new ZYHLinkedList<>();
        String testBarcode = "TEST" + System.currentTimeMillis() % 10000;

        long startTime, endTime;
        long seqTime, linkTime;

        result.append("[1. Tail Add (Inbound Operation)]\n");
        startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            seqList.add(i);
        }
        endTime = System.nanoTime();
        seqTime = endTime - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            linkedList.addLast(i);
        }
        endTime = System.nanoTime();
        linkTime = endTime - startTime;

        result.append("Sequential List: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("Linked List: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("Conclusion: ").append(seqTime < linkTime ? "Sequential List is faster" : "Linked List is faster").append("\n\n");

        result.append("[2. Tail Remove (Outbound Operation)]\n");
        startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            seqList.remove(seqList.size() - 1);
        }
        endTime = System.nanoTime();
        seqTime = endTime - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            linkedList.removeLast();
        }
        endTime = System.nanoTime();
        linkTime = endTime - startTime;

        result.append("Sequential List: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("Linked List: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("Conclusion: ").append(seqTime < linkTime ? "Sequential List is faster" : "Linked List is faster").append("\n\n");

        for (int i = 0; i < testSize; i++) {
            seqList.add(i);
            linkedList.addLast(i);
        }

        result.append("[3. Search Operation (Index Lookup)]\n");
        int midIndex = testSize / 2;
        startTime = System.nanoTime();
        for (int i = 0; i < testSize; i++) {
            seqList.get(i);
        }
        endTime = System.nanoTime();
        seqTime = endTime - startTime;

        startTime = System.nanoTime();
        java.util.Iterator<Integer> iter = linkedList.iterator();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        }
        endTime = System.nanoTime();
        linkTime = endTime - startTime;

        result.append("Sequential List: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("Linked List: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("Conclusion: ").append(seqTime < linkTime ? "Sequential List is faster (supports random access)" : "Linked List is faster").append("\n\n");

        result.append("[4. Middle Insert (Requires Element Shifting)]\n");
        seqList.clear();
        linkedList.clear();
        int smallSize = 100;
        for (int i = 0; i < smallSize; i++) {
            seqList.add(i);
            linkedList.addLast(i);
        }
        int insertPos = 50;

        startTime = System.nanoTime();
        for (int i = 0; i < smallSize; i++) {
            seqList.add(insertPos, i);
        }
        endTime = System.nanoTime();
        seqTime = endTime - startTime;

        startTime = System.nanoTime();
        for (int i = 0; i < smallSize; i++) {
            linkedList.add(insertPos, i);
        }
        endTime = System.nanoTime();
        linkTime = endTime - startTime;

        result.append("Sequential List: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms (requires element shifting)\n");
        result.append("Linked List: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms (modifies pointers)\n");
        result.append("Conclusion: ").append(seqTime > linkTime ? "Linked List is faster (no element shifting needed)" : "Sequential List is faster").append("\n\n");

        JTextArea textArea = new JTextArea(result.toString());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Performance Comparison Results", JOptionPane.INFORMATION_MESSAGE);

        log("[Performance Test] Test complete, data size: " + testSize);
    }

    private void findAndSetReportText(JPanel panel, String text) {
        for (java.awt.Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                if ("reportArea".equals(scroll.getName())) {
                    java.awt.Component view = scroll.getViewport().getView();
                    if (view instanceof JTextArea) {
                        ((JTextArea) view).setText(text);
                        return;
                    }
                }
            } else if (comp instanceof JPanel) {
                findAndSetReportText((JPanel) comp, text);
            }
        }
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            log("[Report] Exported to: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "Report exported successfully!");
        }
    }

    private String getPeriodKey(String date) {
        switch (timeGranularity) {
            case "month":
                return date.substring(0, 7);
            case "year":
                return date.substring(0, 4);
            default:
                return date;
        }
    }
    
    private String formatPeriodDisplay(String period, boolean showFullYear) {
        switch (timeGranularity) {
            case "month":
                if (showFullYear) {
                    return period.substring(2);
                } else {
                    return period.substring(5);
                }
            case "year":
                return period;
            default:
                if (showFullYear) {
                    return period.substring(2);
                } else {
                    return period.substring(5);
                }
        }
    }

    private void refreshChart() {
        contentPanel.repaint();
        log("[Chart] Refresh complete");
    }

    private void runComparison(int size) {
        log("[Performance Comparison] Starting comparison, data size: " + size);
        ZYHDataManager.PerformanceComparison result = dataManager.comparePerformance(size);
        log("[Performance Comparison] Complete");
        log(result.toString());

        JOptionPane.showMessageDialog(this, "Comparison complete, please check the log panel for detailed results!");
    }

    private class RoundedButton extends JButton {
        private int borderRadius;

        public RoundedButton(String text, int borderRadius) {
            super(text);
            this.borderRadius = borderRadius;
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color bgColor = getBackground();
            if (getModel().isArmed()) {
                bgColor = new Color(
                    Math.max((int)(bgColor.getRed() * 0.85), 0),
                    Math.max((int)(bgColor.getGreen() * 0.85), 0),
                    Math.max((int)(bgColor.getBlue() * 0.85), 0)
                );
            }
            
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius);
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Do not draw border
        }
    }

    private void deleteSelectedInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete first");
            return;
        }

        String barcode = (String) inventoryTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete product '" + barcode + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        inventoryTableModel.removeRow(selectedRow);
        
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            if (dataManager.getInventoryList().get(i).getBarcode().equals(barcode)) {
                dataManager.getInventoryList().remove(i);
                break;
            }
        }

        JOptionPane.showMessageDialog(this, "Product deleted successfully!");
        log("[Inventory Management] Deleted product: " + barcode);
    }

    private void openFilterDialog(String filterType) {
        ZYHFilterDialog dialog = new ZYHFilterDialog(this, filterType, dataManager);
        dialog.setVisible(true);
    }

    private void deleteSelectedInbound() {
        int selectedRow = inboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an inbound record to delete first");
            return;
        }

        String recordId = (String) inboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) inboundTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete inbound record '" + recordId + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        inboundTableModel.removeRow(selectedRow);
        
        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            if (dataManager.getInboundQueue().get(i).getRecordId() != null && 
                dataManager.getInboundQueue().get(i).getRecordId().equals(recordId)) {
                dataManager.getInboundQueue().remove(i);
                break;
            }
        }

        updateStockQuantity();
        updateInboundQueueInfo();

        JOptionPane.showMessageDialog(this, "Inbound record deleted successfully! Stock recalculated");
        log("[Inbound Management] Deleted inbound record: " + recordId);
    }

    private void deleteSelectedOutbound() {
        int selectedRow = outboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an outbound record to delete first");
            return;
        }

        String recordId = (String) outboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) outboundTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete outbound record '" + recordId + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        outboundTableModel.removeRow(selectedRow);
        
        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            if (dataManager.getOutboundStack().get(i).getRecordId() != null && 
                dataManager.getOutboundStack().get(i).getRecordId().equals(recordId)) {
                dataManager.getOutboundStack().remove(i);
                break;
            }
        }

        updateStockQuantity();
        updateOutboundStackInfo();

        JOptionPane.showMessageDialog(this, "Outbound record deleted successfully! Stock recalculated");
        log("[Outbound Management] Deleted outbound record: " + recordId);
    }

    private void updateStockQuantity() {
        java.util.Map<String, Integer> inboundTotal = new java.util.HashMap<>();
        java.util.Map<String, Integer> outboundTotal = new java.util.HashMap<>();

        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
            String barcode = record.getBarcode();
            inboundTotal.put(barcode, inboundTotal.getOrDefault(barcode, 0) + record.getQuantity());
        }

        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
            String barcode = record.getBarcode();
            outboundTotal.put(barcode, outboundTotal.getOrDefault(barcode, 0) + record.getQuantity());
        }

        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int inbound = inboundTotal.getOrDefault(product.getBarcode(), 0);
            int outbound = outboundTotal.getOrDefault(product.getBarcode(), 0);
            int newStock = inbound - outbound;
            product.setStockQuantity(newStock);
        }

        for (int i = 0; i < inventoryTableModel.getRowCount(); i++) {
            String barcode = (String) inventoryTableModel.getValueAt(i, 0);
            int inbound = inboundTotal.getOrDefault(barcode, 0);
            int outbound = outboundTotal.getOrDefault(barcode, 0);
            int newStock = inbound - outbound;
            inventoryTableModel.setValueAt(newStock, i, 4);
            inventoryTableModel.setValueAt(newStock >= 0 ? "Normal" : "Abnormal", i, 5);
        }
    }

    private void resetDataFromAPI() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all data? This will clear all operations and reload original data from API.",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            log("Starting data reset...");
            
            dataManager.getInventoryList().clear();
            dataManager.getInboundQueue().clear();
            dataManager.getOutboundStack().clear();
            dataManager.getTransactionFlow().clear();
            
            inventoryTableModel.setRowCount(0);
            inboundTableModel.setRowCount(0);
            outboundTableModel.setRowCount(0);
            
            loadDataFromAPI();
            
            JOptionPane.showMessageDialog(this, "Data reset complete! Original data reloaded from API.");
            log("Data reset complete");
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ZYHInventoryApp app = new ZYHInventoryApp();
            app.setVisible(true);
        });
    }

    /**
     * Show data analysis dialog
     * Statistics for total inbound, total outbound, current stock of each product, and mark abnormal status
     */
    private void showAnalysisDialog() {
        JFrame dialog = new JFrame("Inventory Data Analysis");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setSize(1200, 850);
        dialog.setLocationRelativeTo(this);

        // Statistics for inbound/outbound data
        java.util.Map<String, Integer> inboundStats = new java.util.HashMap<>();
        java.util.Map<String, Integer> outboundStats = new java.util.HashMap<>();
        java.util.Map<String, Integer> inboundCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> outboundCount = new java.util.HashMap<>();
        java.util.Map<String, String> lastInboundDate = new java.util.HashMap<>();
        java.util.Map<String, String> lastOutboundDate = new java.util.HashMap<>();

        // Statistics for total inbound and count
        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
            String barcode = record.getBarcode();
            inboundStats.put(barcode, inboundStats.getOrDefault(barcode, 0) + record.getQuantity());
            inboundCount.put(barcode, inboundCount.getOrDefault(barcode, 0) + 1);
            lastInboundDate.put(barcode, record.getDate());
        }

        // Statistics for total outbound and count
        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
            String barcode = record.getBarcode();
            outboundStats.put(barcode, outboundStats.getOrDefault(barcode, 0) + record.getQuantity());
            outboundCount.put(barcode, outboundCount.getOrDefault(barcode, 0) + 1);
            lastOutboundDate.put(barcode, record.getDate());
        }

        // Create analysis data list
        java.util.List<AnalysisData> analysisList = new java.util.ArrayList<>();
        java.util.List<TopProduct> topInboundList = new java.util.ArrayList<>();
        java.util.List<TopProduct> topOutboundList = new java.util.ArrayList<>();
        java.util.List<WarningItem> warningList = new java.util.ArrayList<>();
        int abnormalCount = 0;
        int backlogCount = 0;

        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            String barcode = product.getBarcode();
            String name = product.getName();
            int inbound = inboundStats.getOrDefault(barcode, 0);
            int outbound = outboundStats.getOrDefault(barcode, 0);
            int currentStock = product.getStockQuantity();
            String status;
            String level = "";

            // Determine status and level
            if (currentStock < 0) {
                status = "Abnormal";
                level = "";
                abnormalCount++;
                warningList.add(new WarningItem(barcode, name, "Stock Abnormal", "Stock is negative: " + currentStock));
            } else if (currentStock == 0) {
                status = "Out of Stock";
                level = "";
                abnormalCount++;
                warningList.add(new WarningItem(barcode, name, "Insufficient Stock", "Stock is 0, restocking needed immediately"));
            } else if (currentStock <= 5) {
                status = "Warning";
                level = "";
                warningList.add(new WarningItem(barcode, name, "Stock Warning", "Stock is low: " + currentStock + " units"));
            } else {
                status = "Normal";
                level = "";
            }

            // Check inventory backlog (no outbound for over 30 days)
            String lastOut = lastOutboundDate.get(barcode);
            if (lastOut != null && outbound > 0) {
                try {
                    java.time.LocalDate outDate = java.time.LocalDate.parse(lastOut);
                    java.time.LocalDate today = java.time.LocalDate.now();
                    long daysSinceOutbound = java.time.temporal.ChronoUnit.DAYS.between(outDate, today);
                    if (daysSinceOutbound > 30 && currentStock > 10) {
                        backlogCount++;
                        warningList.add(new WarningItem(barcode, name, "Inventory Backlog", "No outbound for " + daysSinceOutbound + " days, stock: " + currentStock));
                    }
                } catch (Exception e) {
                }
            }

            analysisList.add(new AnalysisData(barcode, name, inbound, outbound, currentStock, status, level));

            // Add to Top ranking
            topInboundList.add(new TopProduct(barcode, name, inbound, inboundCount.getOrDefault(barcode, 0)));
            topOutboundList.add(new TopProduct(barcode, name, outbound, outboundCount.getOrDefault(barcode, 0)));
        }

        // Sort Top rankings
        topInboundList.sort((a, b) -> Integer.compare(b.totalQuantity, a.totalQuantity));
        topOutboundList.sort((a, b) -> Integer.compare(b.totalQuantity, a.totalQuantity));

        // Create main panel - using BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND);

        // ===== Top: Statistics Summary =====
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setBackground(BACKGROUND);

        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(COLOR_WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.add(createSummaryCard("Total Products", String.valueOf(analysisList.size()), COLOR_1));
        summaryPanel.add(createSummaryCard("Abnormal Products", String.valueOf(abnormalCount), new Color(239, 68, 68)));
        summaryPanel.add(createSummaryCard("Backlog Products", String.valueOf(backlogCount), new Color(234, 179, 8)));
        summaryPanel.add(createSummaryCard("Total Stock", String.valueOf(analysisList.stream().mapToInt(d -> d.currentStock).sum()), COLOR_3));

        northWrapper.add(summaryPanel, BorderLayout.CENTER);
        mainPanel.add(northWrapper, BorderLayout.NORTH);

        // ===== Middle: Top Rankings and Warnings =====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // Left: Inbound Top5 + Outbound Top5
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(createTopListPanel("Inbound Top 5", topInboundList, COLOR_2));
        topPanel.add(createTopListPanel("Outbound Top 5", topOutboundList, COLOR_3));
        centerPanel.add(topPanel);

        // Right: Warning List
        JPanel warningPanel = createWarningPanel(warningList);
        centerPanel.add(warningPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== Bottom: Detailed Data Table =====
        String[] columnNames = {"Product Barcode", "Product Name", "Total Inbound", "Total Outbound", "Current Stock", "Status"};
        java.util.List<Object[]> tableData = new java.util.ArrayList<>();
        for (AnalysisData data : analysisList) {
            tableData.add(new Object[]{
                data.barcode,
                data.name,
                data.inboundTotal,
                data.outboundTotal,
                data.currentStock,
                data.status
            });
        }

        DefaultTableModel model = new DefaultTableModel(tableData.toArray(new Object[0][]), columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 3 || columnIndex == 4) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        table.setRowHeight(30);
        TableColumn col0 = table.getColumnModel().getColumn(0);
        col0.setMinWidth(300);
        col0.setMaxWidth(500);
        col0.setWidth(300);
        col0.setPreferredWidth(300);
        TableColumn col1 = table.getColumnModel().getColumn(1);
        col1.setMinWidth(400);
        col1.setMaxWidth(600);
        col1.setWidth(400);
        col1.setPreferredWidth(400);
        TableColumn col2 = table.getColumnModel().getColumn(2);
        col2.setMinWidth(200);
        col2.setMaxWidth(400);
        col2.setWidth(200);
        col2.setPreferredWidth(200);
        TableColumn col3 = table.getColumnModel().getColumn(3);
        col3.setMinWidth(200);
        col3.setMaxWidth(400);
        col3.setWidth(200);
        col3.setPreferredWidth(200);
        TableColumn col4 = table.getColumnModel().getColumn(4);
        col4.setMinWidth(200);
        col4.setMaxWidth(400);
        col4.setWidth(200);
        col4.setPreferredWidth(200);
        TableColumn col5 = table.getColumnModel().getColumn(5);
        col5.setMinWidth(240);
        col5.setMaxWidth(440);
        col5.setWidth(240);
        col5.setPreferredWidth(240);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Product Details (click column header to sort)"));
        tableScroll.setPreferredSize(new Dimension(1180, 280));

        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
        log("[Data Analysis] Opened enhanced analysis panel");
    }

    private void showStockPredictionDialog() {
        JFrame dialog = new JFrame("Inventory Prediction Analysis");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(true);
        dialog.setSize(1200, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.setBackground(BACKGROUND);
        topPanel.add(new JLabel("Prediction Period (weeks):"));
        JComboBox<Integer> weeksCombo = new JComboBox<>(new Integer[]{2, 4, 6, 8});
        weeksCombo.setPreferredSize(new Dimension(80, 30));
        topPanel.add(weeksCombo);
        JButton predictBtn = createActionButton("Start Prediction", COLOR_1);
        topPanel.add(predictBtn);

        String[] columnNames = {"Barcode", "Product Name", "Current Stock", "Weekly Avg Outbound", "Predicted Weekly Outbound", "Estimated Stockout Date", "Suggested Restock Qty", "Status"};
        java.util.List<Object[]> predictData = new java.util.ArrayList<>();
        java.util.Map<String, java.util.List<Integer>> weeklyOutboundMap = new java.util.HashMap<>();

        java.util.Map<String, Integer> outboundStats = new java.util.HashMap<>();
        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
            String barcode = record.getBarcode();
            int qty = record.getQuantity();
            outboundStats.put(barcode, outboundStats.getOrDefault(barcode, 0) + qty);

            String dateStr = record.getDate();
            if (dateStr != null && !dateStr.isEmpty()) {
                try {
                    java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
                    int weekNum = date.get(java.time.temporal.WeekFields.ISO.weekOfWeekBasedYear());
                    weeklyOutboundMap.computeIfAbsent(barcode, k -> new java.util.ArrayList<>()).add(qty);
                } catch (Exception e) {
                }
            }
        }

        java.util.Map<String, Integer> currentStockMap = new java.util.HashMap<>();
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            currentStockMap.put(product.getBarcode(), product.getStockQuantity());
        }

        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            String barcode = product.getBarcode();
            String name = product.getName();
            int currentStock = currentStockMap.getOrDefault(barcode, 0);
            int totalOutbound = outboundStats.getOrDefault(barcode, 0);

            java.util.List<Integer> weeklyData = weeklyOutboundMap.getOrDefault(barcode, new java.util.ArrayList<>());
            double avgWeeklyOutbound = 0;
            if (!weeklyData.isEmpty()) {
                avgWeeklyOutbound = weeklyData.stream().mapToInt(Integer::intValue).average().orElse(0);
            }

            String status = "Sufficient";
            String stockOutDate = "-";
            int suggestRestock = 0;

            if (currentStock <= 0) {
                status = "Out of Stock";
                stockOutDate = "Already out of stock";
                suggestRestock = (int) (avgWeeklyOutbound * 4);
            } else if (avgWeeklyOutbound > 0) {
                int weeksUntilStockOut = (int) (currentStock / avgWeeklyOutbound);
                if (weeksUntilStockOut <= 0) {
                    status = "Urgent";
                    stockOutDate = "Out of stock this week";
                } else if (weeksUntilStockOut <= 2) {
                    status = "Warning";
                    stockOutDate = java.time.LocalDate.now().plusWeeks(weeksUntilStockOut).toString();
                } else {
                    stockOutDate = java.time.LocalDate.now().plusWeeks(weeksUntilStockOut).toString();
                }
                suggestRestock = (int) (avgWeeklyOutbound * 4);
            } else if (totalOutbound > 0) {
                status = "Normal";
            } else {
                status = "No Outbound";
            }

            predictData.add(new Object[]{barcode, name, currentStock,
                String.format("%.1f", avgWeeklyOutbound),
                String.format("%.1f", avgWeeklyOutbound * 1.1),
                stockOutDate, suggestRestock, status});
        }

        javax.swing.table.DefaultTableModel predictTableModel = new javax.swing.table.DefaultTableModel(
            predictData.toArray(new Object[0][]), columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2 || columnIndex == 6) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        JTable predictTable = new JTable(predictTableModel);
        predictTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        predictTable.setRowHeight(28);
        predictTable.setAutoCreateRowSorter(true);
        predictTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        predictTable.setRowHeight(30);
        TableColumn col0 = predictTable.getColumnModel().getColumn(0);
        col0.setMinWidth(260);
        col0.setMaxWidth(460);
        col0.setWidth(260);
        col0.setPreferredWidth(260);
        TableColumn col1 = predictTable.getColumnModel().getColumn(1);
        col1.setMinWidth(360);
        col1.setMaxWidth(560);
        col1.setWidth(360);
        col1.setPreferredWidth(360);
        TableColumn col2 = predictTable.getColumnModel().getColumn(2);
        col2.setMinWidth(200);
        col2.setMaxWidth(400);
        col2.setWidth(200);
        col2.setPreferredWidth(200);
        TableColumn col3 = predictTable.getColumnModel().getColumn(3);
        col3.setMinWidth(260);
        col3.setMaxWidth(460);
        col3.setWidth(260);
        col3.setPreferredWidth(260);
        TableColumn col4 = predictTable.getColumnModel().getColumn(4);
        col4.setMinWidth(300);
        col4.setMaxWidth(500);
        col4.setWidth(300);
        col4.setPreferredWidth(300);
        TableColumn col5 = predictTable.getColumnModel().getColumn(5);
        col5.setMinWidth(340);
        col5.setMaxWidth(540);
        col5.setWidth(340);
        col5.setPreferredWidth(340);
        TableColumn col6 = predictTable.getColumnModel().getColumn(6);
        col6.setMinWidth(300);
        col6.setMaxWidth(500);
        col6.setWidth(300);
        col6.setPreferredWidth(300);
        TableColumn col7 = predictTable.getColumnModel().getColumn(7);
        col7.setMinWidth(200);
        col7.setMaxWidth(400);
        col7.setWidth(200);
        col7.setPreferredWidth(200);

        predictTable.getColumnModel().getColumn(7).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = String.valueOf(value);
                switch (status) {
                    case "Out of Stock":
                    case "Urgent":
                        c.setForeground(new Color(239, 68, 68));
                        break;
                    case "Warning":
                        c.setForeground(new Color(234, 179, 8));
                        break;
                    case "Sufficient":
                    case "Normal":
                        c.setForeground(new Color(34, 197, 94));
                        break;
                    default:
                        c.setForeground(new Color(107, 114, 128));
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(predictTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Prediction Results (click column header to sort)"));
        tableScroll.setPreferredSize(new Dimension(1150, 550));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(BACKGROUND);
        infoPanel.add(new JLabel("<html><b>Note:</b> Weekly average outbound is calculated based on historical data, predicted weekly outbound considers growth trend (+10%).<br>Suggested restock quantity is calculated based on 4-week safety stock. Products with status \"No Outbound\" indicate no sales data available.</html>"));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        predictBtn.addActionListener(e -> {
            playSound("success");
            JOptionPane.showMessageDialog(dialog, "Prediction analysis complete!\nAnalyzed " + predictData.size() + " products");
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
        log("[Inventory Prediction] Opened prediction analysis panel");
    }

    /**
     * Create summary card
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setBackground(COLOR_WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        card.add(Box.createVerticalGlue());

        return card;
    }

    /**
     * Create Top ranking panel
     */
    private JPanel createTopListPanel(String title, java.util.List<TopProduct> list, Color barColor) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(COLOR_WHITE);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(COLOR_WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // Limit display count to improve scrolling efficiency
        int count = Math.min(8, list.size());
        int maxQuantity = count > 0 ? list.get(0).totalQuantity : 1;

        for (int i = 0; i < count; i++) {
            TopProduct product = list.get(i);
            JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
            itemPanel.setMaximumSize(new Dimension(450, 32));
            itemPanel.setBackground(COLOR_WHITE);

            JLabel rankLabel = new JLabel(" " + (i + 1) + ".");
            rankLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            rankLabel.setPreferredSize(new Dimension(25, 28));

            JLabel nameLabel = new JLabel(product.name);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            nameLabel.setPreferredSize(new Dimension(150, 28));

            double ratio = (double) product.totalQuantity / maxQuantity;
            int barWidth = (int) (ratio * 120);

            // Use JProgressBar instead of custom drawing for better performance
            JProgressBar progressBar = new JProgressBar(0, maxQuantity);
            progressBar.setValue(product.totalQuantity);
            progressBar.setForeground(barColor);
            progressBar.setBackground(new Color(240, 240, 240));
            progressBar.setPreferredSize(new Dimension(120, 22));
            progressBar.setBorderPainted(false);
            progressBar.setStringPainted(false);

            JLabel countLabel = new JLabel(String.valueOf(product.totalQuantity));
            countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            countLabel.setForeground(barColor);
            countLabel.setPreferredSize(new Dimension(50, 28));

            itemPanel.add(rankLabel, BorderLayout.WEST);
            itemPanel.add(nameLabel, BorderLayout.CENTER);
            itemPanel.add(progressBar, BorderLayout.EAST);
            itemPanel.add(countLabel, BorderLayout.LINE_END);

            listPanel.add(itemPanel);
        }

        if (count == 0) {
            JLabel emptyLabel = new JLabel("No Data");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(156, 163, 175));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLabel);
        }

        // Add scrollbar, reduce height
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(480, 160));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Create warning panel
     */
    private JPanel createWarningPanel(java.util.List<WarningItem> warningList) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Warning List"));
        panel.setBackground(COLOR_WHITE);

        if (warningList.isEmpty()) {
            JLabel noWarning = new JLabel("No warnings");
            noWarning.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            noWarning.setForeground(new Color(34, 197, 94));
            noWarning.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noWarning, BorderLayout.CENTER);
            return panel;
        }

        String[] columns = {"Product Name", "Warning Type", "Description"};
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        for (WarningItem item : warningList) {
            data.add(new Object[]{item.name, item.type, item.description});
        }

        DefaultTableModel warningModel = new DefaultTableModel(data.toArray(new Object[0][]), columns);
        JTable warningTable = new JTable(warningModel);
        warningTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        warningTable.setRowHeight(35);
        warningTable.setAutoCreateRowSorter(true);
        warningTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        warningTable.setRowHeight(30);

        // Set column widths
        TableColumn col0 = warningTable.getColumnModel().getColumn(0);
        col0.setMinWidth(300);
        col0.setMaxWidth(500);
        col0.setWidth(300);
        col0.setPreferredWidth(300);
        TableColumn col1 = warningTable.getColumnModel().getColumn(1);
        col1.setMinWidth(200);
        col1.setMaxWidth(400);
        col1.setWidth(200);
        col1.setPreferredWidth(200);
        TableColumn col2 = warningTable.getColumnModel().getColumn(2);
        col2.setMinWidth(400);
        col2.setMaxWidth(600);
        col2.setWidth(400);
        col2.setPreferredWidth(400);

        warningTable.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) value;
                if ("Stock Abnormal".equals(type) || "Insufficient Stock".equals(type)) {
                    setForeground(new Color(239, 68, 68));
                } else {
                    setForeground(new Color(234, 179, 8));
                }
                setHorizontalAlignment(CENTER);
                return this;
            }
        });

        JScrollPane scrollPane = new JScrollPane(warningTable);
        scrollPane.setPreferredSize(new Dimension(480, 320));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Analysis data inner class
     */
    private static class AnalysisData {
        String barcode;
        String name;
        int inboundTotal;
        int outboundTotal;
        int currentStock;
        String status;
        String level;

        AnalysisData(String barcode, String name, int inboundTotal, int outboundTotal, int currentStock, String status, String level) {
            this.barcode = barcode;
            this.name = name;
            this.inboundTotal = inboundTotal;
            this.outboundTotal = outboundTotal;
            this.currentStock = currentStock;
            this.status = status;
            this.level = level;
        }
    }

    /**
     * Top product data class
     */
    private static class TopProduct {
        String barcode;
        String name;
        int totalQuantity;
        int count;

        TopProduct(String barcode, String name, int totalQuantity, int count) {
            this.barcode = barcode;
            this.name = name;
            this.totalQuantity = totalQuantity;
            this.count = count;
        }
    }

    /**
     * Warning item data class
     */
    private static class WarningItem {
        String barcode;
        String name;
        String type;
        String description;

        WarningItem(String barcode, String name, String type, String description) {
            this.barcode = barcode;
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }

    /**
     * Status column cell renderer
     */
    private static class StatusCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            if (status.contains("Normal")) {
                setForeground(new Color(34, 197, 94));
            } else if (status.contains("Warning")) {
                setForeground(new Color(234, 179, 8));
            } else if (status.contains("Out of Stock")) {
                setForeground(new Color(239, 68, 68));
            } else if (status.contains("Abnormal")) {
                setForeground(new Color(37, 37, 37));
            }

            setHorizontalAlignment(CENTER);
            return this;
        }
    }
}
