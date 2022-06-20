/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.components;

import com.formdev.flatlaf.ui.FlatStylingSupport.Styleable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.KeyboardFocusManager;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.MatteBorder;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */
public class VerticalFlatBorder extends MatteBorder {
    @Styleable
    protected Color focusColor = UIManager.getColor("Component.focusColor");
    @Styleable
    protected Color borderColor = UIManager.getColor("Component.borderColor");

    public VerticalFlatBorder() {
        super(1, 0, 1, 0, UIManager.getColor("Component.borderColor"));
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width,
            int height) {
        Container container = SwingUtilities.getAncestorOfClass(
                JScrollPane.class, KeyboardFocusManager.
                        getCurrentKeyboardFocusManager().getFocusOwner());
        if (container != null && container.equals(c)) {
            color = focusColor;
        } else {
            color = borderColor;
        }
        super.paintBorder(c, g, x, y, width, height);
    }
}
