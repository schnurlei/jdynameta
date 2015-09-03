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

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.jxlayer.plaf.ext.DebugRepaintingUI;

/**
 * A demo to show the abilities of the {@link DebugRepaintingUI}.
 */
public class DebugRepaintingDemo extends JFrame {

    public DebugRepaintingDemo() {
        super("DebugLayerDemo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tb = new JTabbedPane();
        tb.setTabPlacement(JTabbedPane.BOTTOM);
        tb.addTab("Components", createButtonPanel());
        tb.addTab("Table", createTable());
        tb.addTab("Tree", new JTree());
        JXLayer<JComponent> layer = new JXLayer<JComponent>(tb);
        add(layer);

        final DebugRepaintingUI dp = new DebugRepaintingUI();
        layer.setUI(dp);

        setSize(400, 350);
        setLocationRelativeTo(null);
    }

    private JComponent createTable() {
        return new JScrollPane(new JTable(new AbstractTableModel() {

            public int getColumnCount() {
                return 10;
            }

            public int getRowCount() {
                return 20;
            }

            public Object getValueAt(int rowIndex, int columnIndex) {
                return "" + rowIndex;
            }
        }));
    }

    private JComponent createButtonPanel() {
        Box box = Box.createVerticalBox();
        box.add(Box.createGlue());
        addToBox(box, new JButton("JButton"));
        addToBox(box, new JRadioButton("JRadioButton"));
        addToBox(box, new JCheckBox("JCheckBox"));
        addToBox(box, new JTextField(10));
        String[] str = {"One", "Two", "Three"};
        addToBox(box, new JComboBox(str));
        addToBox(box, new JSlider(0, 100));
        return box;
    }

    private void addToBox(Box box, JComponent c) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(c);
        box.add(panel);
        box.add(Box.createGlue());
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DebugRepaintingDemo().setVisible(true);
            }
        });
    }
}
