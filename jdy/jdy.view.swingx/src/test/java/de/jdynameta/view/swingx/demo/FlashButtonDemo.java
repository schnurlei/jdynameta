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

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

/**
 * A demo which is shows the "flash animation effect".
 * Move the mouse cursor over the button to see it
 */
public class FlashButtonDemo {
    private static void createGui() {
        JFrame frame = new JFrame("FlashButtonDemo");
//        frame.setJMenuBar(LafMenu.createMenuBar());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JLabel("Move mouse over the button", JLabel.CENTER),
                BorderLayout.SOUTH);
        
        JPanel panel = new JPanel(new GridBagLayout());

        JXLayer<AbstractButton> l = new JXLayer<AbstractButton>(new JButton("Hello"));
        panel.add(l);

        l.setUI(new FlashButtonUI());

        frame.add(panel);
        frame.setSize(250, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGui();
            }
        });
    }

    public static class FlashButtonUI extends AbstractLayerUI<AbstractButton> {
        private Timer timer;
        private double scale;
        private boolean rollover;

        public FlashButtonUI() {
            scale = 1;
            timer = new Timer(30, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (scale > 2) {
                        timer.stop();
                        scale = 1;
                    } else {
                        scale += .2;
                    }
                    setDirty(true);
                }
            });
        }

        @Override
        protected void paintLayer(Graphics2D g2, JXLayer<? extends AbstractButton> l) {
            // paint in once
            l.paint(g2);
            
            // start the timer if the button is in rollovered state
            boolean currRollover = l.getView().getModel().isRollover();
            if (currRollover && !rollover) {
                if (!timer.isRunning()) {
                    timer.start();
                }
            }
            rollover = currRollover;
            
            // apply transform and paint the layer again if timer is running
            if (timer.isRunning()) {
                g2.transform(getCenteredScaleTransform(l));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
                l.paint(g2);
            }            
        }

        private AffineTransform getCenteredScaleTransform(JComponent c) {
            AffineTransform t = AffineTransform.getScaleInstance(scale, scale);
            int width = c.getWidth();
            double xt = ((width * scale) - width) / (2 * scale);
            int height = c.getHeight();
            double yt = ((height * scale) - height) / (2 * scale);
            t.translate(-xt, -yt);
            return t;
        }
    }
}