package org.example.frontend.components;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public final class Layout {
  private Layout() {}

  public static JPanel nav() {
    return panel(new BorderLayout());
  }

  public static JPanel header() {
    return panel(new BorderLayout());
  }

  public static JPanel aside() {
    return panel(new BorderLayout());
  }

  public static JPanel content() {
    return new JPanel(new BorderLayout(0, 0));
  }

  public static JPanel contentWithHeader(JComponent header, JComponent body) {
    JPanel container = content();

    // new BorderLayout(hgap, vgap)
    // hgap → horizontal gap between components
    // vgap → vertical gap between components
    JPanel inner = new JPanel(new BorderLayout(0, 8));

    inner.add(header, BorderLayout.NORTH);
    body.setBorder(new EmptyBorder(8, 8, 8, 8));
    inner.add(body, BorderLayout.CENTER);

    container.add(inner, BorderLayout.CENTER);

    return container;
  }

  private static JPanel panel(java.awt.LayoutManager layout) {
    JPanel panel = new JPanel(layout);
    panel.setBorder(new EmptyBorder(8, 8, 8, 8));
    return panel;
  }
}
