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
package de.jdynameta.metainfoview.metainfo;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

import de.jdynameta.metainfoview.metainfo.main.TitledSeparator;


public class SwingBordersTest extends  JPanel
{
	public SwingBordersTest()
	{
		init();
	}

	 public void init() 
	 {
	    this.setLayout(new GridLayout(2, 4, 10, 10));
	    
	    TitledBorder titledBoder = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Title");
	    titledBoder.setTitleJustification(TitledBorder.LEADING);
	    titledBoder.setTitlePosition(TitledBorder.TOP);
	    
	    this.add(showBorder(titledBoder));
	    this.add(showBorder(new EtchedBorder(EtchedBorder.RAISED)));
	    this.add(showBorder(new LineBorder(Color.BLUE)));
	    this.add(showBorder(new MatteBorder(1, 0, 0, 0, Color.BLACK)));
	    this.add(showBorder(new BevelBorder(BevelBorder.LOWERED)));
	    this.add(showBorder(new SoftBevelBorder(BevelBorder.RAISED)));
	    this.add(showBorder(new CompoundBorder(new EtchedBorder(),
	        new LineBorder(Color.RED))));
	    this.add(new JSeparator(SwingConstants.HORIZONTAL));
	    this.add(new TitledSeparator("Test Seperator"));
	    	    
	  }
	
	 static JPanel showBorder(Border b)
	 {
		    JPanel jp = new JPanel();
		    jp.setLayout(new BorderLayout());
		    String nm = b.getClass().toString();
		    nm = nm.substring(nm.lastIndexOf('.') + 1);
		    jp.add(new JLabel(nm, JLabel.CENTER), BorderLayout.CENTER);
		    jp.setBorder(b);
		    return jp;
		  }
	
	  public static void main(String[] args) 
	  {
//			UIManager.put("control", new Color(195, 220, 247));

			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			    if ("Nimbus".equals(info.getName())) {
			        try {
						UIManager.setLookAndFeel(info.getClassName());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			        break;
			    }
			}
			
		  
		    JFrame frame = new JFrame();
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.getContentPane().add(new SwingBordersTest());
		    frame.setSize(500, 300);
		    frame.setVisible(true);
		}
}
