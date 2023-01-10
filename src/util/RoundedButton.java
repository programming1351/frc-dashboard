package util;

import app.Cameras;
import app.Field;
import app.Logger;
import app.Settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JPanel {

    protected Dimension arcs = new Dimension(25, 25);
    protected Color hoverColor, clickColor;
    protected boolean clicked, hovering;
    protected int ord;

    public RoundedButton(int ord) {
        super();
        this.ord = ord;
        setOpaque(false);

        // TESTING
        if (ord == 0) openNewWindow();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                setClicked(true);
                repaint();
                revalidate();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                setClicked(false);
                openNewWindow();
                repaint();
                revalidate();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                setHovering(true);
                repaint();
                revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                setHovering(false);
                repaint();
                revalidate();
            }
        });
    }

    public void setHovering(boolean hovering) {
        this.hovering = hovering;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    public void setClickColor(Color clickColor) {
        this.clickColor = clickColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();

        Graphics2D graphics = (Graphics2D) g;

        //Sets antialiasing if HQ.
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //Draws the rounded opaque panel with borders.
        graphics.setColor(hovering ? hoverColor : getBackground());
        if (clicked) graphics.setColor(clickColor);
        graphics.fillRoundRect(0, 0, width,
                height, arcs.width, arcs.height);
        graphics.setColor(getForeground());
    }

    protected void openNewWindow() {
        switch (ord) {
            case 0 -> new Logger();
            case 1 -> new Field();
            case 2 -> new Cameras();
            case 3 -> new Settings();
        }
    }
}