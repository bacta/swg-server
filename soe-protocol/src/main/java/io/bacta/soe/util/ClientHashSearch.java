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

package io.bacta.soe.util;

import io.bacta.shared.util.SOECRC32;
import lombok.Getter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class ClientHashSearch {

	public static void main(String[] args) throws IOException {
		ClientHashSearch search = new ClientHashSearch();
		
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command;
		
		System.out.println("Input hash to search client for");
		System.out.print("> ");
		
		while(!(command = br.readLine()).equalsIgnoreCase("quit")) {

			if(command.startsWith("0x")) {
			
				command = command.replace("0x", "");
				
				try {
					search.search(Integer.parseInt(command, 16));
				} catch(Exception e) {
					System.out.println("Invalid integer entered");
				}
			} else {
				System.out.println("Ensure your integer is entered in Hex starting with 0x");
			}
			
			System.out.println("Input String to search client for");
			System.out.print("> ");
		}
		

	}
	
	class HashCodeWorker implements Runnable {
		
		@Getter
        byte[] value;
		@Getter
        int targetHash;
		Map<String, String> matches;
		
		public HashCodeWorker(byte[] value, int targetHash, Map<String, String> matches) {
			this.targetHash = targetHash;
			this.value = value;
			this.matches = matches;
		}
		
		@Override
		public void run() {
			int hash = SOECRC32.hashCode(value);
			
			if(targetHash == hash) {
				
				synchronized(matches) {
					String key = new String(value);
					if(!matches.containsKey(key)) {
						matches.put(key, " 0x" + Integer.toHexString(targetHash));
					}
				}
				
			}
			
		}
		
//		@Override
//		public void run() {
//			byte[] hash = toBytes(SOECRC32.hashCode(value));
//			byte[] target = toBytes(targetHash);
//			
//			if(hash[0] == target[0] &&
//					hash[1] == target[1] &&
//					hash[2] == target[2] &&
//					hash[3] == target[3]) {
//				
//				synchronized(matches) {
//					matches.add(new String(value) + " 0x" + Integer.toHexString(targetHash));
//				}
//				
//			}
//			
//		}
		
	}
	
	public void search(int code) {
		
		System.out.println("Searching...");
		
		File file = new File("gameclient/SWGEmu.exe");
		if(!file.exists()) {
			System.out.println("Client not found at 'gameclient/SWGEmu.exe'");
			return;
		}
		
		Map<String, String> matches = new HashMap<String, String>();
		
		ExecutorService threadPool = Executors.newFixedThreadPool(30);
		
		int count = 0;
		long bytePosition = 20000000;
		
		int maxSize = 100;
		
		RandomAccessFile in = null;
		
		Pattern p = Pattern.compile("[^a-zA-Z0-9:_]");
		
		try {
			
			in = new RandomAccessFile(file, "r");
			long totalBytes = in.length();
			
			while(bytePosition <= totalBytes) {
				
				if(totalBytes - bytePosition < maxSize) {
					maxSize = (int) (totalBytes - bytePosition);
				}
				
				for(int i = 5; i < maxSize; ++i) {
					
					byte[] tempId = new byte[i];
					in.read(tempId);
					in.seek(bytePosition);
					
					String value = new String(tempId);
					//System.out.println(value);
					
					if(p.matcher(value).find()) {
						break;
					}
					
					//HashCodeWorker worker = new HashCodeWorker(tempId,  0x35C28E6F, matches);
					HashCodeWorker worker = new HashCodeWorker(tempId,  code, matches);
					threadPool.submit(worker);
					
				}
				
				
				bytePosition++;
				in.seek(bytePosition);
				
				if((bytePosition % (1024 * 1024)) == 0) {
					//System.out.println(((bytePosition / 1024) / 1024) + " Mb.");
				}

//				if((bytePosition % (1024)) == 0) {
//					System.out.println(((bytePosition / 1024)) + " Kb.");
//				}
				
				if(!(bytePosition <= totalBytes)) {
					if(count == 0) {
						if(matches.size() == 0) {
							bytePosition = 0;
							in.seek(bytePosition);
						}
					}
					
					count++;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if(matches.keySet().isEmpty()) {
			System.out.println("No Matches Found");
		} else {
			System.out.println("Results");
		}
		for(String key : matches.keySet()) {
			System.out.println("	" + key);
		}
		
		System.out.println();
	}
	
	byte[] toBytes(int i)
	{
	  byte[] result = new byte[4];

	  result[0] = (byte) (i >> 24);
	  result[1] = (byte) (i >> 16);
	  result[2] = (byte) (i >> 8);
	  result[3] = (byte) (i /*>> 0*/);

	  return result;
	}
}
