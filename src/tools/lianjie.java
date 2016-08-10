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

				if (request.getZhiling().equals("客户端退出")) {
					if (oczqq.isOnLine(zhangHao)) {
						oczqq.OutLine(zhangHao);
						if (usersSocket.containsKey(zhangHao)) {
							flushOthersList();
						}
					}
					client.close();
					break;
				}

				if (!request.getZhiling().equals("查询用户是否存在")
						&& !request.getZhiling().equals("上线")
						&& !request.getZhiling().equals("客户端退出")
						&& !request.getZhiling().equals("下线")) {
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
		if (response.getResponse().equals("有消息来了")) {
			user responseuser = response.getResponseUser();

			if (responseuser.getZhuangtai().equals("在线")) {

				Socket s = usersSocket.get("" + responseuser.getZhanghao());

				ObjectOutputStream oos2 = new ObjectOutputStream(s
						.getOutputStream());
				// 将未读的消息发送过去：
				oos2.writeObject(response);
				oos2.flush();

			}
		} else if (response != null) {
			ObjectOutputStream oos1 = new ObjectOutputStream(client
					.getOutputStream());
			oos1.writeObject(response);
			oos1.flush();
		}
		if (response.getResponse().equals("上线成功")) {
			socketWork.put(zhangHao, true);
		}
	}

	public Response xiangYing() throws ClassNotFoundException, SQLException,
			IOException {
		Response response = new Response();
		String zhiLing = request.getZhiling();
		// System.out.println("ceshi:"+zhiLing);

		if (!zhiLing.equals("查询用户是否存在") && !zhiLing.equals("上线")) {
			socketWork.put(zhangHao, false);
		}
		if (zhiLing.equals("查询用户是否存在")) {
			zhangHao = request.getMyzhanghao();
			String mima = request.getMima();
			boolean b = oczqq.cznamepswd(zhangHao, mima);
			if (b) {
				response.setResponse("登陆成功");
			} else {
				response.setResponse("登陆失败");
			}
		} else if (zhiLing.equals("上线")) {
			String ip = client.getInetAddress().getHostAddress();

			boolean b = oczqq.isOnLine(zhangHao);
			if (!b) {
				if (oczqq.shangxian(zhangHao, ip)) {

					flushOthersList();
					user me = oczqq.getUser(zhangHao);
					response.setMe(me);
					response.setResponse("上线成功");
					usersSocket.put(zhangHao, client);
					socketWork.put(zhangHao, false);
				} else {
					response.setResponse("上线失败");
				}
			} else {
				response.setResponse("上线失败");
			}
		} else if (zhiLing.equals("获取用户列表")) {
			ArrayList<HashMap<HashMap<Integer, String>, user>> al = oczqq
					.getuserList(zhangHao);
			response.setObj(al);
			System.out.println(al.size()+"::::");
			for(HashMap<HashMap<Integer, String>, user> hm : al){
				System.out.println(hm.values().iterator().next());
			}
		} else if (zhiLing.equals("下线")) {
			String zhangHao = request.getMyzhanghao();
			boolean OutLine = oczqq.OutLine(zhangHao);
			if (OutLine) {
				flushOthersList();

				response.setResponse("下线成功");
			} else {
				response.setResponse("下线失败");
			}
		} else if (zhiLing.equals("发送消息")) {
			String Send_zhanghao = request.getMyzhanghao();
			String recive_zhanghao = request.getDuifangzhanghao();
			String Massage = request.getSendMassage();
			user sendUser = oczqq.getUser(Send_zhanghao);
			user responseUser = oczqq.getUser(recive_zhanghao);

			// 将发送的Massage写入数据库标为未读：
			oczqq.addLiaoTianJiLu(Send_zhanghao, recive_zhanghao, Massage, "否",
					"否");

			// 将发送者信息封装成类
			response.setResponse("有消息来了");
			response.setSendUser(sendUser);
			response.setResponseUser(responseUser);
		}
		else if (zhiLing.equals("获取未读聊天记录")) {
			String recivezhanghao = request.getMyzhanghao();
			String sendzhanghao = request.getDuifangzhanghao();
			response = getNotReadMassageResponse(recivezhanghao, sendzhanghao);

			// 将未读的标为已读并刷新列表：
			setReadedandflush(sendzhanghao);
		} else if (zhiLing.equals("注册新用户")) {
			String newusername = request.getZhuceusername();
			String newuserpswd = request.getZhucepswd();

			String newzhanghao = oczqq.insertNewUser(newusername, newuserpswd);

			File f = new File("d:/yuntest/" + newzhanghao);
			f.mkdir();

			response.setResponseUser(oczqq.getUser(newzhanghao));
			response.setResponse("注册成功");
		} else if ("获取All聊天记录".equals(zhiLing)) {// 聊天记录框的内容
			response.setResponse("All聊天记录");

			user frind = oczqq.getUser(request.getDuifangzhanghao());

			response.setResponseUser(frind);

			String nr = oczqq.getAlljilu(request.getMyzhanghao(), request
					.getDuifangzhanghao());

			response.setAlljilu(nr);
		} else if ("更改密码".equals(zhiLing)) {
			String mima = request.getZhucepswd();
			String zhanghao = request.getMyzhanghao();
			String name = request.getZhuceusername();

			int i = oczqq.resetMima(zhanghao, name, mima);
			if (i > 0) {
				response.setResponse("修改成功");
			} else {
				response.setResponse("修改失败");
			}
		} else if ("获取用户信息".equals(zhiLing)) {
			user user = oczqq.getUser(request.getMyzhanghao());
			response.setResponseUser(user);
			response.setResponse("用户信息");
		} else if ("搜索好友".equals(zhiLing)) {
			String s = request.getRequest();
			if (s == null) {
				s = "";
			}
			response.setResponse("搜索好友的结果");
			ArrayList<user> al = oczqq.getusersByString(s);
			response.setObj(al);

		} else if ("添加好友_发送".equals(zhiLing)) {
			// TODO
			String duifangzhanghao = request.getDuifangzhanghao();
			int item = (Integer) request.getObj();
			// 添加系统消息
			int sysid = oczqq.getSysId();
			if (sysid != -1) {
				SysInfo sinfo = new SysInfo();
				sinfo.setId(sysid);
				sinfo.setOnlyOne(true);
				sinfo.setNeirong(oczqq.getUser(zhangHao).getName() + "请求添加好友");
				sinfo.setAboutUser(duifangzhanghao);
				sinfo.setReleaseuser(zhangHao);
				sinfo.setType_huifu(0);
				boolean b = oczqq.addSysInfo(sinfo);
				oczqq.jiluUserReadSysInfo(false, "" + item, sysid,
						duifangzhanghao);
				if (b) {
					response.setResponse("添加好友请求结果");
					response.setObj(true);
				} else {
					response.setResponse("添加好友请求结果");
					response.setObj(false);
				}
			} else {
				response.setResponse("添加好友请求结果");
				response.setObj(false);
			}
			user duifang = oczqq.getUser(duifangzhanghao);
			if (duifang.getZhuangtai().equals("在线")) {
				Socket duifangsocket = usersSocket.get(duifang.getZhanghao()
						+ "");
				if (duifangsocket != null) {
					// TODO
					ObjectOutputStream oos = new ObjectOutputStream(
							duifangsocket.getOutputStream());

					try {
						Response response1 = new Response();
						response1.setResponse("有系统消息");

						oos.writeObject(response1);
						oos.flush();

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}

		} else if ("获取系统消息".equals(zhiLing)) {
			ArrayList<SysInfo> al = oczqq.getSysInfo(zhangHao);
			response.setResponse("所有系统消息");
			response.setObj(al);
		} else if ("添加为好友的回复".equals(zhiLing)) {
			String zhanghao = request.getDuifangzhanghao();
			ArrayList<Object> al = (ArrayList<Object>) request.getObj();
			SysInfo sinfo = (SysInfo) al.get(0);
			boolean agree = (Boolean) al.get(1);
			int fenzu_item1 = (Integer) al.get(2);
			// 将系统消息标记为已读
			String s = oczqq.modifyReadSysInfo(sinfo.getId() + "",
					lianjie.this.zhangHao);
			// 将双方添加到对方的列表中
			if (agree && fenzu_item1 != -1 && !"".equals(s)) {
				int fenzu_item2 = Integer.parseInt(s);
				if (!oczqq.isFrind(lianjie.this.zhangHao, zhanghao)
						&& !oczqq.isFrind(zhanghao, lianjie.this.zhangHao)) {
					oczqq.addFrind(zhanghao, fenzu_item1);
					oczqq.addFrind(lianjie.this.zhangHao, fenzu_item2);
					response.setObj("");
				} else {
					response.setObj("已是好友，无需添加");
				}
			}

			if (!response.getObj().equals("已是好友，无需添加")) {

				// 添加新的系统消息，告诉发送者，接受者的回复
				SysInfo newSysInfo = new SysInfo();
				int sysId = oczqq.getSysId();
				user me = oczqq.getUser(lianjie.this.zhangHao);
				String ag = "拒绝";
				if (agree && fenzu_item1 != -1 && !"".equals(s)) {
					ag = "同意";
				}
				newSysInfo.setId(sysId);
				newSysInfo.setOnlyOne(true);
				newSysInfo.setNeirong(me.getName() + "用户" + ag + "了你的请求。");
				newSysInfo.setAboutUser(zhanghao);
				newSysInfo.setReleaseuser(me.getZhanghao() + "");
				newSysInfo.setType_huifu(1);
				boolean addSysIsOk = oczqq.addSysInfo(newSysInfo);
				if (addSysIsOk) {
					oczqq.jiluUserReadSysInfo(false, "", newSysInfo.getId(),
							zhanghao);
				}
				// 更新好友列表以及系统消息
				reflushSysInfo(zhanghao);
				reflushFrindList(zhanghao);
			}

			reflushSysInfo(zhangHao);
			reflushFrindList(zhangHao);
			response.setResponse("好友请求回复完成");
		}else if("将系统消息标记为已读".equals(zhiLing)){
			SysInfo sinfo = (SysInfo) request.getObj();
			
			oczqq.modifyReadSysInfo(sinfo.getId()+"", zhangHao);
			//更新系统消息
			reflushSysInfo(zhangHao);
			
		}else if("分组管理".equals(zhiLing)){
			
			Modify_groupitem modify = (Modify_groupitem) request.getObj();
			int type = modify.getType();
			if(type == 2){
				oczqq.removeGroupAllFrinds(modify.getId(), modify.getRemoveId());
			}
			boolean addSeccessful = oczqq.modifyFrindGroup(modify, zhangHao);
			response.setResponse("修改分组信息");
			response.setObj(addSeccessful);
			reflushFrindList(zhangHao);
		}else if("删除好友".equals(zhiLing)){
			user frind = (user) request.getObj();
			boolean del = oczqq.delFrind(frind.getZhanghao()+"", zhangHao);
			System.out.println(zhiLing+"删除:"+del);
			if(del){
				//TODO
				response.setResponse("删除好友成功");
				
				user me = oczqq.getUser(zhangHao);
				SysInfo sinfo = new SysInfo();
				sinfo.setId(oczqq.getSysId());
				sinfo.setAboutUser(""+frind.getZhanghao());
				sinfo.setNeirong(me.getName()+"已将你删除");
				sinfo.setOnlyOne(true);
				sinfo.setRead(false);
				sinfo.setType_huifu(1);
				sinfo.setReleaseuser(zhangHao);
				boolean addInfo = oczqq.addSysInfo(sinfo);
				
				oczqq.jiluUserReadSysInfo(false, "", sinfo.getId(),
						frind.getZhanghao()+"");
				
				if(addInfo){
					response.setObj("添加系统消息成功");
				}else{
					response.setObj("添加系统消息失败");
				}
				
				//更新列表
				reflushFrindList(frind.getZhanghao()+"");
				reflushFrindList(zhangHao);
				reflushSysInfo(frind.getZhanghao()+"");
			}else{
				response.setResponse("删除好友失败");
			}
		}else if("移动好友".equals(zhiLing)){
			user frind = (user) request.getObj();
			int groupId = request.getGroupId();
			
			boolean remove = oczqq.moveFrind(frind, groupId);
			response.setResponse("移动好友结果");
			response.setObj(remove);
			reflushFrindList(zhangHao);
		}
		return response;
	}
	
	private void reflushSysInfo(String zhanghao) throws IOException{
		Response response2 = new Response();
		response2.setResponse("所有系统消息");

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
		
		response2.setResponse("自动更新列表");

		ArrayList<HashMap<HashMap<Integer, String>, user>> frindsfrindlist = oczqq
				.getuserList(zhanghao);
		response2.setObj(frindsfrindlist);
		oos1.writeObject(response2);
		oos1.flush();
	}

	// 将未读的标为已读并刷新列表：
	public void setReadedandflush(String sendzhanghao)
			throws ClassNotFoundException, SQLException, IOException {
		Response response = new Response();
		// 将未读的标为已读
		oczqq.setReaded("" + sendzhanghao, zhangHao);
		// 刷新自己的列表，去掉有消息了：
		ObjectOutputStream oos1 = new ObjectOutputStream(client
				.getOutputStream());
		ArrayList<HashMap<HashMap<Integer, String>, user>> al = oczqq
				.getuserList(zhangHao);
		response.setObj(al);
		response.setResponse("自动更新列表");
		oos1.writeObject(response);
		oos1.flush();
	}

	// 获取未读聊天记录的方法:
	public Response getNotReadMassageResponse(String recivezhanghao,
			String sendzhanghao) throws ClassNotFoundException, SQLException {
		Response response = new Response();

		user sendUser = oczqq.getUser(sendzhanghao);
		ArrayList<String> liaotianjilu = oczqq.getNotReadjilu(sendzhanghao,
				recivezhanghao);
		response.setResponse("给你未读聊天记录");
		response.setSendUser(sendUser);
		response.setLiaotianjilu(liaotianjilu);

		return response;
	}

	// 上线下线时调用该方法可以更新其他用的列表
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
				response.setResponse("自动更新列表");

				try {
					ObjectOutputStream oos = new ObjectOutputStream(s
							.getOutputStream());
					oos.writeObject(response);
					System.out.println("ceshi:自动更新列表   " + usersSocket.size());
					oos.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("更新失败"
							+ s.getInetAddress().getHostAddress());
					e.printStackTrace();
				}
			}

		}
	}
}
