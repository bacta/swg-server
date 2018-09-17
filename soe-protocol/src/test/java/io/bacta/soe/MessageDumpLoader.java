/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MessageDumpLoader {

	int sessionKey;
	
	ArrayList<byte[]> pre = new ArrayList<byte[]>();
	ArrayList<byte[]> post = new ArrayList<byte[]>();
	ArrayList<byte[]> decomp = new ArrayList<byte[]>();

	public MessageDumpLoader() {
		try {
			loadMessages();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadMessages() throws IOException {

		BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/packetdump.txt")));

		String line = br.readLine();
		sessionKey = (int) Long.parseLong(line, 16);

		while ((line = br.readLine()) != null) {

			if (line.isEmpty()) {
				continue;
			}

			ArrayList<byte[]> list = null;
			int offset = 0;

			if (line.startsWith("Pre: ")) {
				line = line.replace("Pre: ", "");
				list = pre;
			}

			if (line.startsWith("Post: ")) {
				line = line.replace("Post: ", "");
				list = post;
			}

			if (line.startsWith("Decomp: ")) {
				line = line.replace("Decomp: ", "");
				list = decomp;
				offset += 3;
			}
			
			if(list == null) {
				continue;
			}

			String[] split = line.split(",");
			byte[] myarray = new byte[split.length - offset];
			for (int i = 0; i < split.length - offset; ++i) {
				
				try {
					short value = Short.parseShort(split[i].replace("0x", ""), 16);
					myarray[i] = (byte) value;
				} catch(Exception e) {
					e.printStackTrace();
				}
			}

			list.add(myarray);
		}
		
		br.close();

	}

}
