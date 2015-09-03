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
package de.jdynameta.metainfoview.metainfo.main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * simple separator which shows and text and a horizontal seperator line
 * @author rs
 *
 */
@SuppressWarnings("serial")
public class TitledSeparator extends JPanel
{
	private JLabel textLbl ;
	
	public TitledSeparator(String aText)
	{
		super(new GridBagLayout());
		
		GridBagConstraints constr = new GridBagConstraints(GridBagConstraints.RELATIVE,0, 1,1 
															, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE
															, new Insets(0, 0, 0, 3), 0,0 ); 
		
		
		this.textLbl = new JLabel(aText);
		this.add(this.textLbl, constr);
		constr.fill = GridBagConstraints.BOTH; constr.weightx =1.0;
		this.add(new EtchedLine(), constr);
	}
	
}
