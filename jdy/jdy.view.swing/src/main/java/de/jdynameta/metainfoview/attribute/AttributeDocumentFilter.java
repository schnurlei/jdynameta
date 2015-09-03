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
package de.jdynameta.metainfoview.attribute;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * 
 */
public class AttributeDocumentFilter extends DocumentFilter 
{

    final long maxSize;

	public AttributeDocumentFilter( long aMaxSize ) 
	{
		super();
		this.maxSize = aMaxSize;
	}

    @Override
	public void insertString(DocumentFilter.FilterBypass fb, int offset, String str,
            AttributeSet attr) throws BadLocationException {
        replace(fb, offset, 0, str, attr);
    }

    @Override
	public void replace(DocumentFilter.FilterBypass fb, int offset, int length,
            String str, AttributeSet attrs) throws BadLocationException 
    {
        int newLength = (str == null) ? 0 : fb.getDocument().getLength()-length+str.length();
        if (newLength <= maxSize) {
            fb.replace(offset, length, str, attrs);
        } else {
            throw new BadLocationException("New characters exceeds max size of document", offset);
        }
    }
}
