/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jdynameta.view.swingx.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public class MouseDrawingDemo {

    private static void createGui() {
        final JFrame frame = new JFrame("MouseDrawingDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        for (int i = 0; i < 3; i++) {
            panel.add(new JButton("JButton"));
            panel.add(new JCheckBox("JCheckBox"));
            panel.add(new JTextField("JTextField"));
        }

        JXLayer<JComponent> l = new JXLayer<JComponent>(panel);

        final MouseDrawingUI<JComponent> mouseDrawingUI = new MouseDrawingUI<JComponent>();
        l.setUI(mouseDrawingUI);
        frame.add(l);

        JMenuBar bar = new JMenuBar();
        JMenu options = new JMenu("Options");
        JMenuItem clearMenu = new JMenuItem("Clear");
        clearMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        clearMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mouseDrawingUI.clear();
            }
        });
        options.add(clearMenu);
        bar.add(options);
        frame.setJMenuBar(bar);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGui();
            }
        });
    }

    // UI which allows to paint on top of the components
    public static class MouseDrawingUI<V extends JComponent> extends AbstractLayerUI<V> {
        private List<List<Point>> pathList = new ArrayList<List<Point>>();
        private List<Point> currentPath;

        // override paintLayer(), not paint()
        protected void paintLayer(Graphics2D g2, JXLayer<? extends V> l) {
            // this paints layer as is
            super.paintLayer(g2, l);
            // custom painting is here
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2f));
            for (java.util.List<Point> points : pathList) {
                Point oldPoint = null;
                for (Point point : points) {
                    if (oldPoint != null) {
                        g2.drawLine(oldPoint.x, oldPoint.y, point.x, point.y);
                    }
                    oldPoint = point;
                }
            }
        }

        // catch drag events
        protected void processMouseMotionEvent(MouseEvent e, JXLayer<? extends V> l) {
            super.processMouseMotionEvent(e, l);
            if (e.getID() == MouseEvent.MOUSE_DRAGGED) {
                Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), l);
                if (currentPath == null) {
                    currentPath = new ArrayList<Point>();
                    pathList.add(currentPath);
                }
                currentPath.add(point);
                // mark the ui as dirty and needed to be repainted
                setDirty(true);
            }
        }

        // catch MouseEvent.MOUSE_RELEASED
        protected void processMouseEvent(MouseEvent e, JXLayer<? extends V> l) {
            super.processMouseEvent(e, l);
            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                currentPath = null;
                // mark the ui as dirty and needed to be repainted
                setDirty(true);
            }
        }

        // clear overlay painting
        public void clear() {
            pathList.clear();
            // mark the ui as dirty and needed to be repainted
            setDirty(true);
        }
    }
}
