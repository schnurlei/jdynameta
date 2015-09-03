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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.ButtonPanelUI;

/**
 * A demo to show the abilities of the {@link ButtonPanelUI}.
 * Enjoy the arrow keys buttons to move transer focus 
 * and selection of the Swing buttons.  
 */
public class ButtonPanelDemo extends JFrame {
    private ButtonGroup radioGroup = new ButtonGroup();

    private ButtonPanelUI buttonPanelUI = new ButtonPanelUI();
    private ButtonPanelUI cyclicButtonPanelUI = new ButtonPanelUI(true);

    public ButtonPanelDemo() {
        super("ButtonPanelUI demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        JPanel topPanel = new JPanel(new GridLayout(1, 0));

        JPanel checkBoxPanel = createCheckBoxPanel();
        topPanel.add(new JXLayer<JComponent>(checkBoxPanel, buttonPanelUI));

        JPanel radioGroupPanel = createRadioButtonPanel();
        topPanel.add(new JXLayer<JComponent>(radioGroupPanel, buttonPanelUI));

        add(topPanel);

        JPanel buttonPanel = createButtonPanel();
        add(new JXLayer<JComponent>(buttonPanel, cyclicButtonPanelUI), BorderLayout.SOUTH);

        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("Options");
        JMenuItem unselectItem = new JMenuItem("Unselect radioButton");
        unselectItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK));
        unselectItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // hack for 1.5
                // in 1.6 ButtonGroup.clearSelection added
                JRadioButton b = new JRadioButton();
                radioGroup.add(b);
                b.setSelected(true);
                radioGroup.remove(b);
            }
        });
        menu.add(unselectItem);

        bar.add(menu);
//        bar.add(new LafMenu());
        setJMenuBar(bar);

        setSize(300, 300);
        setLocationRelativeTo(null);
    }

    private JPanel createRadioButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JRadioButton one = new JRadioButton("One");
        panel.add(one);
        radioGroup.add(one);
        JRadioButton two = new JRadioButton("Two");
        panel.add(two);
        radioGroup.add(two);
        JRadioButton three = new JRadioButton("Three");
        panel.add(three);
        radioGroup.add(three);
        JRadioButton four = new JRadioButton("Four");
        panel.add(four);
        radioGroup.add(four);
        one.setSelected(true);
        panel.setBorder(BorderFactory.createTitledBorder(""));
        return panel;
    }

    private JPanel createCheckBoxPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        JCheckBox one = new JCheckBox("One");
        panel.add(one);
        JCheckBox two = new JCheckBox("Two");
        panel.add(two);
        JCheckBox three = new JCheckBox("Three");
        panel.add(three);
        JCheckBox four = new JCheckBox("Four");
        panel.add(four);
        panel.setBorder(BorderFactory.createTitledBorder(""));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel ret = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Does ButtonPanelUI support arrow keys ?");
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JPanel temp = new JPanel();
        temp.add(label);
        ret.add(temp);

        JPanel panel = new JPanel();
        panel.add(new JButton("Yes"));
        panel.add(new JButton("Sure"));
        panel.add(new JButton("Absolutely !"));
        panel.setBorder(BorderFactory.createTitledBorder(null,
                "ButtonPanelUI.setFocusCyclic(true)",
                TitledBorder.CENTER, TitledBorder.BOTTOM));
        ret.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        ret.add(panel, BorderLayout.SOUTH);
        return ret;
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ButtonPanelDemo().setVisible(true);
            }
        });
    }
}