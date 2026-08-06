package zyhinventory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
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
        setTitle("邹宇恒库存管理系统");
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

        JLabel titleLabel = new JLabel("邹宇恒库存管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        titleLabel.setForeground(COLOR_1);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton inventoryBtn = createMenuButton("库存管理", COLOR_2, e -> showPanel("inventory"));
        JButton inboundBtn = createMenuButton("入库管理", COLOR_2, e -> showPanel("inbound"));
        JButton outboundBtn = createMenuButton("出库管理", COLOR_2, e -> showPanel("outbound"));
        JButton reportBtn = createMenuButton("报表生成", COLOR_2, e -> showPanel("report"));
        JButton chartBtn = createMenuButton("图表展示", COLOR_2, e -> showPanel("chart"));
        JButton resetBtn = createMenuButton("重置数据", COLOR_2, e -> resetDataFromAPI());
        JButton analysisBtn = createMenuButton("数据分析", COLOR_2, e -> showAnalysisDialog());
        JButton predictBtn = createMenuButton("库存预测", COLOR_2, e -> showStockPredictionDialog());

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
        button.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        button.setForeground(COLOR_WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(180, 45));
        button.setMaximumSize(new Dimension(180, 45));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setOpaque(true);
        button.addActionListener(listener);
        return button;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new RoundedButton(text, 6);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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
                new Object[]{"条码", "商品名称", "进货价", "销售价", "库存量", "状态"}, 0);
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        inventoryTable.setRowHeight(28);
        JScrollPane inventoryScroll = new JScrollPane(inventoryTable);

        inboundTableModel = new DefaultTableModel(
                new Object[]{"记录ID", "商品条码", "商品名称", "数量", "日期"}, 0);
        inboundTable = new JTable(inboundTableModel);
        inboundTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        inboundTable.setRowHeight(28);
        JScrollPane inboundScroll = new JScrollPane(inboundTable);

        outboundTableModel = new DefaultTableModel(
                new Object[]{"记录ID", "商品条码", "商品名称", "数量", "日期"}, 0);
        outboundTable = new JTable(outboundTableModel);
        outboundTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        outboundTable.setRowHeight(28);
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
        logPanel.setBorder(BorderFactory.createTitledBorder("操作日志"));
        logArea = new JTextArea();
        logArea.setFont(new Font("微软雅黑", Font.PLAIN, 11));
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

        JLabel titleLabel = new JLabel("库存管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("新增商品", COLOR_1);
        addBtn.addActionListener(e -> showAddProductDialog());

        JButton modifyBtn = createActionButton("修改价格", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyPriceDialog());

        JButton deleteBtn = createActionButton("删除商品", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedInventory());

        JButton filterBtn = createActionButton("数据筛选", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("inventory"));

        JButton batchPriceBtn = createActionButton("批量改价", new Color(249, 115, 22));
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

        JLabel titleLabel = new JLabel("入库管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("新增入库", COLOR_1);
        addBtn.addActionListener(e -> showAddInboundDialog());

        JButton modifyBtn = createActionButton("修改入库记录", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyInboundDialog());

        JButton deleteBtn = createActionButton("删除入库记录", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedInbound());

        JButton filterBtn = createActionButton("数据筛选", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("inbound"));

        JButton batchInboundBtn = createActionButton("批量入库", COLOR_3);
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
        inboundQueueInfoLabel = new JLabel("入库记录队列（先进先出）当前队列长度: " + dataManager.getInboundCount());
        inboundQueueInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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

        JLabel titleLabel = new JLabel("出库管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton addBtn = createActionButton("新增出库", COLOR_1);
        addBtn.addActionListener(e -> showAddOutboundDialog());

        JButton modifyBtn = createActionButton("修改出库记录", COLOR_2);
        modifyBtn.addActionListener(e -> showModifyOutboundDialog());

        JButton deleteBtn = createActionButton("删除出库记录", new Color(239, 68, 68));
        deleteBtn.addActionListener(e -> deleteSelectedOutbound());

        JButton filterBtn = createActionButton("数据筛选", new Color(139, 92, 246));
        filterBtn.addActionListener(e -> openFilterDialog("outbound"));

        JButton batchOutboundBtn = createActionButton("批量出库", COLOR_3);
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
        outboundStackInfoLabel = new JLabel("出库记录栈（先进后出）当前栈长度: " + dataManager.getOutboundCount());
        outboundStackInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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
            inboundQueueInfoLabel.setText("入库记录队列（先进先出）当前队列长度: " + dataManager.getInboundCount());
        }
    }

    private void updateOutboundStackInfo() {
        if (outboundStackInfoLabel != null) {
            outboundStackInfoLabel.setText("出库记录栈（先进后出）当前栈长度: " + dataManager.getOutboundCount());
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

        JLabel titleLabel = new JLabel("库存业务报表");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton generateBtn = createActionButton("生成报表", COLOR_1);
        generateBtn.addActionListener(e -> generateReport());

        JButton perfTestBtn = createActionButton("性能测试", COLOR_2);
        perfTestBtn.addActionListener(e -> runPerformanceTest());

        JButton htmlReportBtn = createActionButton("生成HTML报表", COLOR_3);
        htmlReportBtn.addActionListener(e -> generateHtmlReport());

        buttonPanel.add(generateBtn);
        buttonPanel.add(perfTestBtn);
        buttonPanel.add(htmlReportBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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

        JLabel titleLabel = new JLabel("库存数据图表展示");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JButton refreshBtn = createActionButton("刷新图表", COLOR_1);
        refreshBtn.addActionListener(e -> refreshChart());

        JLabel granularityLabel = new JLabel("时间粒度:");
        granularityLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        
        JComboBox<String> granularityCombo = new JComboBox<>(new String[]{"按日", "按月", "按年"});
        granularityCombo.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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

        JLabel titleLabel = new JLabel("库存分布饼图");
        titleLabel.setFont(new Font("黑体", Font.BOLD, 14));
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
                    g2d.drawString("暂无数据", centerX - 30, centerY);
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

                g2d.setFont(new Font("黑体", Font.PLAIN, 11));
                
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
                    g2d.setFont(new Font("黑体", Font.BOLD, 11));
                    int textWidth = g2d.getFontMetrics().stringWidth(item[1]);
                    g2d.drawString(item[1], width - textWidth - 8, y + 11);
                    g2d.setFont(new Font("黑体", Font.PLAIN, 11));
                    
                    y += itemHeight;
                    colorIndex++;
                }
                
                if (!negativeItems.isEmpty()) {
                    y += 12;
                    
                    g2d.setColor(new Color(239, 68, 68));
                    g2d.setFont(new Font("黑体", Font.BOLD, 11));
                    g2d.drawString("异常库存 (负数):", paddingLeft, y);
                    y += itemHeight;
                    
                    g2d.setFont(new Font("黑体", Font.PLAIN, 11));
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

        JLabel titleLabel = new JLabel("出入库统计柱状图");
        titleLabel.setFont(new Font("黑体", Font.BOLD, 14));
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
                    g2d.drawString("暂无数据", width / 2 - 30, height / 2);
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
                
                g2d.setFont(new Font("黑体", Font.PLAIN, 11));
                
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
                g2d.drawString("入库", width - 80, padding + 8);
                
                g2d.setColor(new Color(239, 68, 68));
                g2d.fillRect(width - 100, padding + 18, 15, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("出库", width - 80, padding + 26);
            }
        };
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createComparePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("顺序表与链表性能比对");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(COLOR_1);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buttonPanel.setBackground(BACKGROUND);

        JSpinner dataSizeSpinner = new JSpinner(new SpinnerNumberModel(10000, 1000, 100000, 1000));
        JButton compareBtn = createActionButton("开始比对", COLOR_1);
        compareBtn.addActionListener(e -> {
            int size = (int) dataSizeSpinner.getValue();
            runComparison(size);
        });

        buttonPanel.add(new JLabel("数据量:"));
        buttonPanel.add(dataSizeSpinner);
        buttonPanel.add(compareBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JTextArea compareArea = new JTextArea();
        compareArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        compareArea.setEditable(false);
        compareArea.setBorder(BorderFactory.createLineBorder(COLOR_4, 2));
        JScrollPane compareScroll = new JScrollPane(compareArea);

        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(BACKGROUND);
        resultPanel.add(compareScroll, BorderLayout.CENTER);

        JLabel resultLabel = new JLabel("比对结果将显示在这里");
        resultLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
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
                
                String status = newStock > 0 ? "正常" : (newStock < 0 ? "异常" : "售罄");
                product.setStatus(status);
                
                if (inventoryTableModel != null) {
                    inventoryTableModel.setValueAt(newStock, i, 4);
                    inventoryTableModel.setValueAt(status, i, 5);
                }
                
                log("[库存更新] 条码: " + barcode + ", 原库存: " + currentStock + ", 新增: " + quantity + ", 新库存: " + newStock);
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
        log("开始从API加载数据...");
        loadInventoryData();
        loadInboundData();
        loadOutboundData();
        updateInboundQueueInfo();
        updateOutboundStackInfo();
        log("数据加载完成");
    }

    private void loadInventoryData() {
        String response = ZYHApiClient.queryData("库存");
        log("[库存] API响应: " + response);

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
                        String status = stock > 0 ? "正常" : (stock < 0 ? "异常" : "售罄");

                        ZYHProduct product = new ZYHProduct(barcode, name, buyPrice, sellPrice);
                        product.setStockQuantity(stock);
                        dataManager.getInventoryList().add(product);
                        inventoryTableModel.addRow(new Object[]{barcode, name, buyPrice, sellPrice, stock, status});
                    }
                }
                log("[库存] 成功加载 " + records.size() + " 条数据");
            } catch (Exception e) {
                log("[库存] 解析错误: " + e.getMessage());
            }
        }
    }

    private void loadInboundData() {
        String response = ZYHApiClient.queryData("入库");
        log("[入库] API响应: " + response);

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
                log("[入库] 成功加载 " + records.size() + " 条数据");
            } catch (Exception e) {
                log("[入库] 解析错误: " + e.getMessage());
            }
        }
    }

    private void loadOutboundData() {
        String response = ZYHApiClient.queryData("出库");
        log("[出库] API响应: " + response);

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
                log("[出库] 成功加载 " + records.size() + " 条数据");
            } catch (Exception e) {
                log("[出库] 解析错误: " + e.getMessage());
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

        log("[DEBUG] 处理后的响应: " + response.substring(0, Math.min(200, response.length())) + "...");

        int dataArrayStart = response.indexOf("\"data\":[");
        if (dataArrayStart == -1) {
            log("[DEBUG] 未找到 data 数组");
            return records;
        }
        
        int arrayContentStart = dataArrayStart + 8;
        int arrayEnd = response.lastIndexOf("]");
        
        if (arrayEnd == -1 || arrayEnd <= arrayContentStart) {
            log("[DEBUG] data 数组格式错误");
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
        
        log("[DEBUG] 分割出 " + itemList.size() + " 条记录");
        for (int i = 0; i < itemList.size(); i++) {
            String item = itemList.get(i);
            log("[DEBUG] 处理第 " + (i + 1) + " 条记录: " + item.substring(0, Math.min(80, item.length())) + "...");
            String record = extractRecordData(item);
            log("[DEBUG] 解析结果: " + record);
            if (!record.isEmpty()) {
                records.add(record);
            }
        }

        log("[DEBUG] 解析出 " + records.size() + " 条记录");
        return records;
    }

    private String extractRecordData(String json) {
        String barcode = parseNestedValue(json, "商品条码");
        String name = parseNestedValue(json, "商品名称");
        String buyPrice = parseNestedValue(json, "进货价格");
        String sellPrice = parseNestedValue(json, "售卖定价");
        String stock = parseNestedValue(json, "库存量");
        String status = parseNestedValue(json, "数据状态");
        String quantity = parseNestedValue(json, "数量");
        String date = parseNestedValue(json, "日期");
        String inboundItem = parseNestedValue(json, "入库项");
        String outboundItem = parseNestedValue(json, "出库项");

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
            log("[DEBUG] parseNestedValue: 未找到字段 '" + fieldName + "'");
            return "";
        }
        log("[DEBUG] parseNestedValue: 字段 '" + fieldName + "' 在位置 " + keyIndex + ", 值开始位置 " + (keyIndex + key.length()));
        if (keyIndex + key.length() < json.length()) {
            log("[DEBUG] parseNestedValue: 字段 '" + fieldName + "' 后100字符: " + json.substring(keyIndex, Math.min(keyIndex + 100, json.length())));
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
        String barcode = extractSimpleValue(fieldsStr, "商品条码");
        String name = extractSimpleValue(fieldsStr, "商品名称");
        String buyPrice = extractSimpleValue(fieldsStr, "进货价");
        String sellPrice = extractSimpleValue(fieldsStr, "售卖定价");
        String stock = extractSimpleValue(fieldsStr, "库存量");
        String status = extractSimpleValue(fieldsStr, "状态");
        String quantity = extractSimpleValue(fieldsStr, "数量");
        String date = extractSimpleValue(fieldsStr, "日期");
        String inboundItem = extractSimpleValue(fieldsStr, "入库项");
        String outboundItem = extractSimpleValue(fieldsStr, "出库项");
        
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
        JDialog dialog = new JDialog(this, "新增商品", true);
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
        panel.add(new JLabel("商品条码:"), gbc);
        gbc.gridx = 1;
        panel.add(barcodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("商品名称:"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("进货价格:"), gbc);
        gbc.gridx = 1;
        panel.add(buyPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("销售价格:"), gbc);
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

        JButton okBtn = createActionButton("确定", COLOR_1);
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
                inventoryTableModel.addRow(new Object[]{barcode, name, buyPrice, sellPrice, stock, "正常"});

                updateInboundRecordsWithProductInfo(barcode, name);

                JOptionPane.showMessageDialog(dialog, "新增成功！\n入库记录已自动关联库存数量: " + stock);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
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
            JOptionPane.showMessageDialog(this, "请先选择一个商品");
            return;
        }

        String barcode = (String) inventoryTableModel.getValueAt(selectedRow, 0);
        String name = (String) inventoryTableModel.getValueAt(selectedRow, 1);
        double buyPrice = (double) inventoryTableModel.getValueAt(selectedRow, 2);
        double sellPrice = (double) inventoryTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "修改商品价格", true);
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

        panel.add(new JLabel("商品条码:"));
        panel.add(barcodeField);
        panel.add(new JLabel("商品名称:"));
        panel.add(nameField);
        panel.add(new JLabel("进货价格:"));
        panel.add(buyPriceField);
        panel.add(new JLabel("销售价格:"));
        panel.add(sellPriceField);

        JButton okBtn = createActionButton("确认修改", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                double newBuyPrice = Double.parseDouble(buyPriceField.getText().trim());
                double newSellPrice = Double.parseDouble(sellPriceField.getText().trim());

                if (newBuyPrice < 0 || newSellPrice < 0) {
                    JOptionPane.showMessageDialog(dialog, "价格不能为负数");
                    return;
                }

                log("[修改价格] 条码: " + barcode + ", 进货价: " + buyPrice + " -> " + newBuyPrice + ", 销售价: " + sellPrice + " -> " + newSellPrice + " (本地操作)");

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

                JOptionPane.showMessageDialog(dialog, "修改成功！");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showBatchPriceDialog() {
        JDialog dialog = new JDialog(this, "批量修改价格", true);
        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        topPanel.add(new JLabel("调整方式:"));
        JComboBox<String> adjustTypeCombo = new JComboBox<>(new String[]{"按金额降价", "按金额涨价", "按比例降价", "按比例涨价"});
        topPanel.add(adjustTypeCombo);
        JTextField adjustValueField = new JTextField("0");
        topPanel.add(adjustValueField);

        String[] columnNames = {"选择", "条码", "商品名称", "当前进货价", "当前销售价", "新进货价", "新销售价"};
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
        priceTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        priceTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(priceTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("商品价格列表（可直接编辑新价格）"));

        JButton previewBtn = createActionButton("预览调整", COLOR_2);
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
                        case "按金额降价":
                            newSell = Math.max(0, currentSell - adjustValue);
                            break;
                        case "按金额涨价":
                            newSell = currentSell + adjustValue;
                            break;
                        case "按比例降价":
                            newSell = Math.max(0, currentSell * (1 - adjustValue / 100));
                            break;
                        case "按比例涨价":
                            newSell = currentSell * (1 + adjustValue / 100);
                            break;
                    }
                    priceTableModel.setValueAt(Math.round(newSell * 100.0) / 100.0, i, 6);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton selectAllBtn = createActionButton("全选", new Color(107, 114, 128));
        selectAllBtn.addActionListener(e -> {
            for (int i = 0; i < priceTableModel.getRowCount(); i++) {
                priceTableModel.setValueAt(true, i, 0);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("确认修改", COLOR_1);
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
                JOptionPane.showMessageDialog(dialog, "批量修改成功！共修改 " + successCount + " 个商品销售价格");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
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
        JDialog dialog = new JDialog(this, "扫码入库", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField("1");

        panel.add(new JLabel("商品条码（扫码枪输入）:"));
        panel.add(barcodeField);
        panel.add(new JLabel("入库数量:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("确认入库", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                log("[扫码入库] 条码: " + barcode + ", 数量: " + quantity + " (本地操作)");

                ZYHInboundRecord record = new ZYHInboundRecord(barcode, "", quantity);
                dataManager.getInboundQueue().addLast(record);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "入库成功！");
                dialog.dispose();
                loadInboundData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddInboundDialog() {
        JDialog dialog = new JDialog(this, "新增入库", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField();
        JTextField quantityField = new JTextField("1");

        panel.add(new JLabel("商品条码:"));
        panel.add(barcodeField);
        panel.add(new JLabel("入库数量:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("确认入库", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                log("[新增入库] 条码: " + barcode + ", 数量: " + quantity + " (本地操作)");

                String recordId = String.valueOf(System.currentTimeMillis());
                String name = findProductNameByBarcode(barcode);
                ZYHInboundRecord record = new ZYHInboundRecord(recordId, barcode, name, quantity, java.time.LocalDate.now().toString());
                dataManager.getInboundQueue().addLast(record);
                inboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity,
                        java.time.LocalDate.now().toString()});

                updateInboundQueueInfo();

                updateInventoryQuantity(barcode, quantity);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "入库成功！");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBatchInboundDialog() {
        JDialog dialog = new JDialog(this, "批量入库", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"日期", "条码", "商品名称", "数量"};
        java.util.List<Object[]> batchData = new java.util.ArrayList<>();
        batchData.add(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        javax.swing.table.DefaultTableModel batchTableModel = new javax.swing.table.DefaultTableModel(batchData.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        JTable batchTable = new JTable(batchTableModel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("商品条码:"));
        JTextField barcodeField = new JTextField();
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("商品名称:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("入库数量:"));
        JTextField quantityField = new JTextField("1");
        inputPanel.add(quantityField);
        
        JPanel quickAddPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton quickAddBtn = createActionButton("快速添加到列表", COLOR_2);
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
                    JOptionPane.showMessageDialog(dialog, "请输入商品条码");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "数量必须是数字");
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
        batchTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        batchTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(batchTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("入库记录列表（可编辑）"));

        JButton addRowBtn = createActionButton("添加一行", COLOR_2);
        addRowBtn.addActionListener(e -> {
            batchTableModel.addRow(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        });

        JButton removeRowBtn = createActionButton("删除行", new Color(239, 68, 68));
        removeRowBtn.addActionListener(e -> {
            int selectedRow = batchTable.getSelectedRow();
            if (selectedRow >= 0) {
                batchTableModel.removeRow(selectedRow);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("确认入库", COLOR_1);
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
                JOptionPane.showMessageDialog(dialog, "批量入库成功！共录入 " + successCount + " 条记录");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
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
            JOptionPane.showMessageDialog(this, "请先选择一条入库记录");
            return;
        }

        String recordId = (String) inboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) inboundTableModel.getValueAt(selectedRow, 1);
        String name = (String) inboundTableModel.getValueAt(selectedRow, 2);
        int oldQuantity = (int) inboundTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "修改入库记录", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField(barcode);
        barcodeField.setEditable(false);
        JTextField quantityField = new JTextField(String.valueOf(oldQuantity));

        panel.add(new JLabel("商品条码:"));
        panel.add(barcodeField);
        panel.add(new JLabel("入库数量:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("确认修改", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "入库数量必须大于0");
                    return;
                }

                int diff = newQuantity - oldQuantity;

                log("[修改入库] 记录ID: " + recordId + ", 条码: " + barcode + ", 原数量: " + oldQuantity + ", 新数量: " + newQuantity + " (本地操作)");

                for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
                    ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
                    if (record.getRecordId().equals(recordId)) {
                        record.setQuantity(newQuantity);
                        break;
                    }
                }

                inboundTableModel.setValueAt(newQuantity, selectedRow, 3);

                updateInventoryQuantity(barcode, diff);

                JOptionPane.showMessageDialog(dialog, "修改成功！");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddOutboundDialog() {
        JDialog dialog = new JDialog(this, "新增出库", true);
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

        panel.add(new JLabel("商品条码:"));
        panel.add(barcodeField);
        panel.add(new JLabel("商品名称:"));
        panel.add(nameField);
        panel.add(new JLabel("出库数量:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("确认出库", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                String barcode = barcodeField.getText().trim();
                String name = nameField.getText().trim();
                int quantity = Integer.parseInt(quantityField.getText().trim());

                if (name.isEmpty()) {
                    name = findProductNameByBarcode(barcode);
                }

                log("[新增出库] 条码: " + barcode + ", 名称: " + name + ", 数量: " + quantity + " (本地操作)");

                String recordId = String.valueOf(System.currentTimeMillis());
                ZYHOutboundRecord record = new ZYHOutboundRecord(recordId, barcode, name, quantity, java.time.LocalDate.now().toString());
                dataManager.getOutboundStack().addLast(record);
                outboundTableModel.addRow(new Object[]{recordId, barcode, name, quantity,
                        java.time.LocalDate.now().toString()});

                updateOutboundStackInfo();

                updateInventoryQuantity(barcode, -quantity);

                playSound("success");
                JOptionPane.showMessageDialog(dialog, "出库成功！");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
        cancelBtn.addActionListener(e -> dialog.dispose());

        panel.add(okBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showBatchOutboundDialog() {
        JDialog dialog = new JDialog(this, "批量出库", true);
        dialog.setSize(600, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"日期", "条码", "商品名称", "数量"};
        java.util.List<Object[]> batchData = new java.util.ArrayList<>();
        batchData.add(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        javax.swing.table.DefaultTableModel batchTableModel = new javax.swing.table.DefaultTableModel(batchData.toArray(new Object[0][]), columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };
        JTable batchTable = new JTable(batchTableModel);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("商品条码:"));
        JTextField barcodeField = new JTextField();
        inputPanel.add(barcodeField);
        inputPanel.add(new JLabel("商品名称:"));
        JTextField nameField = new JTextField();
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("出库数量:"));
        JTextField quantityField = new JTextField("1");
        inputPanel.add(quantityField);
        
        JPanel quickAddPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton quickAddBtn = createActionButton("快速添加到列表", COLOR_2);
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
                    JOptionPane.showMessageDialog(dialog, "请输入商品条码");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "数量必须是数字");
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
        
        batchTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        batchTable.setRowHeight(28);
        JScrollPane tableScroll = new JScrollPane(batchTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("出库记录列表（可编辑）"));

        JButton addRowBtn = createActionButton("添加一行", COLOR_2);
        addRowBtn.addActionListener(e -> {
            batchTableModel.addRow(new Object[]{java.time.LocalDate.now().toString(), "", "", 1});
        });

        JButton removeRowBtn = createActionButton("删除行", new Color(239, 68, 68));
        removeRowBtn.addActionListener(e -> {
            int selectedRow = batchTable.getSelectedRow();
            if (selectedRow >= 0) {
                batchTableModel.removeRow(selectedRow);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okBtn = createActionButton("确认出库", COLOR_1);
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
                JOptionPane.showMessageDialog(dialog, "批量出库成功！共录入 " + successCount + " 条记录");
                dialog.dispose();
            } catch (Exception ex) {
                playSound("error");
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
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
            JOptionPane.showMessageDialog(this, "请先选择一条出库记录");
            return;
        }

        String recordId = (String) outboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) outboundTableModel.getValueAt(selectedRow, 1);
        String name = (String) outboundTableModel.getValueAt(selectedRow, 2);
        int oldQuantity = (int) outboundTableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog(this, "修改出库记录", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField barcodeField = new JTextField(barcode);
        barcodeField.setEditable(false);
        JTextField quantityField = new JTextField(String.valueOf(oldQuantity));

        panel.add(new JLabel("商品条码:"));
        panel.add(barcodeField);
        panel.add(new JLabel("出库数量:"));
        panel.add(quantityField);

        JButton okBtn = createActionButton("确认修改", COLOR_1);
        okBtn.addActionListener(e -> {
            try {
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                if (newQuantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "出库数量必须大于0");
                    return;
                }

                int currentStock = getCurrentStockQuantity(barcode);
                int diff = oldQuantity - newQuantity;

                if (currentStock + diff < 0) {
                    JOptionPane.showMessageDialog(dialog, "库存不足！当前库存: " + currentStock + ", 需要增加库存: " + Math.abs(currentStock + diff));
                    return;
                }

                log("[修改出库] 记录ID: " + recordId + ", 条码: " + barcode + ", 原数量: " + oldQuantity + ", 新数量: " + newQuantity + " (本地操作)");

                for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
                    ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
                    if (record.getRecordId().equals(recordId)) {
                        record.setQuantity(newQuantity);
                        break;
                    }
                }

                outboundTableModel.setValueAt(newQuantity, selectedRow, 3);

                updateInventoryQuantity(barcode, diff);

                JOptionPane.showMessageDialog(dialog, "修改成功！");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "输入错误：" + ex.getMessage());
            }
        });

        JButton cancelBtn = createActionButton("取消", new Color(107, 114, 128));
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
        report.append("========== 邹宇恒库存管理系统 业务报表 ==========\n\n");
        report.append("生成时间: ").append(java.time.LocalDateTime.now().toString().substring(0, 19)).append("\n\n");

        report.append("【库存统计】\n");
        report.append("商品总数: ").append(dataManager.getInventoryCount()).append("\n");
        report.append("库存记录数: ").append(dataManager.getInventoryCount()).append("\n");
        report.append("入库记录数: ").append(dataManager.getInboundCount()).append("\n");
        report.append("出库记录数: ").append(dataManager.getOutboundCount()).append("\n\n");

        report.append("【顺序表数据】\n");
        report.append("商品列表: ").append(dataManager.getInventoryList().size()).append(" 条\n");
        report.append("库存列表: ").append(dataManager.getInventoryList().size()).append(" 条\n\n");

        report.append("【链表数据】\n");
        report.append("入库队列: ").append(dataManager.getInboundQueue().size()).append(" 条\n");
        report.append("出库栈: ").append(dataManager.getOutboundStack().size()).append(" 条\n\n");

        report.append("【库存详情】\n");
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            report.append(String.format("%d. %s - 库存: %d\n", i + 1, product.getName(), product.getStockQuantity()));
        }

        report.append("\n===============================================\n");

        for (java.awt.Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                findAndSetReportText((JPanel) comp, report.toString());
            }
        }

        JOptionPane.showMessageDialog(this, "报表已生成！");

        log("[报表] 生成完成");
    }

    /**
     * 生成HTML格式的库存数据分析报表
     */
    private void generateHtmlReport() {
        StringBuilder html = new StringBuilder();
        
        // HTML头部
        html.append("<!DOCTYPE html>\n");
        html.append("<html lang=\"zh-CN\">\n");
        html.append("<head>\n");
        html.append("<meta charset=\"UTF-8\">\n");
        html.append("<title>库存数据分析报告</title>\n");
        html.append("<style>\n");
        html.append(":root { --darkest: #0c4a6e; --dark: #0369a1; --primary: #0ea5e9; --light: #bae6fd; --white: #ffffff; }\n");
        html.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        html.append("body { font-family: 'Microsoft YaHei', Arial, sans-serif; background: var(--light); padding: 30px; }\n");
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
        html.append(".排查-list { }\n");
        html.append(".排查-item { background: rgba(220, 38, 38, 0.05); border-left: 4px solid #dc2626; padding: 18px; border-radius: 0 12px 12px 0; margin-bottom: 15px; }\n");
        html.append(".排查-item:last-child { margin-bottom: 0; }\n");
        html.append(".排查-header { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }\n");
        html.append(".排查-icon { font-size: 22px; }\n");
        html.append(".排查-name { font-size: 18px; font-weight: bold; color: #dc2626; }\n");
        html.append(".排查-stock { font-size: 14px; color: #374151; margin-bottom: 10px; }\n");
        html.append(".排查-stock span { color: #dc2626; font-weight: bold; font-size: 16px; }\n");
        html.append(".排查-suggest { background: rgba(3, 105, 161, 0.05); padding: 12px; border-radius: 8px; }\n");
        html.append(".排查-suggest-title { font-size: 14px; font-weight: bold; color: var(--dark); margin-bottom: 8px; }\n");
        html.append(".排查-suggest ul { margin: 0; padding-left: 20px; }\n");
        html.append(".排查-suggest li { font-size: 13px; color: #0369a1; margin-bottom: 5px; line-height: 1.5; }\n");
        html.append(".footer { text-align: center; padding: 25px; background: rgba(12, 74, 110, 0.05); color: var(--dark); font-size: 14px; }\n");
        html.append("</style>\n");
        html.append("</head>\n");
        html.append("<body>\n");
        html.append("<div class=\"report-container\">\n");
        
        // 报表头部
        html.append("<div class=\"report-header\">\n");
        html.append("<h1>库存数据分析报告</h1>\n");
        html.append("<div class=\"time\">生成时间: ").append(java.time.LocalDateTime.now().toString().substring(0, 19)).append("</div>\n");
        html.append("</div>\n");
        
        html.append("<div class=\"report-body\">\n");
        
        // 统计摘要
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📊 统计摘要</div>\n");
        html.append("<div class=\"summary-cards\">\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getInventoryCount()).append("</div><div class=\"label\">商品总数</div></div>\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getInboundCount()).append("</div><div class=\"label\">入库记录</div></div>\n");
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(dataManager.getOutboundCount()).append("</div><div class=\"label\">出库记录</div></div>\n");
        
        int totalStock = 0;
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            totalStock += dataManager.getInventoryList().get(i).getStockQuantity();
        }
        html.append("<div class=\"summary-card\"><div class=\"value\">").append(totalStock).append("</div><div class=\"label\">库存总量</div></div>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        // 统计数据分布
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📈 数据分布</div>\n");
        html.append("<table>\n");
        html.append("<tr><th>数据结构</th><th>数据类型</th><th>记录数</th></tr>\n");
        html.append("<tr><td>顺序表</td><td>商品列表</td><td>").append(dataManager.getInventoryList().size()).append("</td></tr>\n");
        html.append("<tr><td>顺序表</td><td>库存列表</td><td>").append(dataManager.getInventoryList().size()).append("</td></tr>\n");
        html.append("<tr><td>链表(队列)</td><td>入库记录</td><td>").append(dataManager.getInboundQueue().size()).append("</td></tr>\n");
        html.append("<tr><td>链表(栈)</td><td>出库记录</td><td>").append(dataManager.getOutboundStack().size()).append("</td></tr>\n");
        html.append("</table>\n");
        html.append("</div>\n");
        
        // 入库Top分析
        java.util.Map<String, Integer> inboundStats = new java.util.HashMap<>();
        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
            inboundStats.put(record.getBarcode(), inboundStats.getOrDefault(record.getBarcode(), 0) + record.getQuantity());
        }
        
        java.util.List<java.util.Map.Entry<String, Integer>> inboundList = new java.util.ArrayList<>(inboundStats.entrySet());
        inboundList.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📥 入库Top商品</div>\n");
        
        int maxInbound = inboundList.isEmpty() ? 1 : inboundList.get(0).getValue();
        for (int i = 0; i < Math.min(5, inboundList.size()); i++) {
            java.util.Map.Entry<String, Integer> entry = inboundList.get(i);
            String name = "未知商品";
            for (int j = 0; j < dataManager.getInventoryList().size(); j++) {
                if (dataManager.getInventoryList().get(j).getBarcode().equals(entry.getKey())) {
                    name = dataManager.getInventoryList().get(j).getName();
                    break;
                }
            }
            double ratio = (double) entry.getValue() / maxInbound * 100;
            html.append("<div style=\"margin: 10px 0;\">\n");
            html.append("<span class=\"rank\">").append(i + 1).append("</span>").append(name).append(" (").append(entry.getValue()).append("件)\n");
            html.append("<div class=\"progress-bar\"><div class=\"progress-fill\" style=\"width: ").append(ratio).append("%\"></div></div>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");
        
        // 出库Top分析
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
        html.append("<div class=\"section-title\">📤 出库Top商品</div>\n");
        
        int maxOutbound = outboundList.isEmpty() ? 1 : outboundList.get(0).getValue();
        for (int i = 0; i < Math.min(5, outboundList.size()); i++) {
            java.util.Map.Entry<String, Integer> entry = outboundList.get(i);
            String name = "未知商品";
            for (int j = 0; j < dataManager.getInventoryList().size(); j++) {
                if (dataManager.getInventoryList().get(j).getBarcode().equals(entry.getKey())) {
                    name = dataManager.getInventoryList().get(j).getName();
                    break;
                }
            }
            double ratio = (double) entry.getValue() / maxOutbound * 100;
            html.append("<div style=\"margin: 10px 0;\">\n");
            html.append("<span class=\"rank\">").append(i + 1).append("</span>").append(name).append(" (").append(entry.getValue()).append("件)\n");
            html.append("<div class=\"progress-bar\"><div class=\"progress-fill\" style=\"width: ").append(ratio).append("%\"></div></div>\n");
            html.append("</div>\n");
        }
        html.append("</div>\n");
        
        // 库存详情
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">📦 库存详情</div>\n");
        html.append("<table>\n");
        html.append("<tr><th>商品条码</th><th>商品名称</th><th>入库总量</th><th>出库总量</th><th>当前库存</th><th>状态</th></tr>\n");
        
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int inbound = inboundStats.getOrDefault(product.getBarcode(), 0);
            int outbound = outboundStats.getOrDefault(product.getBarcode(), 0);
            int stock = product.getStockQuantity();
            String status;
            String statusClass;
            if (stock < 0) {
                status = "异常";
                statusClass = "status-error";
            } else if (stock == 0) {
                status = "缺货";
                statusClass = "status-out";
            } else if (stock <= 5) {
                status = "预警";
                statusClass = "status-warning";
            } else {
                status = "正常";
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
        
        // 预警清单
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">⚠️ 预警清单</div>\n");
        
        boolean hasWarning = false;
        html.append("<div class=\"warning-list\">\n");
        for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
            ZYHProduct product = dataManager.getInventoryList().get(i);
            int stock = product.getStockQuantity();
            if (stock < 0) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-error\">库存异常</span>").append(product.getName()).append(" - 库存为负数: ").append(stock).append("</div>\n");
                hasWarning = true;
            } else if (stock == 0) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-error\">库存不足</span>").append(product.getName()).append(" - 库存为0，需立即补货</div>\n");
                hasWarning = true;
            } else if (stock <= 5) {
                html.append("<div class=\"warning-item\"><span class=\"warning-type type-warning\">库存预警</span>").append(product.getName()).append(" - 库存偏低: ").append(stock).append("件</div>\n");
                hasWarning = true;
            }
        }
        
        if (!hasWarning) {
            html.append("<div style=\"color: #22c55e; text-align: center; padding: 10px;\">暂无预警，库存状态良好</div>\n");
        }
        html.append("</div>\n");
        html.append("</div>\n");
        
        // 智能建议模块
        html.append("<div class=\"section\">\n");
        html.append("<div class=\"section-title\">智能建议</div>\n");
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
                html.append("<div class=\"advice-title\">紧急补货建议</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 库存为负数(<span style=\"color:#dc2626\">").append(stock).append("</span>)，数据异常需立即处理！</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (stock == 0) {
                html.append("<div class=\"advice-card urgent\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">紧急补货建议</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 库存为0，需立即补货避免缺货损失！</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (stock <= 5) {
                html.append("<div class=\"advice-card warning\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">库存预警建议</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 库存偏低(").append(stock).append("件)，建议近期安排补货。</div>\n");
                html.append("</div>\n");
                html.append("</div>\n");
                hasAdvice = true;
            } else if (inbound > 0 && outbound > 0 && inbound == outbound && inbound > 10) {
                html.append("<div class=\"advice-card success\">\n");
                html.append("<div class=\"advice-icon\"></div>\n");
                html.append("<div class=\"advice-content\">\n");
                html.append("<div class=\"advice-title\">销售建议</div>\n");
                html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 入库量与出库量持平（共").append(inbound).append("件），库存周转良好。</div>\n");
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
                            html.append("<div class=\"advice-title\">严重积压警告</div>\n");
                            html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 库存").append(stock).append("件，已超过").append(daysSinceLastOut).append("天无出库记录，建议大幅降价促销！</div>\n");
                            html.append("</div>\n");
                            html.append("</div>\n");
                            hasAdvice = true;
                        } else if (daysSinceLastOut > 30 && inbound > 0) {
                            html.append("<div class=\"advice-card warning\">\n");
                            html.append("<div class=\"advice-icon\"></div>\n");
                            html.append("<div class=\"advice-content\">\n");
                            html.append("<div class=\"advice-title\">库存积压警告</div>\n");
                            html.append("<div class=\"advice-text\"><strong>").append(product.getName()).append("</strong> 库存").append(stock).append("件，近").append(daysSinceLastOut).append("天无出库记录，建议促销消化。</div>\n");
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
            html.append("<div style=\"color: #059669; text-align: center; padding: 15px; font-size: 14px;\">各项指标正常，库存管理良好！</div>\n");
        }
        
        html.append("</div>\n");
        html.append("</div>\n");
        
        // 异常排查建议模块
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
            html.append("<div class=\"section-title\">异常排查建议</div>\n");
            html.append("<div class=\"排查-list\">\n");
            
            for (int i = 0; i < dataManager.getInventoryList().size(); i++) {
                ZYHProduct product = dataManager.getInventoryList().get(i);
                int stock = product.getStockQuantity();
                if (stock < 0) {
                    html.append("<div class=\"排查-item\">\n");
                    html.append("<div class=\"排查-header\">\n");
                    html.append("<div class=\"排查-icon\"></div>\n");
                    html.append("<div class=\"排查-name\">").append(product.getName()).append("</div>\n");
                    html.append("</div>\n");
                    html.append("<div class=\"排查-stock\">当前库存: <span>").append(stock).append("</span> (异常)</div>\n");
                    html.append("<div class=\"排查-suggest\">\n");
                    html.append("<div class=\"排查-suggest-title\">排查建议：请检查是否存在以下问题：</div>\n");
                    html.append("<ul>\n");
                    html.append("<li>出库记录是否重复录入或数量错误</li>\n");
                    html.append("<li>入库记录是否正确录入，数量是否准确</li>\n");
                    html.append("<li>是否存在未经授权的数据修改</li>\n");
                    html.append("<li>出入库时间顺序是否存在逻辑错误</li>\n");
                    html.append("</ul>\n");
                    html.append("</div>\n");
                    html.append("</div>\n");
                }
            }
            
            html.append("</div>\n");
            html.append("</div>\n");
        }
        
        html.append("</div>\n");
        
        // 页脚
        html.append("<div class=\"footer\">\n");
        html.append("邹宇恒库存管理系统 - 数据分析报告\n");
        html.append("</div>\n");
        
        html.append("</div>\n");
        html.append("</body>\n");
        html.append("</html>");
        
        // 保存文件
        try {
            String fileName = "库存数据分析报告_" + java.time.LocalDateTime.now().toString().replace(":", "-").substring(0, 19) + ".html";
            java.io.FileWriter writer = new java.io.FileWriter(fileName);
            writer.write(html.toString());
            writer.close();
            
            java.io.File htmlFile = new java.io.File(fileName);
            java.awt.Desktop.getDesktop().browse(htmlFile.toURI());
            
            playSound("complete");
            JOptionPane.showMessageDialog(this, "HTML报表已生成并打开！\n\n文件位置: " + htmlFile.getAbsolutePath());
            log("[报表] HTML报表生成完成: " + fileName);
        } catch (java.io.IOException e) {
            playSound("error");
            JOptionPane.showMessageDialog(this, "生成报表失败: " + e.getMessage());
            log("[报表] HTML报表生成失败: " + e.getMessage());
        }
    }

    private void runPerformanceTest() {
        int testSize = 50000;
        StringBuilder result = new StringBuilder();
        result.append("========== 顺序表 vs 链表 性能对比 ==========\n\n");
        result.append("测试数据量: ").append(testSize).append(" 条\n");
        result.append("测试环境: Java 数组 vs 双向链表\n\n");

        ZYHSequentialList<Integer> seqList = new ZYHSequentialList<>();
        ZYHLinkedList<Integer> linkedList = new ZYHLinkedList<>();
        String testBarcode = "TEST" + System.currentTimeMillis() % 10000;

        long startTime, endTime;
        long seqTime, linkTime;

        result.append("【1. 尾部添加（入库操作）】\n");
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

        result.append("顺序表: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("链表: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("结论: ").append(seqTime < linkTime ? "顺序表更快" : "链表更快").append("\n\n");

        result.append("【2. 尾部删除（出库操作）】\n");
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

        result.append("顺序表: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("链表: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("结论: ").append(seqTime < linkTime ? "顺序表更快" : "链表更快").append("\n\n");

        for (int i = 0; i < testSize; i++) {
            seqList.add(i);
            linkedList.addLast(i);
        }

        result.append("【3. 搜索操作（按索引查找）】\n");
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

        result.append("顺序表: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms\n");
        result.append("链表: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms\n");
        result.append("结论: ").append(seqTime < linkTime ? "顺序表更快（支持随机访问）" : "链表更快").append("\n\n");

        result.append("【4. 中间插入（需移动元素）】\n");
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

        result.append("顺序表: ").append(String.format("%.2f", seqTime / 1_000_000.0)).append(" ms（需移动元素）\n");
        result.append("链表: ").append(String.format("%.2f", linkTime / 1_000_000.0)).append(" ms（修改指针）\n");
        result.append("结论: ").append(seqTime > linkTime ? "链表更快（无需移动元素）" : "顺序表更快").append("\n\n");

        JTextArea textArea = new JTextArea(result.toString());
        textArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "性能对比结果", JOptionPane.INFORMATION_MESSAGE);

        log("[性能测试] 测试完成，数据量: " + testSize);
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
            log("[报表] 导出到: " + file.getAbsolutePath());
            JOptionPane.showMessageDialog(this, "报表导出成功！");
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
        log("[图表] 刷新完成");
    }

    private void runComparison(int size) {
        log("[性能比对] 开始比对，数据量: " + size);
        ZYHDataManager.PerformanceComparison result = dataManager.comparePerformance(size);
        log("[性能比对] 完成");
        log(result.toString());

        JOptionPane.showMessageDialog(this, "比对完成，请查看日志面板的详细结果！");
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
            // 不绘制边框
        }
    }

    private void deleteSelectedInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的商品");
            return;
        }

        String barcode = (String) inventoryTableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除商品 '" + barcode + "' 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
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

        JOptionPane.showMessageDialog(this, "商品删除成功！");
        log("[库存管理] 删除商品: " + barcode);
    }

    private void openFilterDialog(String filterType) {
        ZYHFilterDialog dialog = new ZYHFilterDialog(this, filterType, dataManager);
        dialog.setVisible(true);
    }

    private void deleteSelectedInbound() {
        int selectedRow = inboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的入库记录");
            return;
        }

        String recordId = (String) inboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) inboundTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除入库记录 '" + recordId + "' 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
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

        JOptionPane.showMessageDialog(this, "入库记录删除成功！库存已重新计算");
        log("[入库管理] 删除入库记录: " + recordId);
    }

    private void deleteSelectedOutbound() {
        int selectedRow = outboundTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的出库记录");
            return;
        }

        String recordId = (String) outboundTableModel.getValueAt(selectedRow, 0);
        String barcode = (String) outboundTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除出库记录 '" + recordId + "' 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
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

        JOptionPane.showMessageDialog(this, "出库记录删除成功！库存已重新计算");
        log("[出库管理] 删除出库记录: " + recordId);
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
            inventoryTableModel.setValueAt(newStock >= 0 ? "正常" : "异常", i, 5);
        }
    }

    private void resetDataFromAPI() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "确定要重置所有数据吗？这将清空所有操作并重新从API加载原始数据。", 
            "确认重置", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            log("开始重置数据...");
            
            dataManager.getInventoryList().clear();
            dataManager.getInboundQueue().clear();
            dataManager.getOutboundStack().clear();
            dataManager.getTransactionFlow().clear();
            
            inventoryTableModel.setRowCount(0);
            inboundTableModel.setRowCount(0);
            outboundTableModel.setRowCount(0);
            
            loadDataFromAPI();
            
            JOptionPane.showMessageDialog(this, "数据重置完成！已重新从API加载原始数据。");
            log("数据重置完成");
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
     * 展示数据分析对话框
     * 统计各商品的入库总量、出库总量、当前库存，并标记异常状态
     */
    private void showAnalysisDialog() {
        JDialog dialog = new JDialog(this, "库存数据分析", true);
        dialog.setSize(1200, 850);
        dialog.setLocationRelativeTo(this);

        // 统计出入库数据
        java.util.Map<String, Integer> inboundStats = new java.util.HashMap<>();
        java.util.Map<String, Integer> outboundStats = new java.util.HashMap<>();
        java.util.Map<String, Integer> inboundCount = new java.util.HashMap<>();
        java.util.Map<String, Integer> outboundCount = new java.util.HashMap<>();
        java.util.Map<String, String> lastInboundDate = new java.util.HashMap<>();
        java.util.Map<String, String> lastOutboundDate = new java.util.HashMap<>();

        // 统计入库总量和次数
        for (int i = 0; i < dataManager.getInboundQueue().size(); i++) {
            ZYHInboundRecord record = dataManager.getInboundQueue().get(i);
            String barcode = record.getBarcode();
            inboundStats.put(barcode, inboundStats.getOrDefault(barcode, 0) + record.getQuantity());
            inboundCount.put(barcode, inboundCount.getOrDefault(barcode, 0) + 1);
            lastInboundDate.put(barcode, record.getDate());
        }

        // 统计出库总量和次数
        for (int i = 0; i < dataManager.getOutboundStack().size(); i++) {
            ZYHOutboundRecord record = dataManager.getOutboundStack().get(i);
            String barcode = record.getBarcode();
            outboundStats.put(barcode, outboundStats.getOrDefault(barcode, 0) + record.getQuantity());
            outboundCount.put(barcode, outboundCount.getOrDefault(barcode, 0) + 1);
            lastOutboundDate.put(barcode, record.getDate());
        }

        // 创建分析数据列表
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

            // 判断状态和等级
            if (currentStock < 0) {
                status = "异常";
                level = "";
                abnormalCount++;
                warningList.add(new WarningItem(barcode, name, "库存异常", "库存为负数: " + currentStock));
            } else if (currentStock == 0) {
                status = "缺货";
                level = "";
                abnormalCount++;
                warningList.add(new WarningItem(barcode, name, "库存不足", "库存为0，需立即补货"));
            } else if (currentStock <= 5) {
                status = "预警";
                level = "";
                warningList.add(new WarningItem(barcode, name, "库存预警", "库存偏低: " + currentStock + "件"));
            } else {
                status = "正常";
                level = "";
            }

            // 检查库存积压（超过30天未出库）
            String lastOut = lastOutboundDate.get(barcode);
            if (lastOut != null && outbound > 0) {
                try {
                    java.time.LocalDate outDate = java.time.LocalDate.parse(lastOut);
                    java.time.LocalDate today = java.time.LocalDate.now();
                    long daysSinceOutbound = java.time.temporal.ChronoUnit.DAYS.between(outDate, today);
                    if (daysSinceOutbound > 30 && currentStock > 10) {
                        backlogCount++;
                        warningList.add(new WarningItem(barcode, name, "库存积压", "已" + daysSinceOutbound + "天未出库，库存: " + currentStock));
                    }
                } catch (Exception e) {
                }
            }

            analysisList.add(new AnalysisData(barcode, name, inbound, outbound, currentStock, status, level));

            // 添加到Top排行
            topInboundList.add(new TopProduct(barcode, name, inbound, inboundCount.getOrDefault(barcode, 0)));
            topOutboundList.add(new TopProduct(barcode, name, outbound, outboundCount.getOrDefault(barcode, 0)));
        }

        // 排序Top榜单
        topInboundList.sort((a, b) -> Integer.compare(b.totalQuantity, a.totalQuantity));
        topOutboundList.sort((a, b) -> Integer.compare(b.totalQuantity, a.totalQuantity));

        // 创建主面板 - 使用BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND);

        // ===== 顶部：统计摘要 =====
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        summaryPanel.setBackground(COLOR_WHITE);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        summaryPanel.add(createSummaryCard("商品总数", String.valueOf(analysisList.size()), COLOR_1));
        summaryPanel.add(createSummaryCard("异常商品", String.valueOf(abnormalCount), new Color(239, 68, 68)));
        summaryPanel.add(createSummaryCard("积压商品", String.valueOf(backlogCount), new Color(234, 179, 8)));
        summaryPanel.add(createSummaryCard("库存总量", String.valueOf(analysisList.stream().mapToInt(d -> d.currentStock).sum()), COLOR_3));
        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // ===== 中部：Top榜单和预警 =====
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        // 左：入库Top5 + 出库Top5
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(createTopListPanel("入库Top 5", topInboundList, COLOR_2));
        topPanel.add(createTopListPanel("出库Top 5", topOutboundList, COLOR_3));
        centerPanel.add(topPanel);

        // 右：预警清单
        JPanel warningPanel = createWarningPanel(warningList);
        centerPanel.add(warningPanel);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ===== 底部：详细数据表格 =====
        String[] columnNames = {"商品条码", "商品名称", "入库总量", "出库总量", "当前库存", "状态"};
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
        table.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusCellRenderer());

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createTitledBorder("商品明细（点击表头可排序）"));
        tableScroll.setPreferredSize(new Dimension(1180, 280));

        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
        log("[数据分析] 打开增强分析面板");
    }

    private void showStockPredictionDialog() {
        JDialog dialog = new JDialog(this, "库存预测分析", true);
        dialog.setSize(900, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        topPanel.setBackground(BACKGROUND);
        topPanel.add(new JLabel("预测周期（周）:"));
        JComboBox<Integer> weeksCombo = new JComboBox<>(new Integer[]{2, 4, 6, 8});
        weeksCombo.setPreferredSize(new Dimension(80, 30));
        topPanel.add(weeksCombo);
        JButton predictBtn = createActionButton("开始预测", COLOR_1);
        topPanel.add(predictBtn);

        String[] columnNames = {"条码", "商品名称", "当前库存", "周均出库", "预测周出库", "预计缺货日期", "建议补货量", "状态"};
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

            String status = "充足";
            String stockOutDate = "-";
            int suggestRestock = 0;

            if (currentStock <= 0) {
                status = "缺货";
                stockOutDate = "已缺货";
                suggestRestock = (int) (avgWeeklyOutbound * 4);
            } else if (avgWeeklyOutbound > 0) {
                int weeksUntilStockOut = (int) (currentStock / avgWeeklyOutbound);
                if (weeksUntilStockOut <= 0) {
                    status = "紧急";
                    stockOutDate = "本周缺货";
                } else if (weeksUntilStockOut <= 2) {
                    status = "预警";
                    stockOutDate = java.time.LocalDate.now().plusWeeks(weeksUntilStockOut).toString();
                } else {
                    stockOutDate = java.time.LocalDate.now().plusWeeks(weeksUntilStockOut).toString();
                }
                suggestRestock = (int) (avgWeeklyOutbound * 4);
            } else if (totalOutbound > 0) {
                status = "正常";
            } else {
                status = "无出库";
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
        predictTable.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        predictTable.setRowHeight(28);
        predictTable.setAutoCreateRowSorter(true);

        predictTable.getColumnModel().getColumn(7).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = String.valueOf(value);
                switch (status) {
                    case "缺货":
                    case "紧急":
                        c.setForeground(new Color(239, 68, 68));
                        break;
                    case "预警":
                        c.setForeground(new Color(234, 179, 8));
                        break;
                    case "充足":
                    case "正常":
                        c.setForeground(new Color(34, 197, 94));
                        break;
                    default:
                        c.setForeground(new Color(107, 114, 128));
                }
                return c;
            }
        });

        JScrollPane tableScroll = new JScrollPane(predictTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("预测结果（点击表头可排序）"));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(BACKGROUND);
        infoPanel.add(new JLabel("<html><b>说明：</b>周均出库基于历史数据计算，预测周出库考虑增长趋势（+10%）。<br>建议补货量按4周安全库存计算。状态为\"无出库\"的商品表示暂无销售数据。</html>"));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        predictBtn.addActionListener(e -> {
            playSound("success");
            JOptionPane.showMessageDialog(dialog, "预测分析完成！\n共分析 " + predictData.size() + " 个商品");
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
        log("[库存预测] 打开预测分析面板");
    }

    /**
     * 创建统计卡片
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setBackground(COLOR_WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(107, 114, 128));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
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
     * 创建Top榜单面板
     */
    private JPanel createTopListPanel(String title, java.util.List<TopProduct> list, Color barColor) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(COLOR_WHITE);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(COLOR_WHITE);
        listPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        // 限制显示数量，提高滚动效率
        int count = Math.min(8, list.size());
        int maxQuantity = count > 0 ? list.get(0).totalQuantity : 1;

        for (int i = 0; i < count; i++) {
            TopProduct product = list.get(i);
            JPanel itemPanel = new JPanel(new BorderLayout(5, 0));
            itemPanel.setMaximumSize(new Dimension(450, 32));
            itemPanel.setBackground(COLOR_WHITE);

            JLabel rankLabel = new JLabel(" " + (i + 1) + ".");
            rankLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            rankLabel.setPreferredSize(new Dimension(25, 28));

            JLabel nameLabel = new JLabel(product.name);
            nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            nameLabel.setPreferredSize(new Dimension(150, 28));

            double ratio = (double) product.totalQuantity / maxQuantity;
            int barWidth = (int) (ratio * 120);

            // 使用JProgressBar替代自定义绘制，提高性能
            JProgressBar progressBar = new JProgressBar(0, maxQuantity);
            progressBar.setValue(product.totalQuantity);
            progressBar.setForeground(barColor);
            progressBar.setBackground(new Color(240, 240, 240));
            progressBar.setPreferredSize(new Dimension(120, 22));
            progressBar.setBorderPainted(false);
            progressBar.setStringPainted(false);

            JLabel countLabel = new JLabel(String.valueOf(product.totalQuantity));
            countLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
            countLabel.setForeground(barColor);
            countLabel.setPreferredSize(new Dimension(50, 28));

            itemPanel.add(rankLabel, BorderLayout.WEST);
            itemPanel.add(nameLabel, BorderLayout.CENTER);
            itemPanel.add(progressBar, BorderLayout.EAST);
            itemPanel.add(countLabel, BorderLayout.LINE_END);

            listPanel.add(itemPanel);
        }

        if (count == 0) {
            JLabel emptyLabel = new JLabel("暂无数据");
            emptyLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(156, 163, 175));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(emptyLabel);
        }

        // 添加滚动条，减小高度
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(480, 160));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 创建预警面板
     */
    private JPanel createWarningPanel(java.util.List<WarningItem> warningList) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("预警清单"));
        panel.setBackground(COLOR_WHITE);

        if (warningList.isEmpty()) {
            JLabel noWarning = new JLabel("暂无预警");
            noWarning.setFont(new Font("微软雅黑", Font.PLAIN, 16));
            noWarning.setForeground(new Color(34, 197, 94));
            noWarning.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(noWarning, BorderLayout.CENTER);
            return panel;
        }

        String[] columns = {"商品名称", "预警类型", "说明"};
        java.util.List<Object[]> data = new java.util.ArrayList<>();
        for (WarningItem item : warningList) {
            data.add(new Object[]{item.name, item.type, item.description});
        }

        DefaultTableModel warningModel = new DefaultTableModel(data.toArray(new Object[0][]), columns);
        JTable warningTable = new JTable(warningModel);
        warningTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        warningTable.setRowHeight(35);
        warningTable.setAutoCreateRowSorter(true);

        // 设置列宽
        warningTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        warningTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        warningTable.getColumnModel().getColumn(2).setPreferredWidth(200);

        warningTable.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String type = (String) value;
                if ("库存异常".equals(type) || "库存不足".equals(type)) {
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
     * 分析数据内部类
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
     * Top商品数据类
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
     * 预警项数据类
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
     * 状态列单元格渲染器
     */
    private static class StatusCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String status = (String) value;
            if (status.contains("正常")) {
                setForeground(new Color(34, 197, 94));
            } else if (status.contains("预警")) {
                setForeground(new Color(234, 179, 8));
            } else if (status.contains("缺货")) {
                setForeground(new Color(239, 68, 68));
            } else if (status.contains("异常")) {
                setForeground(new Color(37, 37, 37));
            }

            setHorizontalAlignment(CENTER);
            return this;
        }
    }
}
