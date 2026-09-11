package org.example.frontend.routes;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.example.App;
import org.example.frontend.components.Layout;

public class Home extends JPanel {

  public Home(App app) {
    setLayout(new BorderLayout(10, 10));

    JPanel nav = Layout.nav();
    nav.setBackground(new Color(30, 30, 30));
    nav.add(new JLabel("Nav"), BorderLayout.CENTER);

    JPanel aside = Layout.aside();
    aside.setBackground(new Color(45, 45, 45));
    aside.add(new JLabel("Menu"), BorderLayout.NORTH);

    JPanel header = Layout.header();
    header.setBackground(new Color(60, 60, 60));
    header.add(new JLabel("Header"), BorderLayout.CENTER);

    JButton dashboardBtn = new JButton("Open Dashboard");
    dashboardBtn.addActionListener(e -> app.goTo(RouteType.DASHBOARD));

    JPanel body = new JPanel(new BorderLayout(10, 10));
    body.setBackground(new Color(75, 75, 75));
    body.add(new JLabel("Content"), BorderLayout.CENTER);
    body.add(dashboardBtn, BorderLayout.SOUTH);

    JPanel content = Layout.contentWithHeader(header, body);

    JPanel mainContent = new JPanel(new BorderLayout(10, 10));
    mainContent.add(content, BorderLayout.CENTER);

    add(nav, BorderLayout.NORTH);
    add(aside, BorderLayout.WEST);
    add(mainContent, BorderLayout.CENTER);
  }
}
