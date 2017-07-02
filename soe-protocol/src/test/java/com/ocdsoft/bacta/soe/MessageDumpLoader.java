package com.ocdsoft.bacta.soe;

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
