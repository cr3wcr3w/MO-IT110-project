package org.example.utils;

import java.awt.Component;
import org.example.Account;

public class AppChecker {

  public static boolean isValidLoginUsername(String value) {
    if (value == null) {
      Logger.error("Invalid username or password", true);
      return false;
    }

    String trimmed = value.trim();
    boolean isValid = !trimmed.isEmpty() && trimmed.length() <= 50;
    if (isValid) {
      Logger.success("Username is valid", false);
    } else {
      Logger.error("Invalid username or password", true);
    }
    return isValid;
  }

  public static boolean isValidLoginPassword(String value) {
    if (value == null) {
      Logger.error("Invalid username or password", true);
      return false;
    }

    String trimmed = value.trim();
    boolean isValid = !trimmed.isEmpty() && trimmed.length() <= 50;
    if (isValid) {
      Logger.success("Password is valid", false);
    } else {
      Logger.error("Invalid username or password", true);
    }
    return isValid;
  }

  public static boolean isValidLoginCredentials(String username, String password) {
    return isValidLoginCredentials(null, username, password);
  }

  public static boolean isValidLoginCredentials(
      Component parent, String username, String password) {
    boolean isValidUsername = isValidLoginUsername(username);
    boolean isValidPassword = isValidLoginPassword(password);
    boolean isValid = isValidUsername && isValidPassword;

    if (!isValid) {
      Logger.error("Invalid username or password", true);
      return false;
    }

    return true;
  }

  public static boolean authenticateLogin(Component parent, String username, String password) {
    if (!isValidLoginCredentials(parent, username, password)) {
      return false;
    }

    boolean isValidStaff = "payroll_staff".equalsIgnoreCase(username) && "12345".equals(password);
    boolean isValidEmployeeRole = "employee".equalsIgnoreCase(username) && "12345".equals(password);
    boolean isValidEmployeeId =
        password != null
            && "12345".equals(password)
            && username != null
            && Account.employeeData.containsKey(username.trim());

    if (isValidStaff || isValidEmployeeRole || isValidEmployeeId) {
      Logger.success("Login successful", true);
      return true;
    }

    Logger.error("Authentication failed", true);
    return false;
  }
}
