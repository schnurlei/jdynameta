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

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.MouseScrollableUI;

/**
 * A demo to show the abilities of the {@link MouseScrollableUI}.
 * Click the mouse wheel button inside any of JScrollPanes to check it out.
 */
public class MouseScrollableDemo extends JFrame {

    private MouseScrollableUI mouseScrollableUI = new MouseScrollableUI();

    public MouseScrollableDemo() {
        super("JXLayer MouseScrollableDemo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar bar = new JMenuBar();
//        bar.add(new LafMenu());
        setJMenuBar(bar);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(new JXLayer<JScrollPane>(createLeftScrollPane(), mouseScrollableUI));
        splitPane.setRightComponent(new JXLayer<JScrollPane>(createRightScrollPane(), mouseScrollableUI));
        splitPane.setDividerLocation(330);

        add(splitPane);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private JScrollPane createLeftScrollPane() {
        JPanel panel = new JPanel(new GridLayout(0, 3));
        for (int i = 0; i < 25; i++) {
            panel.add(new JTextField(8));
            panel.add(new JPanel());
            panel.add(new JCheckBox("JCheckBox"));
            panel.add(new JRadioButton("JRadioButton"));
        }
        return new JScrollPane(panel);
    }

    private JScrollPane createRightScrollPane() {
        JTable table = new JTable(new AbstractTableModel() {
            public int getRowCount() {
                return 50;
            }

            public int getColumnCount() {
                return 20;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                return "" + rowIndex + " " + columnIndex;
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return true;
            }
        });
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        return new JScrollPane(table);
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MouseScrollableDemo().setVisible(true);
            }
        });
    }
}
