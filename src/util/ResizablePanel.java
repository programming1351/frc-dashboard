package util;

import org.w3c.dom.Text;
import util.log.GraphDisplay;
import util.log.TextDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ResizablePanel extends JPanel {
    public int[] locations = {
            SwingConstants.NORTH, SwingConstants.SOUTH, SwingConstants.WEST,
            SwingConstants.EAST, SwingConstants.NORTH_WEST,
            SwingConstants.NORTH_EAST, SwingConstants.SOUTH_WEST,
            SwingConstants.SOUTH_EAST
    };

    public int[] cursors = {
            Cursor.N_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR,
            Cursor.E_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR,
            Cursor.SW_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR
    };

    protected int x, y;
    private final ResizablePanel self;
    private int cornerDist = 8;
    private int edgeDist = 5;

    public ResizablePanel(int x, int y) {
        this.self = this;
        this.x = x;
        this.y = y;

        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Point lastPoint;
            private Rectangle stored, bounds;
            private int cursor, offsetX, offsetY, windowWidth, windowHeight;

            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getLocationOnScreen();
                stored = getBounds();
                cursor = getCursor(e);
                getParent().setComponentZOrder(self,0);
            }

            @Override
            public void mouseMoved(MouseEvent me) {
                setCursor(Cursor.getPredefinedCursor(getCursor(me)));
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = e.getLocationOnScreen();
                offsetX = point.x - lastPoint.x;
                offsetY = point.y - lastPoint.y;

                windowWidth = getParent().getWidth();
                windowHeight = getParent().getHeight();

                bounds = getBounds();

                switch (cursor) {
                    case Cursor.MOVE_CURSOR -> {
                        stored.x += offsetX;
                        stored.y += offsetY;
                        bounds.x = Math.max(0, Math.min(stored.x, windowWidth - bounds.width));
                        bounds.y = Math.max(0, Math.min(stored.y, windowHeight - bounds.height));
                    }
                    case Cursor.SE_RESIZE_CURSOR -> {
                        southDragged();
                        eastDragged();
                    }
                    case Cursor.SW_RESIZE_CURSOR -> {
                        southDragged();
                        westDragged();
                    }
                    case Cursor.NW_RESIZE_CURSOR -> {
                        northDragged();
                        westDragged();
                    }
                    case Cursor.NE_RESIZE_CURSOR -> {
                        northDragged();
                        eastDragged();
                    }
                    case Cursor.N_RESIZE_CURSOR -> northDragged();
                    case Cursor.S_RESIZE_CURSOR -> southDragged();
                    case Cursor.W_RESIZE_CURSOR -> westDragged();
                    case Cursor.E_RESIZE_CURSOR -> eastDragged();
                }

                lastPoint = point;

                if(self instanceof TextDisplay) {
                    ((TextDisplay) self).resizeMaxFont(bounds);
                }

                setBounds(bounds);
                setNewLoc(bounds.x, bounds.y);

                if(self instanceof TextDisplay) {
                    ((TextDisplay) self).resizeLabels();
                }
                if(self instanceof GraphDisplay) {
                    ((GraphDisplay) self).repaint();
                }

                setCursor(Cursor.getPredefinedCursor(cursor));
            }

            public void eastDragged() {
                stored.width = (stored.width + offsetX);
                bounds.width = Math.min(windowWidth - bounds.x, Math.max(stored.width, getMinimumSize().width));
            }

            public void southDragged() {
                stored.height = (stored.height + offsetY);
                bounds.height = Math.min(windowHeight - bounds.y, Math.max(stored.height, getMinimumSize().height));
            }

            public void westDragged() {
                stored.width -= offsetX;
                stored.x += offsetX;


                if (offsetX < 0) {
                    if(stored.x <= bounds.x) {
                        bounds.width = Math.min(stored.width, bounds.width + bounds.x);
                        bounds.x = Math.max(stored.x, 0);
                    }
                } else {
                    if(stored.width <= getMinimumSize().width) {
                        bounds.x += bounds.width - getMinimumSize().width;
                        bounds.width = getMinimumSize().width;
                    } else {
                        bounds.width = Math.min(stored.width, bounds.width + bounds.x);
                        bounds.x = Math.max(stored.x, 0);
                    }
                }
            }

            public void northDragged() {
                stored.height -= offsetY;
                stored.y += offsetY;

                if (offsetY < 0) {
                    if(stored.y <= bounds.y) {
                        bounds.height = Math.min(stored.height, bounds.height + bounds.y);
                        bounds.y = Math.max(stored.y, 0);
                    }
                } else {
                    if(stored.height <= getMinimumSize().height) {
                        bounds.y += bounds.height - getMinimumSize().height;
                        bounds.height = getMinimumSize().height;
                    } else {
                        bounds.height = Math.min(stored.height, bounds.height + bounds.y);
                        bounds.y = Math.max(stored.y, 0);
                    }
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void setNewLoc(int x, int y) {
        this.x = x;
        this.y = y;
    }

    private Rectangle getRectangle(int w, int h, int location) {
        return switch (location) {
            case SwingConstants.NORTH -> new Rectangle(cornerDist, 0, w - 2*cornerDist, edgeDist);
            case SwingConstants.SOUTH -> new Rectangle(cornerDist, h - edgeDist, w - 2*cornerDist, edgeDist);
            case SwingConstants.WEST -> new Rectangle(0, cornerDist, edgeDist, h - 2*cornerDist);
            case SwingConstants.EAST -> new Rectangle(w - edgeDist, cornerDist, edgeDist, h - 2*cornerDist);
            case SwingConstants.NORTH_WEST -> new Rectangle(0, 0, cornerDist, cornerDist);
            case SwingConstants.NORTH_EAST -> new Rectangle(w - cornerDist, 0, cornerDist, cornerDist);
            case SwingConstants.SOUTH_WEST -> new Rectangle(0, h - cornerDist, cornerDist, cornerDist);
            case SwingConstants.SOUTH_EAST -> new Rectangle(w - cornerDist, h - cornerDist, cornerDist, cornerDist);
            default -> new Rectangle();
        };
    }

    public int getCursor(MouseEvent me) {
        for (int i = 0; i < locations.length; i++) {
            Rectangle rect = getRectangle(me.getComponent().getWidth(), me.getComponent().getHeight(), locations[i]);
            if (rect != null && rect.contains(me.getPoint())) return cursors[i];
        }
        return Cursor.MOVE_CURSOR;
    }

    public Dimension getMinimumSize() {
        return new Dimension();
    }
}