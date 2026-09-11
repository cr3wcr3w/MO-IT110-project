package org.example.frontend.routes;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.example.App;

public class Dashboard extends JPanel {

  public Dashboard(App app) {

    setLayout(new BorderLayout(20, 20));

    JLabel title = new JLabel("Dashboard Screen", JLabel.CENTER);
    title.setFont(new Font("Arial", Font.BOLD, 24));

    JButton homeBtn = new JButton("Open Home");
    homeBtn.addActionListener(e -> app.goTo(RouteType.HOME));

    JPanel center = new JPanel();
    center.add(homeBtn);

    add(title, BorderLayout.NORTH);
    add(center, BorderLayout.CENTER);
  }
}
