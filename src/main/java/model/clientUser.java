package model;

import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import util.Message;
import util.User;
import util.tool.manageObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

public class clientUser {
    public static Socket socket;

    public User userInfo;

    public clientUser(){
        try {
            socket = new Socket("127.0.0.1",9999);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean sendLoginInfo(Object o){
        boolean isSuccessLogin = false;
        try {
//            socket = new Socket("127.0.0.1",9999);
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(o);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            if(message.getMesType().equals("loginSuccess")){
                Logger.info("登录成功");
                isSuccessLogin = true;

                ClientConServerThread clientConServerThread = new ClientConServerThread(socket);
                manageObject.addObject("clientConServerThread",clientConServerThread);
                clientConServerThread.start();
            } else if (message.getMesType().equals("loginFailed")) {
                Logger.info("登录失败");
                isSuccessLogin = false;
                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return isSuccessLogin;
    }

    public boolean sendRegisterInfo(Object o){
        boolean isSuccessLogin = false;
        try {
//            Socket socket = new Socket("127.0.0.1",9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(o);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message message = (Message) ois.readObject();

            if (message.getMesType().equals("registerSuccess")) {
                isSuccessLogin = true;
            }
            else if(message.getMesType().equals("registerFailed")) {
                isSuccessLogin = false;
                socket.close();
            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return isSuccessLogin;
    }

}