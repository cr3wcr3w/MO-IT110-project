package org.example.shared.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

public class Logger {

  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  private static String timestamp() {
    return LocalDateTime.now().format(FORMATTER);
  }

  // INFO
  public static void info(String message, boolean showDialog) {
    System.out.println("[" + timestamp() + "] [INFO] " + message);

    if (showDialog) {
      JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // SUCCESS
  public static void success(String message, boolean showDialog) {
    System.out.println("[" + timestamp() + "] [SUCCESS] " + message);

    if (showDialog) {
      JOptionPane.showMessageDialog(null, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
  }

  // ERROR
  public static void error(String message, boolean showDialog) {
    System.err.println("[" + timestamp() + "] [ERROR] " + message);

    if (showDialog) {
      JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void error(String message, Exception e, boolean showDialog) {
    System.err.println("[" + timestamp() + "] [ERROR] " + message);

    if (showDialog) {
      JOptionPane.showMessageDialog(
          null, message + "\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
  }
}
