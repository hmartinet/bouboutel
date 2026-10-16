/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 */

@SuppressWarnings("serial")
public final class FastImagePanel extends JPanel {
    private final Image image;
    private final int maxHeight;

    public FastImagePanel(Image image, int maxHeight) {
        this.image = image;
        this.maxHeight = maxHeight;
        setOpaque(false);
    }
    
    private Dimension calculateDimensions() {
        Container parent = getParent();
        if (parent == null) return null;
        int w = parent.getWidth();
        if (w <= 0) return null;
        
        int imgW = image.getWidth(null);
        int imgH = image.getHeight(null);
        double scale = (double) w / imgW;
        
        int h = (int) Math.round(imgH * scale);
        if (maxHeight > 0 && h > maxHeight) {
            h = maxHeight;
            scale = (double) h / imgH;
            w = (int) Math.round(imgW * scale);
        }
        return new Dimension(w, h);
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = calculateDimensions();
        return dim == null ? super.getPreferredSize() : dim;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Dimension dim = calculateDimensions();
        if (dim == null) return;

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, dim.width, dim.height, null);
        g2d.dispose();
    }
}