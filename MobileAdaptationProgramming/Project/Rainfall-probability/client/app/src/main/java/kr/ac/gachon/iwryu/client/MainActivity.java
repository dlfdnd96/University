package kr.ac.gachon.iwryu.client;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Vibrator mVib;
    Button connectBtn;
    Button Button_send;
    EditText editText_massage;
    EditText ip_EditText;
    EditText port_EditText;
    Handler msghandler;
    TextView naverData, dht22Data;

    SocketClient client; //서버접속을 위한 클라이언트 클라스
    SendThread send; // 안드로이드에서 임의의 문자 보내는거
    ReceiveThread receive;//서버에서 보내온 데이터 안드로이드에서 보이게
    Socket socket;//네트워크

    LinkedList<SocketClient> threadList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //안드로이드 view 소스 코드 연동 레이아웃에 정의 되어있는 뷰
        ip_EditText = (EditText) findViewById(R.id.ip_EditText);
        port_EditText = (EditText) findViewById(R.id.port_EditText);
        connectBtn = (Button) findViewById(R.id.connect_Button);
        threadList = new LinkedList<SocketClient>();
        mVib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Button_send = (Button) findViewById(R.id.Button_send);
        editText_massage = (EditText) findViewById(R.id.editText_massage);
        naverData = (TextView) findViewById(R.id.naver_data);
        dht22Data = (TextView) findViewById(R.id.dht22_data);

        ip_EditText.setText("112.166.74.227"); // 접속할 서버 ip
        port_EditText.setText("5001");

        // ReceiveThread를통해서 받은 메세지를 Handler로 MainThread에서 처리(외부Thread에서는 UI변경이불가)
        msghandler = new Handler() {
            @Override
            public void handleMessage(Message hdmsg) { //생성자 클래스 정의한것처럼
                if (hdmsg.what == 1111) {
                    if (hdmsg.obj.toString().length() > 0) {
                        String[] data =hdmsg.obj.toString().split(";");

                        for (String datas : data) {
                            String[] lineData = datas.split(",");

                            if (lineData[0].length() > 3) {
                                dht22Data.append("시간: " + lineData[0] + ", 온도: " + lineData[1] + "도, 습도: " + lineData[2] + "% \n");
                            } else {
                                naverData.append("시간: " + lineData[0] + ", 온도: " + lineData[1] + "도, 습도: " + lineData[2] + "% \n");
                            }
                        }
                    }
                }
            }
        };

        // 연결버튼 클릭 이벤트
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //Client 연결부
                client = new MainActivity.SocketClient(ip_EditText.getText().toString(),
                        port_EditText.getText().toString());
                threadList.add(client);
                client.start();
            }
        });

        Button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //SendThread 시작
                if (editText_massage.getText().toString() != null) {
                    naverData.setText("");
                    dht22Data.setText("");

                    send = new SendThread(socket);
                    send.start();

                    //시작후 edittext 초기화
                    editText_massage.setText("");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mVib.cancel();
    }

    class SocketClient extends Thread {

        boolean threadAlive;
        String ip;
        String port;

        DataOutputStream output = null;

        public SocketClient(String ip, String port) {
            threadAlive = true; //현재 쓰레드가 살아있으면 존제하면
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {

            try {
                // 연결후 바로 ReceiveThread 시작
                socket = new Socket(ip, Integer.parseInt(port)); //형변환 숫자
                output = new DataOutputStream(socket.getOutputStream());
                receive = new MainActivity.ReceiveThread(socket);
                receive.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class ReceiveThread extends Thread {

        private Socket sock = null;
        DataInputStream input;

        public ReceiveThread(Socket socket) {
            this.sock = socket;
            try {
                input = new DataInputStream(sock.getInputStream());
            } catch (Exception e) {
            }
        }

        // 메세지 수신후 Handler로 전달
        public void run() {
            try {
                while (input != null) {
                    String msg;
                    int count = input.available();
                    byte[] rcv = new byte[count];
                    input.read(rcv);
                    msg = new String(rcv);

                    if (count > 0) {
                        Message hdmsg = msghandler.obtainMessage();
                        hdmsg.what = 1111;
                        hdmsg.obj = msg;
                        msghandler.sendMessage(hdmsg);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    class SendThread extends Thread {
        Socket socket;
        String sendmsg = editText_massage.getText().toString()+"\n";
        DataOutputStream output;

        public SendThread(Socket socket) {
            this.socket = socket;
            try {
                output = new DataOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void run() {

            try {
                // 메세지 전송부
                if (output != null) {
                    if (sendmsg != null) {
                        Log.d(ACTIVITY_SERVICE, sendmsg);
                        output.write(sendmsg.getBytes());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            }
        }
    }

}
