package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Account {

  public static Map<String, List<String>> employeeData = new LinkedHashMap<>();
  private static final Map<String, List<AttendanceEntry>> attendanceData = new LinkedHashMap<>();
  private static final String EMPLOYEE_CSV_FILE_PATH = "EmployeeData.csv";
  private static final String ATTENDANCE_CSV_FILE_PATH = "AttendanceRecord.csv";
  private static final DateTimeFormatter ATTENDANCE_DATE_FORMAT =
      DateTimeFormatter.ofPattern("M/d/uuuu");
  private static final DateTimeFormatter ATTENDANCE_TIME_FORMAT =
      DateTimeFormatter.ofPattern("H:mm");

  private static final class AttendanceEntry {

    private final LocalDate attendanceDate;

    private final double billableHours;

    private AttendanceEntry(LocalDate attendanceDate, double billableHours) {
      this.attendanceDate = attendanceDate;
      this.billableHours = billableHours;
    }
  }

  private static List<String> parseCsvLineSafely(String csvLine) {
    List<String> textTokens = new ArrayList<>();
    StringBuilder currentToken = new StringBuilder();
    boolean insideQuotesContext = false;

    // Keeps commas inside quoted fields from splitting the record too early.
    for (int i = 0; i < csvLine.length(); i++) {
      char currentChar = csvLine.charAt(i);
      if (currentChar == '"') {
        insideQuotesContext = !insideQuotesContext;
      } else if (currentChar == ',' && !insideQuotesContext) {
        textTokens.add(currentToken.toString().trim());
        currentToken.setLength(0);
      } else {
        currentToken.append(currentChar);
      }
    }
    textTokens.add(currentToken.toString().trim());
    return textTokens;
  }

  private static BufferedReader openCsvReader(String csvFilePath) throws IOException {
    File file = new File(csvFilePath);
    if (file.exists()) {
      return new BufferedReader(new FileReader(file));
    }

    InputStream resourceStream = Account.class.getClassLoader().getResourceAsStream(csvFilePath);
    if (resourceStream != null) {
      return new BufferedReader(new InputStreamReader(resourceStream, StandardCharsets.UTF_8));
    }

    return null;
  }

  public static void loadEmployeeDataFromCsv() {
    employeeData.clear();
    try (BufferedReader reader = openCsvReader(EMPLOYEE_CSV_FILE_PATH)) {
      if (reader == null) return;

      // The first row is the CSV header, so it is read and ignored.
      String lineContent = reader.readLine();

      while ((lineContent = reader.readLine()) != null) {
        if (lineContent.trim().isEmpty()) continue;

        // Normalizes each row so the rest of the app can rely on fixed columns.
        List<String> standardizedColumns = parseCsvLineSafely(lineContent);
        if (standardizedColumns.isEmpty()) continue;

        String primaryKeyId = standardizedColumns.get(0).replace("\"", "").trim();

        // Pads short records so lookups and table rendering stay index-safe.
        while (standardizedColumns.size() < 19) {
          standardizedColumns.add("");
        }

        employeeData.put(primaryKeyId, standardizedColumns);
      }
    } catch (IOException e) {
      System.err.println("Database Loading Fault: " + e.getMessage());
    }
  }

  public static void loadAttendanceDataFromCsv() {
    attendanceData.clear();

    try (BufferedReader reader = openCsvReader(ATTENDANCE_CSV_FILE_PATH)) {
      if (reader == null) return;

      String lineContent = reader.readLine();
      while ((lineContent = reader.readLine()) != null) {
        if (lineContent.trim().isEmpty()) continue;

        List<String> columns = parseCsvLineSafely(lineContent);
        if (columns.size() < 6) continue;

        String employeeId = columns.get(0).replace("\"", "").trim();
        String dateText = columns.get(3).replace("\"", "").trim();
        String logInText = columns.get(4).replace("\"", "").trim();
        String logOutText = columns.get(5).replace("\"", "").trim();

        if (employeeId.isEmpty()
            || dateText.isEmpty()
            || logInText.isEmpty()
            || logOutText.isEmpty()) {
          continue;
        }

        try {
          // Parses only valid attendance rows so one bad entry does not stop the rest.
          LocalDate attendanceDate = LocalDate.parse(dateText, ATTENDANCE_DATE_FORMAT);
          LocalTime logInTime = LocalTime.parse(logInText, ATTENDANCE_TIME_FORMAT);
          LocalTime logOutTime = LocalTime.parse(logOutText, ATTENDANCE_TIME_FORMAT);

          double billableHours = computeWorkHours(logInTime, logOutTime);
          attendanceData
              .computeIfAbsent(employeeId, key -> new ArrayList<>())
              .add(new AttendanceEntry(attendanceDate, billableHours));
        } catch (DateTimeParseException ignored) {
          // Skips malformed records while keeping the rest of the file usable.
        }
      }
    } catch (IOException e) {
      System.err.println("Attendance Loading Fault: " + e.getMessage());
    }
  }

  public static double calculateAttendanceHours(String empId) {
    if (attendanceData.isEmpty()) {
      loadAttendanceDataFromCsv();
    }

    List<AttendanceEntry> records = attendanceData.get(empId.trim());
    if (records == null || records.isEmpty()) {
      return 0.0;
    }

    double totalHours = 0.0;
    for (AttendanceEntry record : records) {
      totalHours += record.billableHours;
    }
    return totalHours;
  }

  public static double getTotalHoursForPeriod(String empNum, String month, int start, int end) {
    if (attendanceData.isEmpty()) {
      loadAttendanceDataFromCsv();
    }

    List<AttendanceEntry> records = attendanceData.get(empNum.trim());
    if (records == null || records.isEmpty()) {
      return 0.0;
    }

    int targetMonth = parseMonthNumber(month);
    if (targetMonth < 1 || start > end) {
      return 0.0;
    }

    double totalHours = 0.0;
    for (AttendanceEntry record : records) {
      int recordMonth = record.attendanceDate.getMonthValue();
      int recordDay = record.attendanceDate.getDayOfMonth();
      if (recordMonth == targetMonth && recordDay >= start && recordDay <= end) {
        totalHours += record.billableHours;
      }
    }
    return totalHours;
  }

  public static LocalDate getLatestAttendanceDate(String empId) {
    if (attendanceData.isEmpty()) {
      loadAttendanceDataFromCsv();
    }

    List<AttendanceEntry> records = attendanceData.get(empId.trim());
    if (records == null || records.isEmpty()) {
      return null;
    }

    LocalDate latestDate = records.get(0).attendanceDate;
    for (AttendanceEntry record : records) {
      if (record.attendanceDate.isAfter(latestDate)) {
        latestDate = record.attendanceDate;
      }
    }
    return latestDate;
  }

  public static String getMonthName(String month) {
    String[] monthNames = {
      "",
      "",
      "",
      "",
      "",
      "",
      "June",
      "July",
      "August",
      "September",
      "October",
      "November",
      "December",
    };

    int monthNumber = parseMonthNumber(month);
    if (monthNumber < 6 || monthNumber >= monthNames.length) {
      return month;
    }
    return monthNames[monthNumber];
  }

  public static int getMonthNumber(String month) {
    return parseMonthNumber(month);
  }

  private static int parseMonthNumber(String month) {
    if (month == null) {
      return -1;
    }

    String trimmedMonth = month.trim();
    if (trimmedMonth.isEmpty()) {
      return -1;
    }

    switch (trimmedMonth.toLowerCase()) {
      case "june":
        return 6;
      case "july":
        return 7;
      case "august":
        return 8;
      case "september":
        return 9;
      case "october":
        return 10;
      case "november":
        return 11;
      case "december":
        return 12;
      default:
        break;
    }

    try {
      return Integer.parseInt(trimmedMonth);
    } catch (NumberFormatException ex) {
      return -1;
    }
  }

  private static double computeWorkHours(LocalTime login, LocalTime logout) {
    LocalTime startLimit = LocalTime.of(8, 0);
    LocalTime endLimit = LocalTime.of(17, 0);
    if (login.isBefore(LocalTime.of(8, 10))) login = startLimit;
    if (logout.isAfter(endLimit)) logout = endLimit;
    if (logout.isBefore(login)) return 0;
    long mins = Duration.between(login, logout).toMinutes();
    return (mins > 60) ? (mins - 60) / 60.0 : mins / 60.0;
  }

  public static boolean saveEmployeeDataToCsv() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(EMPLOYEE_CSV_FILE_PATH))) {
      // Rewrites the file in the same column order used everywhere else.
      writer.write(
          "Employee #,Last Name,First Name,Birthday,Address,Phone Number,"
              + "SSS #,PhilHealth #,TIN,Pag-IBIG #,Status,Designation,Supervisor,"
              + "Basic Salary,Rice Subsidy,Phone Allowance,Clothing Allowance,"
              + "Gross Semi-Monthly,Hourly Rate\n");

      for (List<String> rowFields : employeeData.values()) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 19; i++) {
          String fieldText = "";
          if (i < rowFields.size() && rowFields.get(i) != null) {
            fieldText = rowFields.get(i).replace("\"", "").trim();
          }
          // Quotes each field to keep commas and empty values stable on disk.
          sb.append("\"").append(fieldText).append("\"");
          if (i < 18) {
            sb.append(",");
          }
        }
        writer.write(sb.toString() + "\n");
      }
      return true;
    } catch (IOException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public static String[] findEmployeeRecord(String empId) {
    if (employeeData.isEmpty()) {
      loadEmployeeDataFromCsv();
    }
    List<String> record = employeeData.get(empId.trim());
    if (record == null) return null;

    String[] dataArray = new String[record.size()];
    for (int i = 0; i < record.size(); i++) {
      dataArray[i] = record.get(i).replace("\"", "").trim();
    }
    return dataArray;
  }

  public static String getNextSequentialEmployeeId() {
    if (employeeData.isEmpty()) {
      loadEmployeeDataFromCsv();
    }
    int maximumNumericalValue = 10000;
    for (String stringId : employeeData.keySet()) {
      try {
        int numericConvertValue = Integer.parseInt(stringId.trim());
        if (numericConvertValue > maximumNumericalValue) {
          maximumNumericalValue = numericConvertValue;
        }
      } catch (Exception ignored) {
      }
    }
    // Starts from 10001 so new IDs stay above the seeded sample records.
    return String.valueOf(maximumNumericalValue + 1);
  }
}
