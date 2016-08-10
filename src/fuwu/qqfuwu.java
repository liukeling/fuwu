package fuwu;

import java.io.ByteArrayOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import dbdao.dbdao;

import tools.lianjie;



class qqfuwu extends Thread{
	
	ServerSocket serverSocket;
	
	public void run(){

		try {
			serverSocket = new ServerSocket(dbdao.qqduankou);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			while (true) {
				Socket client = serverSocket.accept();
				lianjie lj = new lianjie(client);
				lj.start();
			}
		} catch (Exception e) {
			System.out.println("链接客户端出错");
			e.printStackTrace();
		}
	}
}
