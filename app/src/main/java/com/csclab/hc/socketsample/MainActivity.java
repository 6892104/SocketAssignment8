package com.csclab.hc.socketsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import android.util.Log;

public class MainActivity extends Activity implements android.view.View.OnClickListener{
    /** Init Variable for IP page **/
    EditText inputIP;
    Button ipSend;
    String ipAdd = "";

    /** Init Variable for Page 1 **/
    EditText inputNumTxt1;
    EditText inputNumTxt2;

    Button btnAdd;
    Button btnSub;
    Button btnMult;
    Button btnDiv;

    /** Init Variable for Page 2 **/
    TextView textResult;

    Button return_button;

    /** Init Variable **/
    String oper = "";
    double num1 = 0; // Store input num 1
    double num2 = 0; // Store input num 2
    double result = 0; // Store result after calculating

    public static final int MEG_INVALIDATE = 9527;

    Handler handler = new Handler()
    {

        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                // 當收到的Message的代號為我們剛剛訂的代號就做下面的動作。
                case MEG_INVALIDATE:
                    // 重繪UI
                    if (textResult != null) {
                        //TODO: Set the result text
                        textResult.setText(new String(num1 + " " + oper + " " + num2 + " = " + result));
                    }
                    break;

            }
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ip_page);
        inputIP = (EditText)findViewById(R.id.edIP);
        ipSend = (Button)findViewById(R.id.ipButton);

        ipSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** Func() for setup page 1 **/
                ipAdd = inputIP.getText().toString();
                jumpToMainLayout();
            }
        });
    }

    /** Function for page 1 setup */
    public void jumpToMainLayout() {
        //TODO: Change layout to activity_main
        setContentView(R.layout.activity_main);

        //btn = (Button) findViewById(R.id.button);
        //btn.setOnClickListener(this);

        //TODO: Find and bind all elements(4 buttons 2 EditTexts)
        // inputNumTxt1, inputNumTxt2
        // btnAdd, btnSub, btnMult, btnDiv
        inputNumTxt1 = (EditText) findViewById(R.id.etNum1);
        inputNumTxt2 = (EditText) findViewById(R.id.etNum2);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSub = (Button) findViewById(R.id.btnSub);
        btnMult = (Button) findViewById(R.id.btnMult);
        btnDiv = (Button) findViewById(R.id.btnDiv);

        //TODO: Set 4 buttons' listener
        // HINT: myButton.setOnClickListener(this);
        btnAdd.setOnClickListener(this);
        btnSub.setOnClickListener(this);
        btnMult.setOnClickListener(this);
        btnDiv.setOnClickListener(this);
    }

    /** Function for onclick() implement */
    @Override
    public void onClick(View v) {
        // check if the fields are empty
        if (TextUtils.isEmpty(inputNumTxt1.getText().toString())
                || TextUtils.isEmpty(inputNumTxt2.getText().toString())) {
            return;
        }

        // read EditText and fill variables with numbers
        num1 = Float.parseFloat(inputNumTxt1.getText().toString());
        num2 = Float.parseFloat(inputNumTxt2.getText().toString());

        // defines the button that has been clicked and performs the corresponding operation
        // write operation into oper, we will use it later for output
        //TODO: caculate result
        switch (v.getId()) {
            case R.id.btnAdd:
                oper = "+";
                //result = num1 + num2;
                break;
            case R.id.btnSub:
                oper = "-";
                //result = num1 - num2;
                break;
            case R.id.btnMult:
                oper = "*";
                //result = num1 * num2;
                break;
            case R.id.btnDiv:
                oper = "/";
                //result = num1 / num2;
                break;
            default:
                break;
        }
        // HINT:Using log.d to check your answer is correct before implement page turning
        Log.d("debug", "ANS " + result);
        //TODO: Pass the result String to jumpToResultLayout() and show the result at Result view
        jumpToResultLayout(new String(num1 + " " + oper + " " + num2 + " = " + result));

        Log.d("Client", "Client Send");
        Thread t = new thread(this);
        t.start();
    }

    public void jumpToResultLayout(String resultStr){
        setContentView(R.layout.result_page);

        //TODO: Bind return_button and textResult form result view
        // HINT: findViewById()
        // HINT: Remember to give type
        return_button = (Button) findViewById(R.id.return_button);
        textResult = (TextView) findViewById(R.id.textResult);

        if (textResult != null) {
            //TODO: Set the result text
            textResult.setText(resultStr);
        }

        if (return_button != null) {
            //TODO: prepare button listener for return button
            // HINT:
            // mybutton.setOnClickListener(new View.OnClickListener(){
            //      public void onClick(View v) {
            //          // Something to do..
            //      }
            // }
            return_button.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    // TODO
                    jumpToMainLayout();
                }

            });
        }
    }

    public void setAnswer(double ans){
        if (textResult != null) {
            //TODO: Set the result text
            textResult.setText(new String(num1 + " " + oper + " " + num2 + " = " + result));
        }
    }

    class thread extends Thread{
        MainActivity parent;
        public thread(MainActivity parent){
            super();
            this.parent = parent;
        }
        public void run(){
            try{
                System.out.println("Client: Waiting to connect...");
                int serverPort = 2000;

                // Create socket connect server
                Socket socket = new Socket(ipAdd, serverPort);
                System.out.println("Connected!");

                // Create stream communicate with server
                OutputStream out = socket.getOutputStream();

                byte[] sendStrByte = new byte[1024];
                String num1ToSend = String.valueOf(num1);
                System.arraycopy(num1ToSend.getBytes(), 0, sendStrByte, 0, num1ToSend.length());
                System.out.println("send num1!");
                out.write(sendStrByte);
                out.flush();
                System.out.println("end num1!");
                sendStrByte = new byte[1024];
                System.out.println("oper = " + oper + " length = " + oper.length());
                System.arraycopy(oper.getBytes(), 0, sendStrByte, 0, oper.length());
                out.write(sendStrByte);
                out.flush();
                sendStrByte = new byte[1024];
                String num2ToSend = String.valueOf(num2);
                System.arraycopy(num2ToSend.getBytes(), 0, sendStrByte, 0, num2ToSend.length());
                out.write(sendStrByte);
                out.flush();

                InputStream in = socket.getInputStream();
                // Transfer data
                byte[] b = new byte[1024];
                int length;

                length = in.read(b);
                String s = new String(b);
                System.out.println("[Server return]" + s);
                result = Double.parseDouble(s);
                System.out.println("double  = " + result);

                Message m = new Message();
                // 定義 Message的代號，handler才知道這個號碼是不是自己該處理的。
                m.what = MEG_INVALIDATE;
                handler.sendMessage(m);

            }catch (Exception e){
                System.out.println("Error : " + e.getMessage());
            }
        }
    }
}
