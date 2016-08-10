package tools;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import comm.Modify_groupitem;
import comm.Request;
import comm.Response;
import comm.SysInfo;
import comm.user;

public class lianjie extends Thread {
	private oraclecaozuo oczqq = new oraclecaozuo();
	private Socket client;
	private static HashMap<String, Socket> usersSocket = new HashMap<String, Socket>();
	private static HashMap<String, Boolean> socketWork = new HashMap<String, Boolean>();
	private Request request = new Request();
	private String zhangHao = "";

	public lianjie(Socket client) {
		this.client = client;
	}

	public void run() {
		try {
			while (true) {
				ObjectInputStream ois;
				try {

					ois = new ObjectInputStream(client.getInputStream());
					request = (Request) ois.readObject();

				} catch (Exception e) {
					if (usersSocket.containsKey(zhangHao)) {
						oczqq.OutLine(zhangHao);
						usersSocket.remove(zhangHao);
						flushOthersList();
					}
					socketWork.remove(zhangHao);
					break;
				}

				if (request.getZhiling().equals("�ͻ����˳�")) {
					if (oczqq.isOnLine(zhangHao)) {
						oczqq.OutLine(zhangHao);
						if (usersSocket.containsKey(zhangHao)) {
							flushOthersList();
						}
					}
					client.close();
					break;
				}

				if (!request.getZhiling().equals("��ѯ�û��Ƿ����")
						&& !request.getZhiling().equals("����")
						&& !request.getZhiling().equals("�ͻ����˳�")
						&& !request.getZhiling().equals("����")) {
					new Thread() {
						public void run() {
							try {
								requestchuli();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				} else {
					requestchuli();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void requestchuli() throws ClassNotFoundException, SQLException,
			IOException {

		Response response = xiangYing();
		if (response.getResponse().equals("����Ϣ����")) {
			user responseuser = response.getResponseUser();

			if (responseuser.getZhuangtai().equals("����")) {

				Socket s = usersSocket.get("" + responseuser.getZhanghao());

				ObjectOutputStream oos2 = new ObjectOutputStream(s
						.getOutputStream());
				// ��δ������Ϣ���͹�ȥ��
				oos2.writeObject(response);
				oos2.flush();

			}
		} else if (response != null) {
			ObjectOutputStream oos1 = new ObjectOutputStream(client
					.getOutputStream());
			oos1.writeObject(response);
			oos1.flush();
		}
		if (response.getResponse().equals("���߳ɹ�")) {
			socketWork.put(zhangHao, true);
		}
	}

	public Response xiangYing() throws ClassNotFoundException, SQLException,
			IOException {
		Response response = new Response();
		String zhiLing = request.getZhiling();
		// System.out.println("ceshi:"+zhiLing);

		if (!zhiLing.equals("��ѯ�û��Ƿ����") && !zhiLing.equals("����")) {
			socketWork.put(zhangHao, false);
		}
		if (zhiLing.equals("��ѯ�û��Ƿ����")) {
			zhangHao = request.getMyzhanghao();
			String mima = request.getMima();
			boolean b = oczqq.cznamepswd(zhangHao, mima);
			if (b) {
				response.setResponse("��½�ɹ�");
			} else {
				response.setResponse("��½ʧ��");
			}
		} else if (zhiLing.equals("����")) {
			String ip = client.getInetAddress().getHostAddress();

			boolean b = oczqq.isOnLine(zhangHao);
			if (!b) {
				if (oczqq.shangxian(zhangHao, ip)) {

					flushOthersList();
					user me = oczqq.getUser(zhangHao);
					response.setMe(me);
					response.setResponse("���߳ɹ�");
					usersSocket.put(zhangHao, client);
					socketWork.put(zhangHao, false);
				} else {
					response.setResponse("����ʧ��");
				}
			} else {
				response.setResponse("����ʧ��");
			}
		} else if (zhiLing.equals("��ȡ�û��б�")) {
			ArrayList<HashMap<HashMap<Integer, String>, user>> al = oczqq
					.getuserList(zhangHao);
			response.setObj(al);
			System.out.println(al.size()+"::::");
			for(HashMap<HashMap<Integer, String>, user> hm : al){
				System.out.println(hm.values().iterator().next());
			}
		} else if (zhiLing.equals("����")) {
			String zhangHao = request.getMyzhanghao();
			boolean OutLine = oczqq.OutLine(zhangHao);
			if (OutLine) {
				flushOthersList();

				response.setResponse("���߳ɹ�");
			} else {
				response.setResponse("����ʧ��");
			}
		} else if (zhiLing.equals("������Ϣ")) {
			String Send_zhanghao = request.getMyzhanghao();
			String recive_zhanghao = request.getDuifangzhanghao();
			String Massage = request.getSendMassage();
			user sendUser = oczqq.getUser(Send_zhanghao);
			user responseUser = oczqq.getUser(recive_zhanghao);

			// �����͵�Massageд�����ݿ��Ϊδ����
			oczqq.addLiaoTianJiLu(Send_zhanghao, recive_zhanghao, Massage, "��",
					"��");

			// ����������Ϣ��װ����
			response.setResponse("����Ϣ����");
			response.setSendUser(sendUser);
			response.setResponseUser(responseUser);
		}
		else if (zhiLing.equals("��ȡδ�������¼")) {
			String recivezhanghao = request.getMyzhanghao();
			String sendzhanghao = request.getDuifangzhanghao();
			response = getNotReadMassageResponse(recivezhanghao, sendzhanghao);

			// ��δ���ı�Ϊ�Ѷ���ˢ���б�
			setReadedandflush(sendzhanghao);
		} else if (zhiLing.equals("ע�����û�")) {
			String newusername = request.getZhuceusername();
			String newuserpswd = request.getZhucepswd();

			String newzhanghao = oczqq.insertNewUser(newusername, newuserpswd);

			File f = new File("d:/yuntest/" + newzhanghao);
			f.mkdir();

			response.setResponseUser(oczqq.getUser(newzhanghao));
			response.setResponse("ע��ɹ�");
		} else if ("��ȡAll�����¼".equals(zhiLing)) {// �����¼�������
			response.setResponse("All�����¼");

			user frind = oczqq.getUser(request.getDuifangzhanghao());

			response.setResponseUser(frind);

			String nr = oczqq.getAlljilu(request.getMyzhanghao(), request
					.getDuifangzhanghao());

			response.setAlljilu(nr);
		} else if ("��������".equals(zhiLing)) {
			String mima = request.getZhucepswd();
			String zhanghao = request.getMyzhanghao();
			String name = request.getZhuceusername();

			int i = oczqq.resetMima(zhanghao, name, mima);
			if (i > 0) {
				response.setResponse("�޸ĳɹ�");
			} else {
				response.setResponse("�޸�ʧ��");
			}
		} else if ("��ȡ�û���Ϣ".equals(zhiLing)) {
			user user = oczqq.getUser(request.getMyzhanghao());
			response.setResponseUser(user);
			response.setResponse("�û���Ϣ");
		} else if ("��������".equals(zhiLing)) {
			String s = request.getRequest();
			if (s == null) {
				s = "";
			}
			response.setResponse("�������ѵĽ��");
			ArrayList<user> al = oczqq.getusersByString(s);
			response.setObj(al);

		} else if ("��Ӻ���_����".equals(zhiLing)) {
			// TODO
			String duifangzhanghao = request.getDuifangzhanghao();
			int item = (Integer) request.getObj();
			// ���ϵͳ��Ϣ
			int sysid = oczqq.getSysId();
			if (sysid != -1) {
				SysInfo sinfo = new SysInfo();
				sinfo.setId(sysid);
				sinfo.setOnlyOne(true);
				sinfo.setNeirong(oczqq.getUser(zhangHao).getName() + "������Ӻ���");
				sinfo.setAboutUser(duifangzhanghao);
				sinfo.setReleaseuser(zhangHao);
				sinfo.setType_huifu(0);
				boolean b = oczqq.addSysInfo(sinfo);
				oczqq.jiluUserReadSysInfo(false, "" + item, sysid,
						duifangzhanghao);
				if (b) {
					response.setResponse("��Ӻ���������");
					response.setObj(true);
				} else {
					response.setResponse("��Ӻ���������");
					response.setObj(false);
				}
			} else {
				response.setResponse("��Ӻ���������");
				response.setObj(false);
			}
			user duifang = oczqq.getUser(duifangzhanghao);
			if (duifang.getZhuangtai().equals("����")) {
				Socket duifangsocket = usersSocket.get(duifang.getZhanghao()
						+ "");
				if (duifangsocket != null) {
					// TODO
					ObjectOutputStream oos = new ObjectOutputStream(
							duifangsocket.getOutputStream());

					try {
						Response response1 = new Response();
						response1.setResponse("��ϵͳ��Ϣ");

						oos.writeObject(response1);
						oos.flush();

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		} else if ("��ȡϵͳ��Ϣ".equals(zhiLing)) {
			ArrayList<SysInfo> al = oczqq.getSysInfo(zhangHao);
			response.setResponse("����ϵͳ��Ϣ");
			response.setObj(al);
		} else if ("���Ϊ���ѵĻظ�".equals(zhiLing)) {
			String zhanghao = request.getDuifangzhanghao();
			ArrayList<Object> al = (ArrayList<Object>) request.getObj();
			SysInfo sinfo = (SysInfo) al.get(0);
			boolean agree = (Boolean) al.get(1);
			int fenzu_item1 = (Integer) al.get(2);
			// ��ϵͳ��Ϣ���Ϊ�Ѷ�
			String s = oczqq.modifyReadSysInfo(sinfo.getId() + "",
					lianjie.this.zhangHao);
			// ��˫����ӵ��Է����б���
			if (agree && fenzu_item1 != -1 && !"".equals(s)) {
				int fenzu_item2 = Integer.parseInt(s);
				if (!oczqq.isFrind(lianjie.this.zhangHao, zhanghao)
						&& !oczqq.isFrind(zhanghao, lianjie.this.zhangHao)) {
					oczqq.addFrind(zhanghao, fenzu_item1);
					oczqq.addFrind(lianjie.this.zhangHao, fenzu_item2);
					response.setObj("");
				} else {
					response.setObj("���Ǻ��ѣ��������");
				}
			}

			if (!response.getObj().equals("���Ǻ��ѣ��������")) {

				// ����µ�ϵͳ��Ϣ�����߷����ߣ������ߵĻظ�
				SysInfo newSysInfo = new SysInfo();
				int sysId = oczqq.getSysId();
				user me = oczqq.getUser(lianjie.this.zhangHao);
				String ag = "�ܾ�";
				if (agree && fenzu_item1 != -1 && !"".equals(s)) {
					ag = "ͬ��";
				}
				newSysInfo.setId(sysId);
				newSysInfo.setOnlyOne(true);
				newSysInfo.setNeirong(me.getName() + "�û�" + ag + "���������");
				newSysInfo.setAboutUser(zhanghao);
				newSysInfo.setReleaseuser(me.getZhanghao() + "");
				newSysInfo.setType_huifu(1);
				boolean addSysIsOk = oczqq.addSysInfo(newSysInfo);
				if (addSysIsOk) {
					oczqq.jiluUserReadSysInfo(false, "", newSysInfo.getId(),
							zhanghao);
				}
				// ���º����б��Լ�ϵͳ��Ϣ
				reflushSysInfo(zhanghao);
				reflushFrindList(zhanghao);
			}

			reflushSysInfo(zhangHao);
			reflushFrindList(zhangHao);
			response.setResponse("��������ظ����");
		}else if("��ϵͳ��Ϣ���Ϊ�Ѷ�".equals(zhiLing)){
			SysInfo sinfo = (SysInfo) request.getObj();
			
			oczqq.modifyReadSysInfo(sinfo.getId()+"", zhangHao);
			//����ϵͳ��Ϣ
			reflushSysInfo(zhangHao);
			
		}else if("�������".equals(zhiLing)){
			
			Modify_groupitem modify = (Modify_groupitem) request.getObj();
			int type = modify.getType();
			if(type == 2){
				oczqq.removeGroupAllFrinds(modify.getId(), modify.getRemoveId());
			}
			boolean addSeccessful = oczqq.modifyFrindGroup(modify, zhangHao);
			response.setResponse("�޸ķ�����Ϣ");
			response.setObj(addSeccessful);
			reflushFrindList(zhangHao);
		}else if("ɾ������".equals(zhiLing)){
			user frind = (user) request.getObj();
			boolean del = oczqq.delFrind(frind.getZhanghao()+"", zhangHao);
			System.out.println(zhiLing+"ɾ��:"+del);
			if(del){
				//TODO
				response.setResponse("ɾ�����ѳɹ�");
				
				user me = oczqq.getUser(zhangHao);
				SysInfo sinfo = new SysInfo();
				sinfo.setId(oczqq.getSysId());
				sinfo.setAboutUser(""+frind.getZhanghao());
				sinfo.setNeirong(me.getName()+"�ѽ���ɾ��");
				sinfo.setOnlyOne(true);
				sinfo.setRead(false);
				sinfo.setType_huifu(1);
				sinfo.setReleaseuser(zhangHao);
				boolean addInfo = oczqq.addSysInfo(sinfo);
				
				oczqq.jiluUserReadSysInfo(false, "", sinfo.getId(),
						frind.getZhanghao()+"");
				
				if(addInfo){
					response.setObj("���ϵͳ��Ϣ�ɹ�");
				}else{
					response.setObj("���ϵͳ��Ϣʧ��");
				}
				
				//�����б�
				reflushFrindList(frind.getZhanghao()+"");
				reflushFrindList(zhangHao);
				reflushSysInfo(frind.getZhanghao()+"");
			}else{
				response.setResponse("ɾ������ʧ��");
			}
		}else if("�ƶ�����".equals(zhiLing)){
			user frind = (user) request.getObj();
			int groupId = request.getGroupId();
			
			boolean remove = oczqq.moveFrind(frind, groupId);
			response.setResponse("�ƶ����ѽ��");
			response.setObj(remove);
			reflushFrindList(zhangHao);
		}
		return response;
	}
	
	private void reflushSysInfo(String zhanghao) throws IOException{
		Response response2 = new Response();
		response2.setResponse("����ϵͳ��Ϣ");

		Socket socket = usersSocket.get(zhanghao);
		
		ArrayList<SysInfo> sysinfos = oczqq.getSysInfo(zhanghao);
		response2.setObj(sysinfos);
		ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
		oos.writeObject(response2);
		oos.flush();
	}
	private void reflushFrindList(String zhanghao) throws IOException{

		Response response2 = new Response();
		ObjectOutputStream oos1 = new ObjectOutputStream(usersSocket.get(zhanghao)
				.getOutputStream());
		
		response2.setResponse("�Զ������б�");

		ArrayList<HashMap<HashMap<Integer, String>, user>> frindsfrindlist = oczqq
				.getuserList(zhanghao);
		response2.setObj(frindsfrindlist);
		oos1.writeObject(response2);
		oos1.flush();
	}

	// ��δ���ı�Ϊ�Ѷ���ˢ���б�
	public void setReadedandflush(String sendzhanghao)
			throws ClassNotFoundException, SQLException, IOException {
		Response response = new Response();
		// ��δ���ı�Ϊ�Ѷ�
		oczqq.setReaded("" + sendzhanghao, zhangHao);
		// ˢ���Լ����б�ȥ������Ϣ�ˣ�
		ObjectOutputStream oos1 = new ObjectOutputStream(client
				.getOutputStream());
		ArrayList<HashMap<HashMap<Integer, String>, user>> al = oczqq
				.getuserList(zhangHao);
		response.setObj(al);
		response.setResponse("�Զ������б�");
		oos1.writeObject(response);
		oos1.flush();
	}

	// ��ȡδ�������¼�ķ���:
	public Response getNotReadMassageResponse(String recivezhanghao,
			String sendzhanghao) throws ClassNotFoundException, SQLException {
		Response response = new Response();

		user sendUser = oczqq.getUser(sendzhanghao);
		ArrayList<String> liaotianjilu = oczqq.getNotReadjilu(sendzhanghao,
				recivezhanghao);
		response.setResponse("����δ�������¼");
		response.setSendUser(sendUser);
		response.setLiaotianjilu(liaotianjilu);

		return response;
	}

	// ��������ʱ���ø÷������Ը��������õ��б�
	public void flushOthersList() throws ClassNotFoundException, SQLException {

		Response response = new Response();

		Set<String> keys = usersSocket.keySet();
		for (String key : keys) {
			ArrayList<HashMap<HashMap<Integer, String>, user>> al = oczqq
					.getuserList(key);
			response.setObj(al);
			Socket s = usersSocket.get(key);
			// &&
			// !s.getInetAddress().getHostAddress().equals(client.getInetAddress().getHostAddress())
			if (oczqq.isOnLine(key) && !zhangHao.equals(key)) {
				response.setResponse("�Զ������б�");

				try {
					ObjectOutputStream oos = new ObjectOutputStream(s
							.getOutputStream());
					oos.writeObject(response);
					System.out.println("ceshi:�Զ������б�   " + usersSocket.size());
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("����ʧ��"
							+ s.getInetAddress().getHostAddress());
					e.printStackTrace();
				}
			}

		}
	}
}
