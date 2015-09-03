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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.SpotLightUI;

public class SpotLightDemo extends JFrame {
    private SpotLightUI spotLightUI = new SpotLightUI(15);

    private Ellipse2D shape;
    
    public SpotLightDemo() {
        super("SpotLight effect demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JXLayer<JComponent> layer = 
                new JXLayer<JComponent>(createDemoPanel(4, 5),
                        spotLightUI);
        JScrollPane pane = new JScrollPane(layer);
        add(pane);

        shape = new Ellipse2D.Double(20, 20, 120, 120);
        spotLightUI.addShape(shape);
        
        add(createToolPanel(), BorderLayout.SOUTH);
        
        setSize(400, 300);
        setLocationRelativeTo(null);
//        setJMenuBar(LafMenu.createMenuBar());
    }

    private JComponent createDemoPanel(int w, int h) {
        JPanel panel = new JPanel(new GridLayout(w, h));
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                JButton button = new JButton("Hello");
                button.setFocusable(false);
                panel.add(button);
            }
        }
        panel.setPreferredSize(new Dimension(500, 400));
        return panel;
    }
    
    private JComponent createToolPanel() {
        JPanel panel = new JPanel();
        final JSpinner xspinner = new JSpinner(
                new SpinnerNumberModel((int) shape.getX(), 0, 400, 5));
        final JSpinner yspinner = new JSpinner(
                new SpinnerNumberModel((int) shape.getY(), 0, 400, 5));
        
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                spotLightUI.reset();
                shape.setFrame((Integer) xspinner.getValue(), 
                        ((Integer)yspinner.getValue()),
                        shape.getWidth(), shape.getHeight());
                spotLightUI.addShape(shape);
            }
        };
        xspinner.addChangeListener(listener);
        yspinner.addChangeListener(listener);
        
        panel.add(new JLabel("X:"));
        panel.add(xspinner);
        panel.add(new JLabel(" Y:"));
        panel.add(yspinner);
        
        final JSpinner clipSpinner = new JSpinner(new SpinnerNumberModel(15, 0, 100, 1));
        clipSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                spotLightUI.setSoftClipWidth((Integer) clipSpinner.getValue());
            }
        });

        panel.add(new JLabel(" Border size:"));
        panel.add(clipSpinner);
        return panel;
    }
    
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SpotLightDemo().setVisible(true);
            }
        });
    }
}
