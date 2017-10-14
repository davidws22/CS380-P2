// ============================================================================
// file: PhysLayerClient.java
// ============================================================================
// Programmer: David Shin
// Date: 10/13/2017
// Class: CS 380 ("Computer Networks")
// Time: TR 3:00 - 4:50pm
// Instructor: Mr. Davarpanah
// Project : 2
//
// Description: This program recieves a preceding stream of 64 bits of
// alternating high and low signal unsigned bytes which act as a preamble, then
// receives another 32 bytes of randomly generated data, encoded in 4B5B with NRZI.
// We then decode the data and send it back to the server and await a response of
// 1(success) or 0(failure).
//      
// ============================================================================      

import java.net.Socket;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Hashtable;


public class PhysLayerClient{

    //4B5B encoding table
    public static void _4B5BTable(Hashtable<String,String> table){
	table.put("11110","0000");
	table.put("01001","0001");
	table.put("10100","0010");
	table.put("10101","0011");
	table.put("01010","0100");
	table.put("01011","0101");
	table.put("01110","0110");
	table.put("01111","0111");
	table.put("10010","1000");
	table.put("10011","1001");
	table.put("10110","1010");
	table.put("10111","1011");
	table.put("11010","1100");
	table.put("11011","1101");
	table.put("11100","1110");
	table.put("11101","1111");
    }


    
    public static void main(String[] args) throws Exception{
	try (Socket socket = new Socket("18.221.102.182",38002)){
	    //if connection is successful, socket will be able to communicate
	    //with server
	    System.out.println("Connected to server.");
	    InputStream is = socket.getInputStream();
	    OutputStream os = socket.getOutputStream();
	    double sum = 0.0;
	    for(int i = 0; i < 64; i++){
		int input = is.read();
		sum += input;
	    }
	    //sum holds the average of the preamble
	    sum /= 64;
	    System.out.printf("Baseline established from preamble: %.2f\n", sum);
	    Hashtable<String, String> encoding = new Hashtable<>();
	    _4B5BTable(encoding);
	    String[] _4bits = new String[64];
	    boolean indicator = false;
	    for(int i = 0; i < 64; i++){
		String _5bits = "";
		for(int j = 0; j < 5; j++){
		    boolean target = is.read()>sum;
		    _5bits += (indicator==target)? "0":"1";
		    indicator = target;
		}
		_4bits[i] = encoding.get(_5bits);
	    }
	    System.out.print("Received 32 bytes: ");
	    byte[] bytes = new byte[32];
	    for(int i = 0; i < 32; i++){
		String temp = _4bits[i*2];
		String temp2 = _4bits[(i*2)+1];
		System.out.printf("%X", Integer.parseInt(temp, 2));
		System.out.printf("%X", Integer.parseInt(temp2, 2));
		String Byte = temp + temp2;
		bytes[i] = (byte)Integer.parseInt(Byte, 2);
	    }
	    System.out.println();
	    os.write(bytes);
	    if(is.read() == 1)
		System.out.println("Response good.");
	    else
		System.out.println("Response bad.");
	}
	System.out.println("Disconnected from server.");
    }

}
