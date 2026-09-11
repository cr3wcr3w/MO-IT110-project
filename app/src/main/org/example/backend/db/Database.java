package org.example.backend.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Database {
  private static final String EMPLOYEE_CSV_FILE_PATH = "EmployeeData.csv";
  private static final String ATTENDANCE_CSV_FILE_PATH = "AttendanceRecord.csv";
  private static final Map<String, List<String>> employeeData = new TreeMap<>();
  private static final Map<String, List<String[]>> attendanceData = new HashMap<>();

  /**
   * This method help employee csv format into [{key: [employee_number, fname, lname, birthday]}]
   * return type: array[hashmap{key: array[]}]
   */
  public static void loadEmployeeData() {
    // initialize csv reader for employee
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                openCsvResource(EMPLOYEE_CSV_FILE_PATH), StandardCharsets.UTF_8))) {
      // Skip header row
      reader.readLine();

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = splitCsvLine(line);

        List<String> details = new ArrayList<>();
        for (String part : parts) {
          details.add(part.trim().replaceAll("^\"|\"$", ""));
        }

        employeeData.put(parts[0].trim(), details);
      }
    } catch (IOException e) {
      System.out.println("Failed to read file: " + e.getMessage());
    }
  }

  /**
   * This method help attedance csv format into [{key = [date, time-in, time-out]}] return type:
   * array[hashmap{key: array[]}]
   */
  public static void loadAttendanceData() {
    // initialize csv reader for employee attedance
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                openCsvResource(ATTENDANCE_CSV_FILE_PATH), StandardCharsets.UTF_8))) {
      // Skip header row
      reader.readLine();

      String line;
      while ((line = reader.readLine()) != null) {
        String[] parts = splitCsvLine(line);

        String employeeNumber = parts[0].trim();
        String timeIn = parts[4].trim();
        String timeOut = parts[5].trim();
        String date = parts[3].trim();

        // only include attendance rows for known employee IDs
        if (employeeData.containsKey(employeeNumber)) {
          attendanceData
              // computeIfAbsent
              //  - get the employee’s list if already present
              //  - otherwise create a new empty list for that employee
              .computeIfAbsent(employeeNumber, k -> new ArrayList<>())
              // appends the array
              .add(new String[] {date, timeIn, timeOut});
        }
      }
    } catch (IOException e) {
      System.out.println("Failed to read file: " + e.getMessage());
    }
  }

  public static Map<String, List<String>> getEmployeeData() {
    return employeeData;
  }

  public static Map<String, List<String[]>> getAttendanceData() {
    return attendanceData;
  }

  private static InputStream openCsvResource(String fileName) throws IOException {
    InputStream resourceStream = Database.class.getResourceAsStream("/" + fileName);
    if (resourceStream != null) {
      return resourceStream;
    }

    throw new IOException("Resource not found: " + fileName);
  }

  /**
   * This method help remove character
   *
   * @param line a single line from the CSV file
   * @return an array of parsed CSV values
   */
  private static String[] splitCsvLine(String line) {
    return line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
  }
}
