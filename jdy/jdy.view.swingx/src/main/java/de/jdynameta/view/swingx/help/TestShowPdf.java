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
package de.jdynameta.view.swingx.help;

import java.awt.HeadlessException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PagePanel;

import de.jdynameta.view.util.WindowUtil;

public class TestShowPdf
{
	private static final int READ_BLOCK = 8192;

	public static void main(String[] args)
	{
	    
        try {
        	
			String resourceName = "Test.pdf";
//			File target = File.createTempFile("Test", ".pdf");
			File tempFile = new File("/tmp",resourceName);
			tempFile.deleteOnExit();
			InputStream resourceStream = TestShowPdf.class.getResourceAsStream(resourceName);
			if ( resourceStream != null) {
				ResourceUtil.copyInputToOutput(resourceStream, tempFile);
			} else {
				throw new IOException("Resource not found: " + resourceName);
			}
			
			WindowUtil.openFile(tempFile);
        	
			//set up the frame and panel
//			PDFViewer viewer = new PDFViewer(true);
//			viewer.openFile(target);

			
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void showPage() throws IOException
	{
		// show the first page
		InputStream stream = TestShowPdf.class.getResourceAsStream("/de/jdynameta/view/swingx/Test.pdf");			
		PDFFile pdffile = new PDFFile(readToEnd(stream));
		PDFPage page = pdffile.getPage(0);
		JFrame frame = new JFrame("PDF Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		PagePanel panel = new PagePanel();
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
		
	}

	
	public static ByteBuffer readToEnd(InputStream in) throws IOException
	{
		
		ByteArrayOutputStream out = new ByteArrayOutputStream(READ_BLOCK);

		byte[] tmp = new byte[READ_BLOCK];

		while (true) {
		    int r = in.read(tmp);
		    if (r == -1) break;
		    
		    out.write(tmp,0,r);
		}

		
//		ByteBuffer buf = ByteBuffer.wrap(out.toByteArray());		
//		// create channel for input stream
//		ReadableByteChannel bc = Channels.newChannel(in);
//		ByteBuffer bb = ByteBuffer.allocate(READ_BLOCK);
//
//		while (bc.read(bb) != -1) {
//			bb = resizeBuffer(bb); // get new buffer for read
//		}
//		byte[] result = new byte[bb.position()];
//		bb.position(0);
//		bb.get(result);

		return  ByteBuffer.wrap(out.toByteArray());
	}

}
