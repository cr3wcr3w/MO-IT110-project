package org.example;

// import java.util.List;
// import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.example.backend.config.Setting;
import org.example.backend.db.Database;

public class App extends JFrame {

  public App() {
    Setting app = new Setting();

    // setup the mock database
    Database.loadEmployeeData();
    Database.loadAttendanceData();

    // Map<String, List<String>> employees = Database.getEmployeeData();
    // Map<String, List<String[]>> attendance = Database.getAttendanceData();

    // System.out.println("Employee Data: " + employees);
    // for (String employeeId : attendance.keySet()) {
    //   System.out.println("Employee: " + employeeId);

    //   for (String[] record : attendance.get(employeeId)) {
    //     System.out.println(
    //         "  Date: " + record[0] + " | Time In: " + record[1] + " | Time Out: " + record[2]);
    //   }
    // }

    JLabel label = new JLabel("Hello World!");
    app.add(label);

    app.setVisible(true);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new App());
  }
}
