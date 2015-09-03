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
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.AbstractLayerUI;

public class TextValidationDemo extends JFrame {
    private TranslucentValidationUI translucentUI = new TranslucentValidationUI();
    private IconValidationUI iconValidationUI = new IconValidationUI();

    public TextValidationDemo() {
        super("Validation layers");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.add(createLayerBox());
        panel.add(createTitleBox());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        add(panel);

        JMenuBar bar = new JMenuBar();
//        bar.add(new LafMenu());
        setJMenuBar(bar);
        setSize(300, 150);
        setLocationRelativeTo(null);
    }

    private JComponent createLayerBox() {
        Box layerBox = Box.createVerticalBox();

        JXLayer<JTextComponent>l1 
                = new JXLayer<JTextComponent>(new JTextField(10), translucentUI);

        layerBox.add(Box.createGlue());
        layerBox.add(l1);

        JXLayer<JTextComponent> l2 
                = new JXLayer<JTextComponent>(new JTextField(10), iconValidationUI);

        layerBox.add(Box.createGlue());
        layerBox.add(l2);
        return layerBox;
    }

    private Component createTitleBox() {
        Box titleBox = Box.createVerticalBox();
        titleBox.add(new JLabel("type \"JXLayer\""));
        titleBox.add(new JLabel("and see the result"));
        titleBox.add(Box.createGlue());
        return titleBox;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TextValidationDemo().setVisible(true);
            }
        });
    }

    // Collection of demo validation layerUI's

    /**
     * This UI works only for JTextField, because this component 
     * is completely repainted when you type. 
     * For multiline components use the DocumentListener approach 
     * from the IconValidationUI.   
     */
    public static class TranslucentValidationUI extends AbstractLayerUI<JTextComponent> {
        protected void paintLayer(Graphics2D g2, JXLayer<? extends JTextComponent> l) {
            // paints the layer as is
            super.paintLayer(g2, l);

            // to be in sync with the view if the layer has a border
            Insets layerInsets = l.getInsets();
            g2.translate(layerInsets.left, layerInsets.top);

            JTextComponent view = l.getView();
            // To prevent painting on view's border
            Insets insets = view.getInsets();
            g2.clip(new Rectangle(insets.left, insets.top,
                    view.getWidth() - insets.left - insets.right,
                    view.getHeight() - insets.top - insets.bottom));

            g2.setColor(view.getText().toLowerCase().equals("jxlayer") ?
                    Color.GREEN : Color.RED);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .2f));
            g2.fillRect(0, 0, l.getWidth(), l.getHeight());
        }
    }

    public static class IconValidationUI extends AbstractLayerUI<JTextComponent> {

        // The red icon to be shown at the layer's corner
        private final static BufferedImage INVALID_ICON;

        static {
            int width = 7;
            int height = 8;
            INVALID_ICON = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) INVALID_ICON.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2.setColor(Color.RED);
            g2.fillRect(0, 0, width, height);
            g2.setColor(Color.WHITE);
            g2.drawLine(0, 0, width, height);
            g2.drawLine(0, height, width, 0);
            g2.dispose();
        }

        // DocumentListener to repaint the layer when the textComponent is updated
        private final DocumentListener documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                setDirty(true);
            }

            public void removeUpdate(DocumentEvent e) {
                setDirty(true);
            }

            public void changedUpdate(DocumentEvent e) {
                setDirty(true);
            }
        };

        @SuppressWarnings("unchecked")
        public void installUI(JComponent c) {
            super.installUI(c);
            JXLayer<JTextComponent> l = (JXLayer<JTextComponent>) c;
            l.getView().getDocument().addDocumentListener(documentListener);
            // set necessary insets for the layer
            l.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 3));
        }

        @SuppressWarnings("unchecked")
        public void uninstallUI(JComponent c) {
            super.uninstallUI(c);
            JXLayer<JTextComponent> l = (JXLayer<JTextComponent>) c;
            l.getView().getDocument().removeDocumentListener(documentListener);
            l.setBorder(null);
        }

        protected void paintLayer(Graphics2D g2, JXLayer<? extends JTextComponent> l) {
            super.paintLayer(g2, l);

            // There is no need to take insets into account for this painter
            if (!l.getView().getText().toLowerCase().equals("jxlayer")) {
                g2.drawImage(INVALID_ICON, l.getWidth() - INVALID_ICON.getWidth() - 1, 0, null);
            }
        }
    }
}

