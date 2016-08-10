package fuwu;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import tools.oraclecaozuo;
import tools.shangchuang;
import tools.xiazai;

import comm.Request;
import comm.Response;

public class fuwuuser extends Thread {
	private Socket userSocket;
	private static oraclecaozuo oczyun = new oraclecaozuo();

	public fuwuuser(Socket userSocket) {
		this.userSocket = userSocket;
	}

	public void run() {
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;
		Request request = null;
		Response response = null;
		try {
			ois = new ObjectInputStream(userSocket.getInputStream());
			oos = new ObjectOutputStream(userSocket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			while (true) {
				try {
					request = (Request) ois.readObject();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.out.println("��ȡ�������ʧ��");
					e.printStackTrace();
				}
				try {
					response = requestpanduan(request);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (response.getResponse().equals("�����߳��˳�")) {
					oos.writeObject(response);
					break;
				} else if (response.getResponse().equals("�ϴ���ʼ")) {
					
					new shangchuang(response, request, oos).start();
					
				} else if (response.getResponse().equals("���ؿ�ʼ")) {
					int i = (int) Math.round(Math.random() * 9000 + 1000);
					ServerSocket downServer = new ServerSocket(i);
					response.setFileduangkou(i);
					oos.writeObject(response);
					
					String dqmulu = request.getDangqianmulu();
					String[] filenames = request.getListItems();
					
					Socket downSocket = downServer.accept();
					xiazai xz = new xiazai(downSocket, dqmulu, filenames);
					xz.start();
					
				} else{
					oos.writeObject(response);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("��ȡ������");
			e.printStackTrace();
		}
	}

	public void shangchuangfangfa(Response response, ObjectOutputStream oos,
			String FileName, String dqml) throws Exception {
		response.setResponse("��ʼ�ϴ�");
		int i = (int) Math.round(Math.random() * 9000 + 1000);
		response.setFileduangkou(i);
		oos.writeObject(response);

		ServerSocket scServerSocket = new ServerSocket(i);
		Socket shangchuang = scServerSocket.accept();
		InputStream ips = shangchuang.getInputStream();
		FileOutputStream fos = new FileOutputStream(new File(dqml + "/"
				+ FileName));
		int k;
		while ((k = ips.read()) != -1) {
			fos.write(k);
			fos.flush();
		}
		ips.close();
		fos.close();
		shangchuang.close();
	}

	public Response requestpanduan(Request request)
			throws ClassNotFoundException, SQLException {
		Response response = new Response();

		String zhiLing = request.getZhiling();
		if (zhiLing.equals("�ͻ����˳�")) {
			response.setResponse("�����߳��˳�");
		} else if (zhiLing.equals("��½")) {
			String MyZhangHao = request.getDlzhanghao();
			String MyPswd = request.getDlpswd();
			boolean b = oczyun.cznamepswd(MyZhangHao, MyPswd);
			if (b) {
				response.setResponse("��½�ɹ�");
				response.setMyName(oczyun.getUser(MyZhangHao).getName());
			} else {
				response.setResponse("��½ʧ��");
			}
		} else if (zhiLing.equals("��ȡĿ¼�µ������ļ��б�")) {
			String Path = request.getDangqianmulu();
			File file = new File(Path);
			File files[] = file.listFiles();
			List<String> fileList = new ArrayList<String>();
			for (File f : files) {
				if (f.canRead()) {
					if (f.isDirectory()) {
						fileList.add(f.getName() + "               ���ͣ��ļ���");
					} else {
						fileList.add(f.getName() + "               ���ͣ��� ��");
					}
				}
			}
			response.setFileList(fileList);
		} else if (zhiLing.equals("�ϴ��ļ�")) {
			response.setResponse("�ϴ���ʼ");
		} else if (zhiLing.equals("�����ļ���")) {
			String dqmulu = request.getDangqianmulu();
			String wjjianame = request.getFileName();
			String newmulu = dqmulu + "/" + wjjianame;
			File f = new File(newmulu);
			try {
				Boolean b = f.mkdir();
				if (b) {
					response.setResponse("�����ɹ�");
				} else {
					response.setResponse("����ʧ��,��ǰĿ¼�и��ļ���");
				}
			} catch (Exception e) {
				response.setResponse("����ʧ��");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(zhiLing.equals("�����ļ�")){
			String dqmulu = request.getDangqianmulu();
			String wjjianame = request.getFileName();
			String newmulu = dqmulu + "/" + wjjianame;
			File f = new File(newmulu);
			try {
				Boolean b = f.createNewFile();
				if (b) {
					response.setResponse("�����ɹ�");
				} else {
					response.setResponse("����ʧ��,��ǰĿ¼�и��ļ�");
				}
			} catch (Exception e) {
				response.setResponse("����ʧ��");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (zhiLing.equals("ɾ��")) {
			String filenames[] = request.getListItems();
			String dqmulu = request.getDangqianmulu();
			for (int i = 0; i < filenames.length; i++) {
				File f = new File(dqmulu + "/" + filenames[i]);
				if (f.isFile()) {
					deletewenjian(f);
				} else {
					deletewenjianjia(f, f.getPath());
				}
			}
			response.setResponse("ִ�����");
		}else if(zhiLing.equals("����")){
			response.setResponse("���ؿ�ʼ");
		}
		return response;
	}

	public void deletewenjian(File f) {
		f.delete();
	}

	public void deletewenjianjia(File f, String mulu) {
		String[] filenames = f.list();
		if (filenames.length == 0) {
			f.delete();
		} else {
			for (String s : filenames) {
				String filepath = mulu + "/" + s;
				File zif = new File(filepath);
				if (zif.isFile()) {
					deletewenjian(zif);
				} else {
					deletewenjianjia(zif, filepath);
				}
			}
			f.delete();
		}
	}
}
