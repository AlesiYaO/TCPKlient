package com.example.klienttbn;

//подключение библиотек
import android.net.LocalSocket;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;//входной поток для чтения байтов из этого сокета.
import android.view.View;
import java.io.BufferedReader;
import java.net.Socket;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


//основной класс приложения, всегда должен совпадать с названием файла
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "myServerApp"; //объявляем постоянную неизменяемую(final) строковую переменную Сервера
    Handler h;/*
    Handler может использоваться для планирования выполнения кода в некоторый момент
    в будущем. Также класс может использоваться для передачи кода,
     который должен выполняться в другом программном потоке.   */
    public OutputStream outStream = null;
    /* это абстрактный класс, определяющий байтовый потоковый вывода.
    Классы, производные от классов OutputStream
    или Writer, имеют методы с именами write()
    для записи одиночных байтов или массива байтов (отвечают за вывод данных).
    Наследники данного класса определяют куда направлять данные: в массив байтов,
     в файл или канал. Из массива байт можно создать текстовую строку String.*/
    private Socket socket;
    /*Этот класс реализует клиентские сокеты (также называемые просто «сокетами»).
     Сокет - это конечная точка для связи между двумя машинами.*/
    //создадим обработчики на кнопки
    private Button mButtonOpen;
    private Button mButtonSendChar;
    private Button mButtonClose;
    private LaptopServer mServer;
    private EditText mEditText;//объявляем переменные полей ввода, кнопки, поля вывода результата
    private TextView mTextView;
    private Button mButtonSendInt;
    BufferedReader in;

    String a, b;
    int answerTCP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonOpen =  findViewById(R.id.button_open_connection);
        mButtonSendChar = findViewById(R.id.button_send_char);
        mButtonClose = findViewById(R.id.button_close_connection);
        mEditText = findViewById(R.id.edit_text);
        mTextView = findViewById(R.id.text_view);
        mButtonSendInt = findViewById(R.id.button_send_int);
        mButtonSendChar.setEnabled(false);
        mButtonClose.setEnabled(false);
        mButtonSendInt.setEnabled(false);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                // обновляем TextView
                switch (msg.what)
                {
                    case 1: {mTextView.setText("Creat Socket 1");break;}
                    case 2: {mTextView.setText("Creat Socket 2");break;}
                    case 3: {mTextView.setText("Creat Socket 3");break;}
                    case 4: {mTextView.setText("Creat Socket Ok!");break;}
                    case 5: {mTextView.setText("Creat Socket Fail!");break;}
                    case 6: {mTextView.setText("Не отправленны данные!");break;}
                    case 7: {mTextView.setText("Не полученны данные!");break;}
                    case 8: {mTextView.setText("s_data!");break;}
                }
            };
        };



        mButtonOpen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                /*Создаем объект для работы с сервером */

                mServer = new LaptopServer();
                /*Открываем соединение. Открытие должно происходить в отедльном потокеот от ui*/
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            mServer.openConnection();
                            /*
                             * устанавливаем активные кнопки для отправки данных и закрытия соединения.
                             * Все данные по обновлению интерфейса должны обрабатываться в Ui потоке,
                             * а так как мы сейчас находимся в отдельном потоке, нам необходимо вызвать метод
                             * runOnUiThread()
                             *
                             * */
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mButtonSendChar.setEnabled(true);
                                    mButtonSendInt.setEnabled(true);
                                    mButtonClose.setEnabled(true);
                                }
                            });
                        }catch(Exception e){
                            Log.e(LOG_TAG, e.getMessage());
                            mServer = null;
                        }
                    }
                }).start();
            }
        });


        mButtonSendChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServer == null){
                    Log.e(LOG_TAG, "Сервер не создан");
                }
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        final int r = 0;
                        try {
                            /*отправляем на сервер данные*/

                            a = mEditText.getText().toString();//из первого
                            mServer.sendData(a.getBytes());//мое, где данные заносит





                        }catch (Exception e){
                            Log.e(LOG_TAG, e.getMessage());
                                h.sendEmptyMessage(7);
                        }


                    }
                }).start();
                new Thread(new Runnable() {


                        @Override
                        public void run() {
                            try {
                                LaptopServer.class.getMethods();

                                 answerTCP = mServer.mSocket.getInputStream().read();//SetText - Устанавливает текст для отображения
                                 mTextView.setText(Integer.toString(answerTCP));

                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            // Stuff that updates the UI


                    }
                }).start();
            }
        });

        mButtonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Закрываем соединение*/
                mServer.closeConnection();
                /*Устанавливаем неактивными кнопки отправки и закрытия*/
                mButtonSendChar.setEnabled(false);
                mButtonClose.setEnabled(false);
                mButtonSendInt.setEnabled(false);
            }
        });

    }
}



