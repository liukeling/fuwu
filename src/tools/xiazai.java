package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import comm.Response;

public class xiazai extends Thread{
	
	private Socket xiazaiSocket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String dqmulu;
	private String[] filenames;
	
	public xiazai(Socket xiazaiSocket, String dqmulu, String[] filenames){
		this.xiazaiSocket = xiazaiSocket;
		this.filenames = filenames;
		this.dqmulu = dqmulu;
		
		try {
			oos = new ObjectOutputStream(xiazaiSocket.getOutputStream());
			ois = new ObjectInputStream(xiazaiSocket.getInputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run(){
		File[] files = new File[filenames.length];
		for(int i = 0; i < filenames.length; i ++){
			files[i] = new File(dqmulu + "/" + filenames[i]);
		}
		
		String linshimulu = "";
		
		for(File f : files){
			if(f.isFile()){
				try {
					xiazaiwenjianfangfa(f, linshimulu);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(f.isDirectory()){
				try {
					xiazaiwenjianjia(f, linshimulu);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.out.println("该文件读取错误");
			}
		}
	}
	public void xiazaiwenjianfangfa(File f, String linshimulu) throws IOException{
		Response response = new Response();
		response.setResponse("下载");
		int i = (int) Math.round(Math.random() * 9000 + 1000);
		response.setFileduangkou(i);
		response.setFileName(f.getName());
		response.setLinshimulu(linshimulu);
		
		oos.writeObject(response);
		
		ServerSocket dserver = new ServerSocket(i);
		Socket s = dserver.accept();
		
		OutputStream ops = s.getOutputStream();
		FileInputStream fis = new FileInputStream(f);
		int k;
		while((k = fis.read()) != -1){
			ops.write(k);
		}
		ops.close();
		fis.close();
		s.close();
	}
	public void xiazaiwenjianjia(File f, String linshimulu) throws IOException{
		String wenjianjianame = f.getName();
		Response response = new Response();
		response.setResponse("下载文件夹");
		response.setFileName(wenjianjianame);
		response.setLinshimulu(linshimulu);
		oos.writeObject(response);
		String files[] = f.list();
		String mulu = f.getPath();
		
		for(int i = 0; i < files.length; i ++){
			File zifile = new File(mulu + "/" + files[i]);
			
			if(zifile.isFile()){
				xiazaiwenjianfangfa(zifile, linshimulu + "/" + f.getName());
			}else if(zifile.isDirectory()){
				xiazaiwenjianjia(zifile, linshimulu + "/" + f.getName());
			}else{
				System.out.println("出错了");
			}
			
		}
		
	}
}
