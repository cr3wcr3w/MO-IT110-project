package org.example.frontend.routes;

import java.awt.CardLayout;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.JPanel;

public class Router {
  private final CardLayout cardLayout;
  private final JPanel container;
  private final Map<RouteType, JPanel> routes = new EnumMap<>(RouteType.class);

  public Router(JPanel container, CardLayout cardLayout) {
    this.container = container;
    this.cardLayout = cardLayout;
  }

  public void register(RouteType route, JPanel panel) {
    routes.put(route, panel);
    container.add(panel, route.key());
  }

  public void navigate(RouteType route) {
    if (!routes.containsKey(route)) {
      throw new IllegalArgumentException("Route not found: " + route.key());
    }

    cardLayout.show(container, route.key());
  }
}
