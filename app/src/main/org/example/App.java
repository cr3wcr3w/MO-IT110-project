package org.example;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import org.example.utils.AppChecker;

public class App extends JFrame {

  private CardLayout cardLayout;
  private JPanel mainContainer;

  private static final Color BG_LIGHT = new Color(245, 245, 247);
  private static final Color MAC_ACCENT = new Color(0, 122, 255);
  private static final Color MAC_TEXT_DARK = new Color(29, 29, 31);
  private static final Color MAC_BORDER = new Color(210, 210, 215);

  private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
  private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
  private static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

  public App() {
    // Load the employee cache once before any screen tries to read it.
    Account.loadEmployeeDataFromCsv();

    setTitle("MotorPH Payroll  System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(950, 650);
    setLocationRelativeTo(null);

    cardLayout = new CardLayout();
    mainContainer = new JPanel(cardLayout);

    JPanel loginScreen = createLoginScreen();
    mainContainer.add(loginScreen, "LOGIN_SCREEN");

    add(mainContainer);
    cardLayout.show(mainContainer, "LOGIN_SCREEN");
  }

  private JPanel createLoginScreen() {
    JPanel loginPanel = new JPanel(new GridBagLayout());
    loginPanel.setBackground(BG_LIGHT);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new java.awt.Insets(10, 10, 10, 10);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JPanel cardWrapper = new JPanel();
    cardWrapper.setLayout(new BoxLayout(cardWrapper, BoxLayout.Y_AXIS));
    cardWrapper.setBackground(Color.WHITE);
    cardWrapper.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(30, 40, 30, 40)));

    JLabel titleLabel = new JLabel("MotorPH Payroll System - Login");
    titleLabel.setFont(FONT_TITLE);
    titleLabel.setForeground(MAC_TEXT_DARK);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel subtitleLabel = new JLabel("Enter your corporate credential keys.");
    subtitleLabel.setFont(FONT_BODY);
    subtitleLabel.setForeground(new Color(134, 134, 139));
    subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel userLabel = new JLabel("Username / Employee ID:");
    userLabel.setFont(FONT_BOLD);
    userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JTextField usernameField = new JTextField(20);
    styleInputField(usernameField);

    JLabel passLabel = new JLabel("Password:");
    passLabel.setFont(FONT_BOLD);
    passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JPasswordField passwordField = new JPasswordField(20);
    styleInputField(passwordField);

    JButton loginButton = new JButton("Login");
    loginButton.setFont(FONT_BOLD);
    loginButton.setBackground(MAC_ACCENT);
    loginButton.setForeground(Color.WHITE);
    loginButton.setOpaque(true);
    loginButton.setBorderPainted(false);
    loginButton.setFocusPainted(false);
    loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    cardWrapper.add(titleLabel);
    cardWrapper.add(Box.createVerticalStrut(5));
    cardWrapper.add(subtitleLabel);
    cardWrapper.add(Box.createVerticalStrut(25));
    cardWrapper.add(userLabel);
    cardWrapper.add(Box.createVerticalStrut(5));
    cardWrapper.add(usernameField);
    cardWrapper.add(Box.createVerticalStrut(15));
    cardWrapper.add(passLabel);
    cardWrapper.add(Box.createVerticalStrut(5));
    cardWrapper.add(passwordField);
    cardWrapper.add(Box.createVerticalStrut(25));
    cardWrapper.add(loginButton);

    loginButton.addActionListener(
        e -> {
          String inputUser = usernameField.getText().trim();
          String inputPass = new String(passwordField.getPassword()).trim();

          if (!AppChecker.authenticateLogin(loginPanel, inputUser, inputPass)) {
            return;
          }

          Account.loadEmployeeDataFromCsv();
          // Staff can enter the shared admin key; employees can log in using the shared role name
          // or an ID.
          boolean isValidStaff =
              "payroll_staff".equalsIgnoreCase(inputUser) && "12345".equals(inputPass);
          boolean isValidEmployeeRole =
              "employee".equalsIgnoreCase(inputUser) && "12345".equals(inputPass);
          boolean isValidEmployeeId =
              Account.employeeData.containsKey(inputUser) && "12345".equals(inputPass);

          if (isValidStaff || isValidEmployeeRole || isValidEmployeeId) {
            String assignedRole = isValidStaff ? "payroll_staff" : "employee";

            // Switches the UI to the role-specific dashboard after authentication.
            JPanel securedDashboard = createDashboardLayout(assignedRole, inputUser);
            mainContainer.add(securedDashboard, "MAIN_DASHBOARD");
            cardLayout.show(mainContainer, "MAIN_DASHBOARD");

            usernameField.setText("");
            passwordField.setText("");
          } else {
            JOptionPane.showMessageDialog(
                loginPanel,
                "Authentication Failed: Access keys are invalid or Employee ID does not exist.",
                "Security Notice",
                JOptionPane.ERROR_MESSAGE);
          }
        });

    loginPanel.add(cardWrapper, gbc);
    return loginPanel;
  }

  private JPanel createDashboardLayout(String userRole, String usernameSessionId) {
    JPanel dashboardPanel = new JPanel(new BorderLayout());
    dashboardPanel.setBackground(BG_LIGHT);

    JPanel headerBar = new JPanel(new BorderLayout());
    headerBar.setBackground(Color.WHITE);
    headerBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, MAC_BORDER));
    headerBar.setPreferredSize(new Dimension(900, 60));
    headerBar.setBorder(new EmptyBorder(0, 20, 0, 20));

    String labelRoleText =
        "payroll_staff".equalsIgnoreCase(userRole) ? "ADMIN ACCOUNT" : "EMPLOYEE PORTAL";
    String welcomeString =
        String.format(
            "MotorPH Console  |  Logged User ID: %s (%s)", usernameSessionId, labelRoleText);

    JLabel appTitle = new JLabel(welcomeString);
    appTitle.setFont(FONT_BOLD);
    appTitle.setForeground(MAC_TEXT_DARK);
    headerBar.add(appTitle, BorderLayout.WEST);

    JButton logoutBtn = new JButton("Log Out");
    logoutBtn.setFont(FONT_BOLD);
    logoutBtn.setBackground(new Color(220, 53, 69));
    logoutBtn.setForeground(Color.WHITE);
    logoutBtn.setOpaque(true);
    logoutBtn.setBorderPainted(false);
    logoutBtn.setFocusPainted(false);

    logoutBtn.addActionListener(
        e -> {
          int verifyLogout =
              JOptionPane.showConfirmDialog(
                  dashboardPanel,
                  "Are you sure you want to log out?",
                  "Confirm Logout",
                  JOptionPane.YES_NO_OPTION);
          if (verifyLogout == JOptionPane.YES_OPTION) {
            cardLayout.show(mainContainer, "LOGIN_SCREEN");
          }
        });
    headerBar.add(logoutBtn, BorderLayout.EAST);
    dashboardPanel.add(headerBar, BorderLayout.NORTH);

    if ("employee".equalsIgnoreCase(userRole)) {
      // Employee sessions are restricted to the self-service payroll view.
      JPanel dedicatedEmployeePayrollWorkspace =
          PayrollService.createPayrollPanel(userRole, usernameSessionId);
      dedicatedEmployeePayrollWorkspace.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
      dashboardPanel.add(dedicatedEmployeePayrollWorkspace, BorderLayout.CENTER);
    } else {
      // Admins get both the payroll table and the employee maintenance tab.
      JTabbedPane multiViewTabContainer = new JTabbedPane();
      multiViewTabContainer.setFont(FONT_BOLD);

      JPanel payrollViewTab = PayrollService.createPayrollPanel(userRole, usernameSessionId);
      multiViewTabContainer.addTab("Payroll Administration", payrollViewTab);

      multiViewTabContainer.setSelectedIndex(0);
      dashboardPanel.add(multiViewTabContainer, BorderLayout.CENTER);
    }

    dashboardPanel.revalidate();
    dashboardPanel.repaint();
    return dashboardPanel;
  }

  private void styleInputField(JTextField field) {
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
    field.setFont(FONT_BODY);
    field.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAC_BORDER, 1), new EmptyBorder(6, 10, 6, 10)));
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new App().setVisible(true));
  }
}
