package org.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import org.example.utils.PayrollServiceChecker;

public class PayrollService {

  public static final Color MAC_CARD_BG = Color.WHITE;
  public static final Color MAC_ACCENT = new Color(0, 122, 255);
  public static final Color MAC_TEXT_DARK = new Color(29, 29, 31);
  public static final Color MAC_BORDER = new Color(210, 210, 215);

  public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
  public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
  public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

  private static DefaultTableModel tableModel;
  private static JTable dataTable;

  private static final String[] ALL_CSV_COLUMNS = {
    "Employee #",
    "Last Name",
    "First Name",
    "Birthday",
    "Address",
    "Phone Number",
    "SSS #",
    "PhilHealth #",
    "TIN",
    "Pag-IBIG #",
    "Status",
    "Position",
    "Immediate Supervisor",
    "Basic Salary",
    "Rice Subsidy",
    "Phone Allowance",
    "Clothing Allowance",
    "Gross Semi-Monthly Rate",
    "Hourly Rate",
  };

  private static final String[] MONTH_OPTIONS = {
    "June", "July", "August", "September", "October", "November", "December",
  };

  private static final String[] CUTOFF_OPTIONS = {
    "1st Cutoff (1-15)", "2nd Cutoff (16-End)",
  };

  public static JPanel createEmployeePanel() {
    JPanel basePanel = new JPanel(new BorderLayout());
    basePanel.setBackground(new Color(245, 245, 247));
    basePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

    return basePanel;
  }

  public static JPanel createPayrollPanel(String loginRole, String loggedInUserUsername) {
    boolean isEmployee = "employee".equalsIgnoreCase(loginRole);

    if (isEmployee) {
      // The employee view only exposes a narrow self-service payslip flow.
      JPanel mainEmployeePanel = new JPanel(new BorderLayout());
      mainEmployeePanel.setBackground(new Color(245, 245, 247));
      mainEmployeePanel.setBorder(new EmptyBorder(40, 40, 40, 40));

      boolean restrictToLoggedInEmployee = Account.employeeData.containsKey(loggedInUserUsername);

      JPanel centerCard = new JPanel();
      centerCard.setLayout(new BoxLayout(centerCard, BoxLayout.Y_AXIS));
      centerCard.setBackground(Color.WHITE);
      centerCard.setBorder(
          BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(45, 45, 45, 45)));

      JLabel portalHeader = new JLabel("Employee Online Payslip Portal");
      portalHeader.setFont(new Font("Segoe UI", Font.BOLD, 22));
      portalHeader.setForeground(MAC_TEXT_DARK);
      portalHeader.setAlignmentX(Component.CENTER_ALIGNMENT);

      JLabel portalSub = new JLabel("Verify your credentials.");
      portalSub.setFont(FONT_BODY);
      portalSub.setForeground(new java.awt.Color(134, 134, 139));
      portalSub.setAlignmentX(Component.CENTER_ALIGNMENT);

      JPanel selectionRibbonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
      selectionRibbonPanel.setOpaque(false);

      JComboBox<String> monthSelector = new JComboBox<>(MONTH_OPTIONS);
      monthSelector.setFont(FONT_BOLD);
      monthSelector.setMaximumSize(new Dimension(180, 36));

      JComboBox<String> cutoffSelector = new JComboBox<>(CUTOFF_OPTIONS);
      cutoffSelector.setFont(FONT_BOLD);
      cutoffSelector.setMaximumSize(new Dimension(180, 36));

      selectionRibbonPanel.add(new JLabel("Month:"));
      selectionRibbonPanel.add(monthSelector);
      selectionRibbonPanel.add(new JLabel("Cutoff:"));
      selectionRibbonPanel.add(cutoffSelector);

      JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
      actionRow.setOpaque(false);

      JLabel idSearchLabel = new JLabel("Enter Employee ID:");
      idSearchLabel.setFont(FONT_BOLD);

      JTextField empSearchField = new JTextField(12);
      empSearchField.setFont(FONT_BODY);
      empSearchField.setPreferredSize(new Dimension(150, 36));
      empSearchField.setBorder(
          BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(4, 8, 4, 8)));

      if (restrictToLoggedInEmployee) {
        empSearchField.setText(loggedInUserUsername);
      }

      JButton viewPayslipBtn = new JButton("Open Payslip");
      styleMacButton(viewPayslipBtn, new Color(40, 167, 69), Color.WHITE);
      viewPayslipBtn.setPreferredSize(new Dimension(210, 36));

      actionRow.add(idSearchLabel);
      actionRow.add(empSearchField);
      actionRow.add(viewPayslipBtn);

      viewPayslipBtn.addActionListener(
          e -> {
            String inputId = empSearchField.getText().trim();

            String[] matchedRecord = Account.findEmployeeRecord(inputId);
            if (!PayrollServiceChecker.validatePayslipRequest(
                mainEmployeePanel,
                inputId,
                restrictToLoggedInEmployee,
                loggedInUserUsername,
                matchedRecord)) {
              if (restrictToLoggedInEmployee) {
                empSearchField.setText(loggedInUserUsername);
              }
              return;
            }

            String selectedMonth = (String) monthSelector.getSelectedItem();
            boolean isSecondCutoff = cutoffSelector.getSelectedIndex() == 1;
            executePayrollEngineCalculations(
                mainEmployeePanel, inputId, selectedMonth, isSecondCutoff);
          });

      centerCard.add(Box.createVerticalGlue());
      centerCard.add(portalHeader);
      centerCard.add(Box.createVerticalStrut(10));
      centerCard.add(portalSub);
      centerCard.add(Box.createVerticalStrut(25));
      centerCard.add(selectionRibbonPanel);
      centerCard.add(Box.createVerticalStrut(15));
      centerCard.add(actionRow);
      centerCard.add(Box.createVerticalGlue());

      mainEmployeePanel.add(centerCard, BorderLayout.CENTER);
      return mainEmployeePanel;
    } else {
      // Admin dashboard includes search, record maintenance, and payroll computation.
      JPanel basePanel = new JPanel(new BorderLayout(15, 15));
      basePanel.setOpaque(false);
      basePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

      JPanel topRibbon = new JPanel();
      topRibbon.setLayout(new BoxLayout(topRibbon, BoxLayout.X_AXIS));
      topRibbon.setOpaque(false);

      JButton addEmpBtn = new JButton("+ Add Employee");
      styleMacButton(addEmpBtn, MAC_ACCENT, Color.WHITE);

      JTextField searchField = new JTextField(15);
      searchField.setMaximumSize(new Dimension(180, 36));
      searchField.setBorder(
          BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(4, 8, 4, 8)));

      JButton filterBtn = new JButton("Search");
      styleMacButton(filterBtn, MAC_TEXT_DARK, Color.WHITE);

      JComboBox<String> adminMonthSelector = new JComboBox<>(MONTH_OPTIONS);
      adminMonthSelector.setFont(FONT_BOLD);
      adminMonthSelector.setMaximumSize(new Dimension(180, 36));

      JComboBox<String> adminCutoffSelector = new JComboBox<>(CUTOFF_OPTIONS);
      adminCutoffSelector.setFont(FONT_BOLD);
      adminCutoffSelector.setMaximumSize(new Dimension(180, 36));

      JButton calculatePayrollBtn = new JButton("Compute Salaries");
      styleMacButton(calculatePayrollBtn, new Color(40, 167, 69), Color.WHITE);

      topRibbon.add(addEmpBtn);
      topRibbon.add(Box.createHorizontalStrut(15));
      topRibbon.add(new JLabel("Search ID: "));
      topRibbon.add(searchField);
      topRibbon.add(Box.createHorizontalStrut(8));
      topRibbon.add(filterBtn);
      topRibbon.add(Box.createHorizontalStrut(15));
      topRibbon.add(adminMonthSelector);
      topRibbon.add(Box.createHorizontalStrut(8));
      topRibbon.add(adminCutoffSelector);
      topRibbon.add(Box.createHorizontalGlue());
      topRibbon.add(calculatePayrollBtn);
      basePanel.add(topRibbon, BorderLayout.NORTH);

      tableModel =
          new DefaultTableModel(ALL_CSV_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
              return false;
            }
          };

      dataTable = new JTable(tableModel);
      dataTable.setRowHeight(35);
      dataTable.setFont(FONT_BODY);
      dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      JTableHeader header = dataTable.getTableHeader();
      header.setFont(FONT_BOLD);
      header.setBackground(Color.WHITE);

      for (int i = 0; i < ALL_CSV_COLUMNS.length; i++) {
        dataTable.getColumnModel().getColumn(i).setPreferredWidth(145);
      }

      JScrollPane scrollPane =
          new JScrollPane(
              dataTable,
              JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
              JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
      scrollPane.getViewport().setBackground(Color.WHITE);
      basePanel.add(scrollPane, BorderLayout.CENTER);

      JPanel bottomStrip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
      bottomStrip.setOpaque(false);

      JButton editBtn = new JButton("Edit Selected");
      styleMacButton(editBtn, Color.WHITE, MAC_TEXT_DARK);
      editBtn.setBorder(BorderFactory.createLineBorder(MAC_BORDER));

      JButton deleteBtn = new JButton("Delete Record");
      styleMacButton(deleteBtn, new Color(220, 53, 69), Color.WHITE);

      bottomStrip.add(editBtn);
      bottomStrip.add(deleteBtn);
      basePanel.add(bottomStrip, BorderLayout.SOUTH);

      // Seed the table so the admin view opens with the full roster.
      syncTableGridRows("");

      filterBtn.addActionListener(
          e -> {
            String searchInputStr = searchField.getText().trim();

            if (searchInputStr.isEmpty()) {
              JOptionPane.showMessageDialog(
                  basePanel,
                  "Invalid Input: Please enter a valid Employee ID to search.",
                  "Search Error",
                  JOptionPane.WARNING_MESSAGE);
              // Restores all rows instead of leaving the table blank.
              syncTableGridRows("");
              return;
            }

            syncTableGridRows(searchInputStr);
          });

      addEmpBtn.addActionListener(e -> launchAddRecordModal(basePanel));

      editBtn.addActionListener(
          e -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow == -1) {
              JOptionPane.showMessageDialog(
                  basePanel,
                  "Please select an employee row from the table view registry.",
                  "Selection Blank",
                  JOptionPane.WARNING_MESSAGE);
              return;
            }
            String selectedId = (String) tableModel.getValueAt(selectedRow, 0);
            launchEditRecordModal(basePanel, selectedId);
          });

      deleteBtn.addActionListener(
          e -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow == -1) {
              JOptionPane.showMessageDialog(
                  basePanel,
                  "Please select an employee record line item to remove.",
                  "Selection Blank",
                  JOptionPane.WARNING_MESSAGE);
              return;
            }
            String selectedId = (String) tableModel.getValueAt(selectedRow, 0);
            int promptResult =
                JOptionPane.showConfirmDialog(
                    basePanel,
                    "Delete Employee row record " + selectedId + "?",
                    "Verify Operation",
                    JOptionPane.YES_NO_OPTION);
            if (promptResult == JOptionPane.YES_OPTION) {
              // Updates the in-memory map first, then persists the new file state.
              Account.employeeData.remove(selectedId);
              Account.saveEmployeeDataToCsv();
              syncTableGridRows("");
            }
          });

      calculatePayrollBtn.addActionListener(
          e -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow == -1) {
              JOptionPane.showMessageDialog(
                  basePanel,
                  "Please select an employee from the table list to calculate salary metrics.",
                  "Notice",
                  JOptionPane.INFORMATION_MESSAGE);
              return;
            }
            String selectedId = (String) tableModel.getValueAt(selectedRow, 0);
            String selectedMonth = (String) adminMonthSelector.getSelectedItem();
            boolean isSecondCutoff = adminCutoffSelector.getSelectedIndex() == 1;
            executePayrollEngineCalculations(basePanel, selectedId, selectedMonth, isSecondCutoff);
          });

      return basePanel;
    }
  }

  public static void syncTableGridRows(String targetFilterId) {
    if (tableModel == null) return;
    tableModel.setRowCount(0);
    for (List<String> row : Account.employeeData.values()) {
      if (row.size() < 19) continue;
      String id = row.get(0);

      // Empty filter means show everything; otherwise, only the matched record is rendered.
      if (targetFilterId.isEmpty() || id.equalsIgnoreCase(targetFilterId)) {
        tableModel.addRow(row.toArray());
      }
    }
  }

  private static void styleMacButton(JButton btn, Color bg, Color fg) {
    btn.setFont(FONT_BOLD);
    btn.setBackground(bg);
    btn.setForeground(fg);
    btn.setOpaque(true);
    btn.setBorderPainted(false);
    btn.setFocusPainted(false);
    btn.setPreferredSize(new Dimension(140, 36));
  }

  private static void executePayrollEngineCalculations(
      Component anchor, String empId, String selectedMonth, boolean isSecondCutoff) {
    String[] empData = Account.findEmployeeRecord(empId);
    if (empData == null) return;

    double hourlyRate = 0.0;
    try {
      hourlyRate = Double.parseDouble(empData[18].trim().replace("\"", ""));
    } catch (Exception ex) {
      hourlyRate = 0.0;
    }

    int monthNumber = Account.getMonthNumber(selectedMonth);
    if (monthNumber < 6 || monthNumber > 12) {
      JOptionPane.showMessageDialog(
          anchor,
          "Please select a month from June through December.",
          "Invalid Month",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    Account.loadAttendanceDataFromCsv();
    LocalDate latestAttendanceDate = Account.getLatestAttendanceDate(empId);
    if (latestAttendanceDate == null) {
      JOptionPane.showMessageDialog(
          anchor,
          "No attendance records were found for Employee ID " + empId + ".",
          "Attendance Missing",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    YearMonth payrollMonth = YearMonth.of(latestAttendanceDate.getYear(), monthNumber);
    int firstCutoffStartDay = 1;
    int firstCutoffEndDay = 15;
    int secondCutoffStartDay = 16;
    int secondCutoffEndDay = payrollMonth.lengthOfMonth();

    double firstCutoffHours =
        Account.getTotalHoursForPeriod(
            empId, selectedMonth, firstCutoffStartDay, firstCutoffEndDay);
    double secondCutoffHours =
        Account.getTotalHoursForPeriod(
            empId, selectedMonth, secondCutoffStartDay, secondCutoffEndDay);

    double attendanceHours = isSecondCutoff ? secondCutoffHours : firstCutoffHours;

    if (attendanceHours <= 0.0) {
      JOptionPane.showMessageDialog(
          anchor,
          "No attendance records were found for Employee ID " + empId + ".",
          "Attendance Missing",
          JOptionPane.WARNING_MESSAGE);
      return;
    }

    double grossPayPeriod = hourlyRate * attendanceHours;
    double monthlyGrossPay = hourlyRate * (firstCutoffHours + secondCutoffHours);

    double sssPeriodDeduction = 0.0;
    double phPeriodDeduction = 0.0;
    double piPeriodDeduction = 0.0;
    double taxWithholdingPeriod = 0.0;

    if (isSecondCutoff) {
      sssPeriodDeduction = computeSSS(monthlyGrossPay);
      phPeriodDeduction = computePhilHealth(monthlyGrossPay);
      piPeriodDeduction = computePagIBIG(monthlyGrossPay);

      double totalPreTaxDeductions = sssPeriodDeduction + phPeriodDeduction + piPeriodDeduction;
      double taxableIncomeThisPeriod = monthlyGrossPay - totalPreTaxDeductions;
      taxWithholdingPeriod = computeIncomeTax(taxableIncomeThisPeriod);
    }

    double totalPreTaxDeductions = sssPeriodDeduction + phPeriodDeduction + piPeriodDeduction;

    double totalFinalDeductions = totalPreTaxDeductions + taxWithholdingPeriod;
    double netPayPeriodTotal = grossPayPeriod - totalFinalDeductions;

    String titleHeaderString =
        Account.getMonthName(String.valueOf(monthNumber))
            + " - "
            + (isSecondCutoff ? "2nd Cutoff (16-End)" : "1st Cutoff (1-15)");

    JPanel payslipCard = new JPanel();
    payslipCard.setBackground(MAC_CARD_BG);
    payslipCard.setLayout(new BoxLayout(payslipCard, BoxLayout.Y_AXIS));
    payslipCard.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(20, 22, 20, 22)));

    JPanel accentBar = new JPanel();
    accentBar.setBackground(MAC_ACCENT);
    accentBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
    accentBar.setPreferredSize(new Dimension(1, 4));
    payslipCard.add(accentBar);
    payslipCard.add(Box.createVerticalStrut(16));

    JLabel receiptTitle = new JLabel("MOTORPH PAYSLIP");
    receiptTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
    receiptTitle.setForeground(MAC_TEXT_DARK);
    receiptTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    receiptTitle.setHorizontalAlignment(JLabel.CENTER);

    JLabel receiptSubtitle = new JLabel(titleHeaderString);
    receiptSubtitle.setFont(FONT_BODY);
    receiptSubtitle.setForeground(new Color(134, 134, 139));
    receiptSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    receiptSubtitle.setHorizontalAlignment(JLabel.CENTER);

    JLabel receiptStatus =
        new JLabel("Status Ledger: Calculated and Generated dynamically from DB.");
    receiptStatus.setFont(FONT_BODY);
    receiptStatus.setForeground(MAC_ACCENT);
    receiptStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
    receiptStatus.setHorizontalAlignment(JLabel.CENTER);

    payslipCard.add(receiptTitle);
    payslipCard.add(Box.createVerticalStrut(4));
    payslipCard.add(receiptSubtitle);
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(receiptStatus);
    payslipCard.add(Box.createVerticalStrut(18));
    payslipCard.add(createReceiptDivider());
    payslipCard.add(Box.createVerticalStrut(14));

    payslipCard.add(createReceiptRow("Employee ID", empId, false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(createReceiptRow("Employee Name", empData[1] + ", " + empData[2], false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(createReceiptRow("Position Title", empData[11], false));
    payslipCard.add(Box.createVerticalStrut(14));
    payslipCard.add(createReceiptDivider());
    payslipCard.add(Box.createVerticalStrut(14));

    payslipCard.add(createReceiptSectionHeader("Earnings Breakdown"));
    payslipCard.add(Box.createVerticalStrut(8));
    payslipCard.add(
        createReceiptRow(
            "Hours worked", String.format("%.2f Hours worked", attendanceHours), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(
        createReceiptRow("Hourly Rate", String.format("PHP %.2f / hr", hourlyRate), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(createReceiptRow("GROSS PAY", String.format("PHP %.2f", grossPayPeriod), true));
    payslipCard.add(Box.createVerticalStrut(14));
    payslipCard.add(createReceiptDivider());
    payslipCard.add(Box.createVerticalStrut(14));

    payslipCard.add(createReceiptSectionHeader("Government Deductions"));
    payslipCard.add(Box.createVerticalStrut(8));
    payslipCard.add(
        createReceiptRow("SSS Contribution", String.format("PHP %.2f", sssPeriodDeduction), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(
        createReceiptRow(
            "PhilHealth Contribution", String.format("PHP %.2f", phPeriodDeduction), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(
        createReceiptRow("Pag-IBIG Premium", String.format("PHP %.2f", piPeriodDeduction), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(
        createReceiptRow(
            "Withholding Income Tax", String.format("PHP %.2f", taxWithholdingPeriod), false));
    payslipCard.add(Box.createVerticalStrut(6));
    payslipCard.add(
        createReceiptRow(
            "TOTAL DEDUCTIONS", String.format("PHP %.2f", totalFinalDeductions), true));
    payslipCard.add(Box.createVerticalStrut(14));
    payslipCard.add(createReceiptDivider());
    payslipCard.add(Box.createVerticalStrut(14));

    JPanel netPayPanel = new JPanel(new BorderLayout());
    netPayPanel.setBackground(new Color(235, 245, 255));
    netPayPanel.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 215, 255), 1),
            new EmptyBorder(12, 14, 12, 14)));
    netPayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
    JLabel netPayLabel = new JLabel("NET PAY");
    netPayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    netPayLabel.setForeground(MAC_TEXT_DARK);
    JLabel netPayValue = new JLabel(String.format("PHP %.2f", netPayPeriodTotal));
    netPayValue.setFont(new Font("Segoe UI", Font.BOLD, 16));
    netPayValue.setForeground(MAC_ACCENT);
    netPayValue.setHorizontalAlignment(JLabel.RIGHT);
    netPayPanel.add(netPayLabel, BorderLayout.WEST);
    netPayPanel.add(netPayValue, BorderLayout.EAST);
    payslipCard.add(netPayPanel);

    JScrollPane internalPane = new JScrollPane(payslipCard);
    internalPane.setBorder(null);
    internalPane.setPreferredSize(new Dimension(560, 520));

    JOptionPane.showMessageDialog(
        anchor, internalPane, "Official MotorPH Payroll  Receipt", JOptionPane.INFORMATION_MESSAGE);
  }

  public static double computeSSS(double gross) {
    // Salary lower bounds from the SSS table.
    double[] salaryLimits = {
      0, 3250, 3750, 4250, 4750, 5250, 5750, 6250, 6750, 7250, 7750, 8250, 8750, 9250, 9750, 10250,
      10750, 11250, 11750, 12250, 12750, 13250, 13750, 14250, 14750, 15250, 15750, 16250, 16750,
      17250, 17750, 18250, 18750, 19250, 19750, 20250, 20750, 21250, 21750, 22250, 22750, 23250,
      23750, 24250, 24750,
    };

    // Corresponding contributions from the SSS table.
    double[] contributions = {
      135.0, 157.5, 180.0, 202.5, 225.0, 247.5, 270.0, 292.5, 315.0, 337.5, 360.0, 382.5, 405.0,
      427.5, 450.0, 472.5, 495.0, 517.5, 540.0, 562.5, 585.0, 607.5, 630.0, 652.5, 675.0, 697.5,
      720.0, 742.5, 765.0, 787.5, 810.0, 832.5, 855.0, 877.5, 900.0, 922.5, 945.0, 967.5, 990.0,
      1012.5, 1035.0, 1057.5, 1080.0, 1102.5, 1125.0,
    };

    // Iterate backwards to find which bracket the gross salary falls into.
    for (int i = salaryLimits.length - 1; i >= 0; i--) {
      if (gross >= salaryLimits[i]) {
        return contributions[i];
      }
    }
    return 135.0;
  }

  public static double computePhilHealth(double gross) {
    // PhilHealth uses the monthly premium and returns the employee share.
    double premium = (gross <= 10000) ? 300.0 : (gross >= 60000) ? 1800.0 : gross * 0.03;
    return premium / 2;
  }

  public static double computePagIBIG(double gross) {
    return Math.min(gross * ((gross <= 1500) ? 0.01 : 0.02), 100.0);
  }

  public static double computeIncomeTax(double taxable) {
    // Income tax uses the Philippine progressive tax brackets.
    if (taxable <= 20832) {
      return 0;
    }
    if (taxable <= 33332) {
      return (taxable - 20833) * 0.20;
    }
    if (taxable <= 66666) {
      return 2500 + (taxable - 33333) * 0.25;
    }
    if (taxable <= 166666) {
      return 10833 + (taxable - 66667) * 0.30;
    }
    if (taxable <= 666666) {
      return 40833.33 + (taxable - 166667) * 0.32;
    }
    return 200833.33 + (taxable - 666667) * 0.35;
  }

  private static JPanel createReceiptDivider() {
    JPanel divider = new JPanel();
    divider.setBackground(MAC_BORDER);
    divider.setMaximumSize(new Dimension(430, 1));
    divider.setPreferredSize(new Dimension(430, 1));
    divider.setAlignmentX(Component.CENTER_ALIGNMENT);
    return divider;
  }

  private static JPanel createReceiptSectionHeader(String title) {
    JPanel header = new JPanel(new BorderLayout());
    header.setOpaque(false);
    header.setMaximumSize(new Dimension(430, 24));
    header.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel label = new JLabel(title);
    label.setFont(FONT_BOLD);
    label.setForeground(MAC_ACCENT);
    label.setHorizontalAlignment(JLabel.CENTER);
    header.add(label, BorderLayout.CENTER);
    return header;
  }

  private static JPanel createReceiptRow(String labelText, String valueText, boolean emphasized) {
    JPanel row = new JPanel(new BorderLayout(16, 0));
    row.setOpaque(false);
    row.setMaximumSize(new Dimension(430, 28));
    row.setPreferredSize(new Dimension(430, 28));
    row.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel label = new JLabel(labelText);
    label.setFont(emphasized ? FONT_BOLD : FONT_BODY);
    label.setForeground(MAC_TEXT_DARK);

    JLabel value = new JLabel(valueText);
    value.setFont(emphasized ? FONT_BOLD : FONT_BODY);
    value.setForeground(emphasized ? MAC_ACCENT : MAC_TEXT_DARK);
    value.setHorizontalAlignment(JLabel.RIGHT);

    row.add(label, BorderLayout.WEST);
    row.add(value, BorderLayout.EAST);
    return row;
  }

  public static void launchAddRecordModal(Component anchor) {
    // New employee entries are collected in the same field order used by the CSV file.
    JDialog modal =
        new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(anchor), "Update/Edit Profile Entry", true);
    modal.setSize(520, 580);
    modal.setLocationRelativeTo(anchor);

    JTabbedPane formTabs = new JTabbedPane();
    formTabs.setFont(FONT_BOLD);

    JPanel personalTab = new JPanel(new GridLayout(6, 2, 10, 15));
    personalTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    String nextIdSequence = Account.getNextSequentialEmployeeId();
    JTextField txtId = new JTextField(nextIdSequence);
    txtId.setEditable(false);
    txtId.setBackground(new Color(240, 240, 242));
    JTextField txtLn = new JTextField();
    JTextField txtFn = new JTextField();
    JTextField txtBday = new JTextField("MM/DD/YYYY");
    JTextField txtAddress = new JTextField();
    JTextField txtPhone = new JTextField();

    personalTab.add(new JLabel("Employee ID (Auto):"));
    personalTab.add(txtId);
    personalTab.add(new JLabel("Last Name * :"));
    personalTab.add(txtLn);
    personalTab.add(new JLabel("First Name * :"));
    personalTab.add(txtFn);
    personalTab.add(new JLabel("Birthday:"));
    personalTab.add(txtBday);
    personalTab.add(new JLabel("Address:"));
    personalTab.add(txtAddress);
    personalTab.add(new JLabel("Phone Number:"));
    personalTab.add(txtPhone);

    JPanel gPositionTab = new JPanel(new GridLayout(7, 2, 10, 15));
    gPositionTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    JTextField txtSss = new JTextField();
    JTextField txtPh = new JTextField();
    JTextField txtTin = new JTextField();
    JTextField txtPagibig = new JTextField();
    JTextField txtStatus = new JTextField("Regular");
    JTextField txtDes = new JTextField();
    JTextField txtSupervisor = new JTextField();

    gPositionTab.add(new JLabel("SSS Number:"));
    gPositionTab.add(txtSss);
    gPositionTab.add(new JLabel("PhilHealth Number:"));
    gPositionTab.add(txtPh);
    gPositionTab.add(new JLabel("TIN:"));
    gPositionTab.add(txtTin);
    gPositionTab.add(new JLabel("Pag-IBIG Number:"));
    gPositionTab.add(txtPagibig);
    gPositionTab.add(new JLabel("Employment Status:"));
    gPositionTab.add(txtStatus);
    gPositionTab.add(new JLabel("Designation / Position:"));
    gPositionTab.add(txtDes);
    gPositionTab.add(new JLabel("Immediate Supervisor:"));
    gPositionTab.add(txtSupervisor);

    JPanel salaryTab = new JPanel(new GridLayout(6, 2, 10, 15));
    salaryTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    JTextField txtBasic = new JTextField();
    JTextField txtRice = new JTextField();
    JTextField txtPhoneAll = new JTextField();
    JTextField txtCloth = new JTextField();
    JTextField txtGrossSemi = new JTextField();
    JTextField txtHourly = new JTextField();

    salaryTab.add(new JLabel("Basic Salary Base:"));
    salaryTab.add(txtBasic);
    salaryTab.add(new JLabel("Rice Subsidy Allowance:"));
    salaryTab.add(txtRice);
    salaryTab.add(new JLabel("Phone Allowance:"));
    salaryTab.add(txtPhoneAll);
    salaryTab.add(new JLabel("Clothing Allowance:"));
    salaryTab.add(txtCloth);
    salaryTab.add(new JLabel("Gross Semi-Monthly Rate:"));
    salaryTab.add(txtGrossSemi);
    salaryTab.add(new JLabel("Hourly Rate Base * :"));
    salaryTab.add(txtHourly);

    formTabs.addTab("1. Personal Details", personalTab);
    formTabs.addTab("2. Gov IDs & Status", gPositionTab);
    formTabs.addTab("3. Salary & Rates", salaryTab);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
    JButton saveBtn = new JButton("Add Employee");
    styleMacButton(saveBtn, MAC_ACCENT, Color.WHITE);
    bottomPanel.add(saveBtn);

    saveBtn.addActionListener(
        e -> {
          String cleanId = txtId.getText().trim();

          if (!PayrollServiceChecker.validateEmployeeInput(
              modal,
              txtLn.getText(),
              txtFn.getText(),
              txtBday.getText(),
              txtPhone.getText(),
              txtSss.getText(),
              txtPh.getText(),
              txtTin.getText(),
              txtPagibig.getText(),
              txtBasic.getText(),
              txtRice.getText(),
              txtPhoneAll.getText(),
              txtCloth.getText(),
              txtGrossSemi.getText(),
              txtHourly.getText())) {
            return;
          }

          // Builds one ordered record so it can be stored and reloaded without transformation.
          List<String> newRecordFields = new ArrayList<>();
          newRecordFields.add(cleanId);
          newRecordFields.add(txtLn.getText().trim());
          newRecordFields.add(txtFn.getText().trim());
          newRecordFields.add(txtBday.getText().trim());
          newRecordFields.add(txtAddress.getText().trim());
          newRecordFields.add(txtPhone.getText().trim());
          newRecordFields.add(txtSss.getText().trim());
          newRecordFields.add(txtPh.getText().trim());
          newRecordFields.add(txtTin.getText().trim());
          newRecordFields.add(txtPagibig.getText().trim());
          newRecordFields.add(
              txtStatus.getText().trim().isEmpty() ? "Regular" : txtStatus.getText().trim());
          newRecordFields.add(txtDes.getText().trim());
          newRecordFields.add(
              txtSupervisor.getText().trim().isEmpty() ? "N/A" : txtSupervisor.getText().trim());
          newRecordFields.add(
              txtBasic.getText().trim().isEmpty() ? "0" : txtBasic.getText().trim());
          newRecordFields.add(txtRice.getText().trim().isEmpty() ? "0" : txtRice.getText().trim());
          newRecordFields.add(
              txtPhoneAll.getText().trim().isEmpty() ? "0" : txtPhoneAll.getText().trim());
          newRecordFields.add(
              txtCloth.getText().trim().isEmpty() ? "0" : txtCloth.getText().trim());
          newRecordFields.add(
              txtGrossSemi.getText().trim().isEmpty() ? "0" : txtGrossSemi.getText().trim());
          newRecordFields.add(
              txtHourly.getText().trim().isEmpty() ? "0.0" : txtHourly.getText().trim());

          Account.employeeData.put(cleanId, newRecordFields);

          if (Account.saveEmployeeDataToCsv()) {
            syncTableGridRows("");
            modal.dispose();
            JOptionPane.showMessageDialog(
                anchor,
                "Success! Record Updated.",
                "Record Updated",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(
                modal,
                "CRITICAL WRITE FAULT!\n\nPlease close EmployeeData.csv if open in Excel.",
                "Sync Blocked",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    modal.setLayout(new BorderLayout());
    modal.add(formTabs, BorderLayout.CENTER);
    modal.add(bottomPanel, BorderLayout.SOUTH);
    modal.setVisible(true);
  }

  private static void launchEditRecordModal(Component anchor, String targetId) {
    List<String> currentFields = Account.employeeData.get(targetId);
    if (currentFields == null) return;

    // Mirrors the add form, but preloads the current record for safe overwrite edits.
    JDialog modal =
        new JDialog((Frame) SwingUtilities.getWindowAncestor(anchor), "Modify Profile Entry", true);
    modal.setSize(520, 580);
    modal.setLocationRelativeTo(anchor);

    JTabbedPane formTabs = new JTabbedPane();
    formTabs.setFont(FONT_BOLD);

    JPanel personalTab = new JPanel(new GridLayout(6, 2, 10, 15));
    personalTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    JTextField txtLn = new JTextField(currentFields.get(1));
    JTextField txtFn = new JTextField(currentFields.get(2));
    JTextField txtBday = new JTextField(currentFields.get(3));
    JTextField txtAddress = new JTextField(currentFields.get(4));
    JTextField txtPhone = new JTextField(currentFields.get(5));

    personalTab.add(new JLabel("Employee ID:"));
    personalTab.add(new JLabel(targetId));
    personalTab.add(new JLabel("Last Name * :"));
    personalTab.add(txtLn);
    personalTab.add(new JLabel("First Name * :"));
    personalTab.add(txtFn);
    personalTab.add(new JLabel("Birthday:"));
    personalTab.add(txtBday);
    personalTab.add(new JLabel("Address:"));
    personalTab.add(txtAddress);
    personalTab.add(new JLabel("Phone Number:"));
    personalTab.add(txtPhone);

    JPanel gPositionTab = new JPanel(new GridLayout(7, 2, 10, 15));
    gPositionTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    JTextField txtSss = new JTextField(currentFields.get(6));
    JTextField txtPh = new JTextField(currentFields.get(7));
    JTextField txtTin = new JTextField(currentFields.get(8));
    JTextField txtPagibig = new JTextField(currentFields.get(9));
    JTextField txtStatus = new JTextField(currentFields.get(10));
    JTextField txtDes = new JTextField(currentFields.get(11));
    JTextField txtSupervisor = new JTextField(currentFields.get(12));

    gPositionTab.add(new JLabel("SSS Number:"));
    gPositionTab.add(txtSss);
    gPositionTab.add(new JLabel("PhilHealth Number:"));
    gPositionTab.add(txtPh);
    gPositionTab.add(new JLabel("TIN:"));
    gPositionTab.add(txtTin);
    gPositionTab.add(new JLabel("Pag-IBIG Number:"));
    gPositionTab.add(txtPagibig);
    gPositionTab.add(new JLabel("Employment Status:"));
    gPositionTab.add(txtStatus);
    gPositionTab.add(new JLabel("Designation / Position:"));
    gPositionTab.add(txtDes);
    gPositionTab.add(new JLabel("Immediate Supervisor:"));
    gPositionTab.add(txtSupervisor);

    JPanel salaryTab = new JPanel(new GridLayout(6, 2, 10, 15));
    salaryTab.setBorder(new EmptyBorder(15, 15, 15, 15));

    JTextField txtBasic = new JTextField(currentFields.get(13));
    JTextField txtRice = new JTextField(currentFields.get(14));
    JTextField txtPhoneAll = new JTextField(currentFields.get(15));
    JTextField txtCloth = new JTextField(currentFields.get(16));
    JTextField txtGrossSemi = new JTextField(currentFields.get(17));
    JTextField txtHourly = new JTextField(currentFields.get(18));

    salaryTab.add(new JLabel("Basic Salary Base:"));
    salaryTab.add(txtBasic);
    salaryTab.add(new JLabel("Rice Subsidy Allowance:"));
    salaryTab.add(txtRice);
    salaryTab.add(new JLabel("Phone Allowance:"));
    salaryTab.add(txtPhoneAll);
    salaryTab.add(new JLabel("Clothing Allowance:"));
    salaryTab.add(txtCloth);
    salaryTab.add(new JLabel("Gross Semi-Monthly Rate:"));
    salaryTab.add(txtGrossSemi);
    salaryTab.add(new JLabel("Hourly Rate Base * :"));
    salaryTab.add(txtHourly);

    formTabs.addTab("1. Personal Details", personalTab);
    formTabs.addTab("2. Gov IDs & Status", gPositionTab);
    formTabs.addTab("3. Salary & Rates", salaryTab);

    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
    JButton updateBtn = new JButton("Commit Overwrite");
    styleMacButton(updateBtn, MAC_ACCENT, Color.WHITE);
    bottomPanel.add(updateBtn);

    updateBtn.addActionListener(
        e -> {
          if (!PayrollServiceChecker.validateEmployeeInput(
              modal,
              txtLn.getText(),
              txtFn.getText(),
              txtBday.getText(),
              txtPhone.getText(),
              txtSss.getText(),
              txtPh.getText(),
              txtTin.getText(),
              txtPagibig.getText(),
              txtBasic.getText(),
              txtRice.getText(),
              txtPhoneAll.getText(),
              txtCloth.getText(),
              txtGrossSemi.getText(),
              txtHourly.getText())) {
            return;
          }

          // Rebuilds the row in the same column order before replacing the stored version.
          List<String> updatedRecordFields = new ArrayList<>();
          updatedRecordFields.add(targetId);
          updatedRecordFields.add(txtLn.getText().trim());
          updatedRecordFields.add(txtFn.getText().trim());
          updatedRecordFields.add(txtBday.getText().trim());
          updatedRecordFields.add(txtAddress.getText().trim());
          updatedRecordFields.add(txtPhone.getText().trim());
          updatedRecordFields.add(txtSss.getText().trim());
          updatedRecordFields.add(txtPh.getText().trim());
          updatedRecordFields.add(txtTin.getText().trim());
          updatedRecordFields.add(txtPagibig.getText().trim());
          updatedRecordFields.add(txtStatus.getText().trim());
          updatedRecordFields.add(txtDes.getText().trim());
          updatedRecordFields.add(txtSupervisor.getText().trim());
          updatedRecordFields.add(txtBasic.getText().trim());
          updatedRecordFields.add(txtRice.getText().trim());
          updatedRecordFields.add(txtPhoneAll.getText().trim());
          updatedRecordFields.add(txtCloth.getText().trim());
          updatedRecordFields.add(txtGrossSemi.getText().trim());
          updatedRecordFields.add(txtHourly.getText().trim());

          Account.employeeData.put(targetId, updatedRecordFields);

          if (Account.saveEmployeeDataToCsv()) {
            syncTableGridRows("");
            modal.dispose();
            JOptionPane.showMessageDialog(
                anchor,
                "Success! Record updated cleanly inside Master Repository.",
                "Update Finalized",
                JOptionPane.INFORMATION_MESSAGE);
          } else {
            JOptionPane.showMessageDialog(
                modal,
                "CRITICAL FILE WRITE LOCK FAULT!",
                "Update Failure",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    modal.setLayout(new BorderLayout());
    modal.add(formTabs, BorderLayout.CENTER);
    modal.add(bottomPanel, BorderLayout.SOUTH);
    modal.setVisible(true);
  }
}
