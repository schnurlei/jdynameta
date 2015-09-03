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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

/**
 * Here is a simple {@link JXLayer}'s demo which is a good point to start with.
 * It shows how easy it is to decorate any Swing components with JXLayer.
 * <p/>
 * We are going to decorate a button and paint a translucent foreground over it,
 * if the button in its normal state we'll use the green foreground color,
 * for the rollovered state (the mouse is over the button) we'll use orange
 * <p/>
 * To make it more intersting we'll provide a setForeground() method for our LayerUI
 * and make it possible to change the default foreground color from the "Options" menu
 * 
 * @see JXLayer
 * @see AbstractLayerUI
 */
public class SimpleDemo extends JFrame {

    /**
     * It is our custom LayerUI, which will help us to implement what we need
     */
    private SimpleButtonUI simpleButtonUI = new SimpleButtonUI();

    public SimpleDemo() {
        super("JXLayer demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        setJMenuBar(createMenuBar());

        // We create a button, it will the "view" component for our JXLayer
        JButton button = new JButton("JButton");
        // Wrap it into the layer and set simpleButtonUI as the layer's UI;
        JXLayer<JButton> layer = new JXLayer<JButton>(button, simpleButtonUI);
        // add the layer as an ordinary component
        add(layer);

        setSize(200, 200);
        setLocationRelativeTo(null);
    }

    /**
     * Here is our custom LayerUI implementation;
     * the generic type matches the type of the view component
     */
    static class SimpleButtonUI extends AbstractLayerUI<JButton> {
        // The mutable foreground color
        private Color foreground = Color.GREEN;

        public Color getForeground() {
            return foreground;
        }

        public void setForeground(Color foreground) {
            // save the old color
            Color oldRolloverColor = getForeground();
            this.foreground = foreground;
            // if the new color is set we mark the UI as dirty
            if (!oldRolloverColor.equals(foreground)) {
                // it will eventually repaint the layer
                setDirty(true);
            }
        }

        // Note that we override AbstractLayerUI.paintLayer(), not LayerUI.paint()
        protected void paintLayer(Graphics2D g2, JXLayer<? extends JButton> layer) {

            // super implementation just paints the layer as is
            super.paintLayer(g2, layer);

            // note that we can access the button via layer.getView() method
            final ButtonModel model = layer.getView().getModel();

            // choose the color depending on the state of the button
            if (model.isRollover()) {
                g2.setColor(Color.ORANGE);
            } else {
                g2.setColor(foreground);
            }

            // paint the selected color translucently
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .3f));
            g2.fillRect(0, 0, layer.getWidth(), layer.getHeight());
        }
    }

    // That's it! Please try the other demos in this package.
    // 
    // Should you have any questions or comments about JXLayer
    // feel free to send them to the following mailing list:
    //
    // https://jxlayer.dev.java.net/servlets/SummarizeList?listName=users
    //
    // Have a nice day

    private JMenuBar createMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu optionsMenu = new JMenu("Options");
        final JCheckBoxMenuItem colorItem = new JCheckBoxMenuItem("Red foreground");
        optionsMenu.add(colorItem);
        colorItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                simpleButtonUI.setForeground(colorItem.isSelected()?Color.RED:Color.GREEN);
            }
        });
        bar.add(optionsMenu);
//        bar.add(new LafMenu());
        return bar;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SimpleDemo().setVisible(true);
            }
        });
    }
}
