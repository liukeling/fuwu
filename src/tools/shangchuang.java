package tools;

import java.io.ObjectOutputStream;

import comm.Request;
import comm.Response;
import fuwu.fuwuuser;

public class shangchuang extends Thread{
	Response response;
	Request request;
	ObjectOutputStream oos;
	public shangchuang(Response response, Request request, ObjectOutputStream oos){
		this.request = request;
		this.response = response;
		this.oos = oos;
	}
	public void run(){

		
		String dqml = request.getDangqianmulu();
		try {
			String name = request.getFileName();
			new fuwuuser(null).shangchuangfangfa(response, oos, name, dqml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

