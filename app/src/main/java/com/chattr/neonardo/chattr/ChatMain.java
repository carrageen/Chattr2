package com.chattr.neonardo.chattr;

import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;


public class ChatMain extends AppCompatActivity {

    private TextView chat;
    private EditText message;
    private boolean doubleBackToExitPressedOnce = false;
    private Client client;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_main);

        chat = (TextView) findViewById(R.id.chat);
        chat.setMovementMethod(new ScrollingMovementMethod());
        message = (EditText) findViewById(R.id.message);
        send = (Button) findViewById(R.id.send);

        //Dazu da um mit Enter senden zu können.
        message.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    send.performClick();
                    return true;
                }
                return false;
            }
        });


        /*
         * TODO Müssen wir auf jeden Fall ändern. Nur eine Notlösung um den Networkshit im main
         * Thread haben zu können weil AsycTask aids ist.
        */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            InetAddress host = InetAddress.getByName(new URL("http://niggafaggot.ddnsking.com/").getHost());
            Socket socket = new Socket(host.getHostAddress(), 4269);
            //Socket socket = new Socket("localhost", 4269);
            client = new Client(this);
            client.connect(socket);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        /*
        new AsyncTask<ChatMain, Void, Void>() {
            @Override
            protected Void doInBackground(ChatMain... cm) {
                try {
                    Log.d("test", "TEST CONNECTION1111");
                    Looper.prepare();
                    socket = new Socket("localhost", 4269);
                    Log.d("test", "TEST CONNECTION");
                    client = new Client(cm);
                    client.connect(socket);
                    Log.d("test", "TEST CONNECTION222222");
                } catch (
                        IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute() {
                chat.append("\n" + getString(R.string.connection_established));

            }
        }.execute(this);    */

    }


    public void sendMessage(View v) {
        if (!message.getText().toString().equals("")) {
            new Thread() {
                @Override
                public void run() {
                    client.send(message.getText().toString());
                    autoScroll();
                }
            }.start();
            message.setText("");
        }
    }

    public void displayMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chat.append("\n" + msg);
                autoScroll();
            }
        });
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.backButtonTwice), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                client.disconnect();
            }
        }, 2000);
    }

    public void autoScroll() {
        final int scrollAmount = chat.getLayout().getLineTop(chat.getLineCount()) - chat.getHeight();
        if (scrollAmount > 0)
            chat.scrollTo(0, scrollAmount);
        else
            chat.scrollTo(0, 0);
    }
}
