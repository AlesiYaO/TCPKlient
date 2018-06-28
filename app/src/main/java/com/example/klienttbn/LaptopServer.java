package com.example.klienttbn;

import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.net.Socket;

public class LaptopServer {
private static final String LOG_TAG = "myServerApp";

//ip адрес сервера который принимает соединения
    private TextView mTextView;
    private String mServerName = "89.207.73.52";

    //номер порта на который сервер принимает соединения
    private int mServerPort = 10002;

    //сокет, через который приложения общается с сервером
    public Socket mSocket = null;

    public LaptopServer(){

    }
    /**
     * *открытие нового соединения. если сокет уже открыт, то он закрывается
     *
     * @throws Exception
     * Если не удалось открыть сокет
     *
     */

    public void openConnection() throws Exception {

        /*осмвобождаем ресурсы*/
        closeConnection();
        try{
            /*Создаем новывй сокет. Указываем на каком компьютере и порту запущен наш процесс,
            который будет принимать наше соединение. */
            mSocket = new Socket(mServerName,mServerPort);

            }
        catch (IOException e){
            throw new Exception("Невозможно создать сокет:" +e.getMessage());
        }
    }

    public void closeConnection() {

        /*проверяем сокет. Если он не закрыт, то закрываем его и освобождаем соединение. */
        if (mSocket != null && !mSocket.isClosed()) {

            try {
                mSocket.close();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Невозможно закрыть сокет:" + e.getMessage());
            } finally {
                mSocket = null;
            }
        }
        mSocket = null;
    }
    /*метод для отправки данных по сокету.
    *
    * @param data
    *   данные, которы будут отправлены
    * @throws Exception
    *   Если невозможно отправить данные
    * */
    public void sendData(byte[]data) throws Exception{

        /*Проверяем сокет. Если он создан или закрыт, то выдаем исключение*/
        if(mSocket == null||mSocket.isClosed()){
            throw new Exception("Невозможно отправить данные. Сокет не создан или закрыт");
        }

        /* отправка данных */
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();


            }catch (IOException e)
        {
            throw new Exception("Невозможно отправить данные:"+e.getMessage());
        }
    }
    @Override
    protected void finalize() throws Throwable{
        super.finalize();
        closeConnection();

    }
}
