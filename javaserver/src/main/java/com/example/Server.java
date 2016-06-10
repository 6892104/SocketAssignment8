package com.example;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Gary on 16/5/28.
 */
public class Server implements Runnable{
    private Thread thread;
    private ServerSocket servSock;

    public Server(){


        try {
            // Detect server ip
            InetAddress IP = InetAddress.getLocalHost();
            System.out.println("IP of my system is := "+IP.getHostAddress());
            System.out.println("Waitting to connect......");

            // Create server socket
            servSock = new ServerSocket(2000);

            // Create socket thread
            thread = new Thread(this);
            thread.start();
        } catch (java.io.IOException e) {
            System.out.println("Socket啟動有問題 !");
            System.out.println("IOException :" + e.toString());
        } finally{

        }
    }

    @Override
    public void run(){
        // Running for waitting multiple client
        while(true){
            try{
                // After client connected, create client socket connect with client
                Socket clntSock = servSock.accept();
                InputStream in = clntSock.getInputStream();

                System.out.println("Connected!!");
                System.out.println("fuck!!" + clntSock.getInetAddress());
                // Transfer data
                byte[] b = new byte[1024];
                int length;
                String s;

                length = in.read(b);
                System.out.println("[Server Said] length = " + length);
                s = new String(b);
                System.out.println("[Server Said] s = " + s);
                double num1 = Double.parseDouble(s);
                System.out.println("[Server Said] num1 = " + s);
                length = in.read(b);
                s = new String(b);
                System.out.println("[Server Said] s = " + s);
                String oper = s.substring(0,1);
                System.out.println("[Server Said] oper = " + oper + " equal " + oper.equals("*"));
                length = in.read(b);
                s = new String(b);
                double num2 = Double.parseDouble(s);
                System.out.println("[Server Said] num2 = " + s);

                double result = 0;
                if(oper.equals("+"))
                    result = num1 + num2;
                else if(oper.equals("-"))
                    result = num1 - num2;
                else if(oper.equals("*"))
                    result = num1 * num2;
                else if(oper.equals("/"))
                    result = num1 / num2;

                System.out.println("[Server Said] ans =" + result);

                OutputStream out = clntSock.getOutputStream();
                String strToSend;

                byte[] sendStrByte = new byte[1024];
                strToSend = String.valueOf(result);
                System.out.println("[Server send] ans =" + strToSend);
                System.out.println("[Server send] legnth =" + strToSend.length());
                System.arraycopy(strToSend.getBytes(), 0, sendStrByte, 0, strToSend.length());
                out.write(sendStrByte);
                out.flush();
            }
            catch(Exception e){
                //System.out.println("Error: "+e.getMessage());
            }
        }
    }
}
