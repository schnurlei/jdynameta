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
package de.jdynameta.view.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

public class Arrow implements Icon 
{
    private boolean descending;

    private int size;

    private int priority;

    public Arrow(boolean descending, int size, int priority) {
        this.descending = descending;
        this.size = size;
        this.priority = priority;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color color = c == null ? Color.GRAY : c.getBackground();
        // In a compound sort, make each succesive triangle 20%
        // smaller than the previous one.
        int dx = (int) (size / 2 * Math.pow(0.8, priority));
        int dy = descending ? dx : -dx;
        // Align icon (roughly) with font baseline.
        y = y + 5 * size / 6 + (descending ? -dy : 0);
        int shift = descending ? 1 : -1;
        g.translate(x, y);

        // Right diagonal.
        g.setColor(color.darker());
        g.drawLine(dx / 2, dy, 0, 0);
        g.drawLine(dx / 2, dy + shift, 0, shift);

        // Left diagonal.
        g.setColor(color.brighter());
        g.drawLine(dx / 2, dy, dx, 0);
        g.drawLine(dx / 2, dy + shift, dx, shift);

        // Horizontal line.
        if (descending) {
            g.setColor(color.darker().darker());
        } else {
            g.setColor(color.brighter().brighter());
        }
        g.drawLine(dx, 0, 0, 0);

        g.setColor(color);
        g.translate(-x, -y);
    }

    public int getIconWidth() {
        return size;
    }

    public int getIconHeight() {
        return size;
    }
}