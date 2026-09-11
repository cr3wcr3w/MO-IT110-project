package org.example;

import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.example.backend.config.Setting;
import org.example.backend.db.Database;
import org.example.frontend.routes.Dashboard;
import org.example.frontend.routes.Home;
import org.example.frontend.routes.RouteType;
import org.example.frontend.routes.Router;

public class App {
  private final CardLayout cardLayout = new CardLayout();
  private final JPanel screens = new JPanel(cardLayout);
  private final Router router;
  private final Setting window;

  public App() {
    this.window = new Setting();

    // setup the mock database
    Database.loadEmployeeData();
    Database.loadAttendanceData();

    this.router = new Router(screens, cardLayout);

    // register routes
    router.register(RouteType.HOME, new Home(this));
    router.register(RouteType.DASHBOARD, new Dashboard(this));

    window.setContentPane(screens);

    // default route
    router.navigate(RouteType.HOME);

    window.setVisible(true);
  }

  public void goTo(RouteType route) {
    router.navigate(route);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new App());
  }
}
