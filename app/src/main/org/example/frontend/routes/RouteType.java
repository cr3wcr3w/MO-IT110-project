package org.example.frontend.routes;

public enum RouteType {
  HOME("home"),
  DASHBOARD("dashboard");

  private final String key;

  RouteType(String key) {
    this.key = key;
  }

  public String key() {
    return key;
  }
}
