package fuwu;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

import dbdao.dbdao;

class yunfuwu extends Thread{
	
	ServerSocket yunServer;
	
	public void run(){
		try {
			yunServer = new ServerSocket(dbdao.yunduankou);
			while (true) {
				Socket userSocket = yunServer.accept();
				fuwuuser fwuser = new fuwuuser(userSocket);
				fwuser.start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}