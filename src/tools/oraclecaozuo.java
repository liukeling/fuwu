package tools;

import java.io.File;
import java.io.ObjectInputStream.GetField;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comm.Modify_groupitem;
import comm.SysInfo;
import comm.user;

import dbdao.dbdao;

public class oraclecaozuo {

	public Connection getlianjie() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName(dbdao.oracleDriver);
		conn = DriverManager.getConnection(dbdao.oracleurl, dbdao.oracleuser,
				dbdao.oraclepswd);
		return conn;
	}

	public void close(Connection conn, Statement stmt, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 查询用户账号和密码是否存在：
	public boolean cznamepswd(String zhanghao, String mima) {
		String sql = "select * from user_zhanghaomima where user_zhanghao='"
				+ zhanghao + "' and user_pswd='" + mima + "'";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		boolean b = false;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			b = rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return b;
	}

	// 根据ip查询账号和姓名：
	public String ip_zhanghaoname(String ip) {
		String sql = "select zi.user_zhanghao,zm.user_name from user_zhanghaomima zm,user_zhuangtai_ip zi where zm.user_zhanghao=zi.user_zhanghao and zi.user_ip='"
				+ ip + "'";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String zhanghao = "";
		String name = "";
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			rs.next();
			zhanghao = rs.getString("user_zhanghao");
			name = rs.getString("user_name");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		return name + "(" + zhanghao + ")";
	}

	// 上线：
	public boolean shangxian(String zhanghao, String ip) {
		boolean shangxiancg = false;
		String sql = "update user_zhuangtai_ip set user_zhuangtai='在线' , user_ip='"
				+ ip + "' where user_zhanghao=" + zhanghao;
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = getlianjie();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			int i = stmt.executeUpdate(sql);
			if (i != 0) {
				shangxiancg = true;
			}
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, null);
		}
		return shangxiancg;
	}

	// 修改记录
	public String modifyReadSysInfo(String sinfo_id, String userId) {
		String sql = "update sys_user set isread='y' where sysinfo_id="
				+ sinfo_id + " and user_zhanghao=" + userId;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			int i = stmt.executeUpdate(sql);
			if (i > 0) {
				sql = "select beizhu from sys_user where sysinfo_id="
						+ sinfo_id + " and user_zhanghao=" + userId;
				rs = stmt.executeQuery(sql);
				if (rs != null && rs.next()) {
					String s = rs.getString("beizhu");
					return s;
				} else {
					return "";
				}
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 用户读取系统消息时的记录
	public int jiluUserReadSysInfo(boolean isRead, String beizhu,
			int sysinfo_id, String user_id) {
		String read = "n";
		if (isRead) {
			read = "y";
		}
		String sql = "insert into sys_user (sysuser_id, isread, beizhu, sysinfo_id, user_zhanghao) "
				+ "values (sysuser_id_sqe.nextval, '"
				+ read
				+ "', '"
				+ beizhu
				+ "', " + sysinfo_id + ", " + user_id + ")";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			int i = stmt.executeUpdate(sql);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, stmt, rs);
		}

	}

	// 获取系统消息的编号
	public int getSysId() {
		int i = -1;
		String sql = "select ssinfo_id_sqe.nextval id from dual";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null && rs.next()) {
				i = rs.getInt("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		return i;
	}

	// 获取系统消息
	public ArrayList<SysInfo> getSysInfo(String zhanghao) {
		ArrayList<SysInfo> al = new ArrayList<SysInfo>();

		String sql = "select info.sysinfo_id id, info.neirong neirong, suser.beizhu beizhu,"
				+ " info.sys_time sys_date, suser.readtime read_time, suser.isread isread, "
				+ "info.isonlyone isonlyone, info.aboutuser aboutuser, info.releaseuser releaseuser, info.type_huifu type_huifu"
				+ " from system_infomation info, sys_user suser where "
				+ "(info.sysinfo_id = suser.sysinfo_id) and "
				+ "((info.isonlyone = 'n' and suser.user_zhanghao = "
				+ zhanghao
				+ ") or "
				+ "(info.isonlyone = 'y' and info.aboutuser = "
				+ zhanghao
				+ ")) order by sys_date";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();

			rs = stmt.executeQuery(sql);
			while (rs != null && rs.next()) {
				SysInfo sinfo = new SysInfo();
				int id = rs.getInt("id");
				String neirong = rs.getString("neirong");
				String beizhu = rs.getString("beizhu");
				Date sys_date = rs.getDate("sys_date");
				Date read_time = rs.getDate("read_time");
				String isread_s = rs.getString("isread");
				String isonlyone_s = rs.getString("isonlyone");
				String aboutuser = rs.getString("aboutuser");
				String releaseuser = rs.getString("releaseuser");
				int type_huifu = rs.getInt("type_huifu");
				boolean isread = false;
				if ("y".equals(isread_s)) {
					isread = true;
				} else if ("n".equals(isread_s)) {
					isread = false;
				}
				boolean isonlyone = false;
				if ("y".equals(isonlyone_s)) {
					isonlyone = true;
				} else if ("n".equals(isonlyone_s)) {
					isonlyone = false;
				}

				sinfo.setId(id);
				sinfo.setNeirong(neirong);
				sinfo.setBeizhu(beizhu);
				sinfo.setSys_date(sys_date);
				sinfo.setRead_date(read_time);
				sinfo.setRead(isread);
				sinfo.setOnlyOne(isonlyone);
				sinfo.setAboutUser(aboutuser);
				sinfo.setReleaseuser(releaseuser);
				sinfo.setType_huifu(type_huifu);
				al.add(sinfo);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return al;
	}

	// 新建系统消息
	public boolean addSysInfo(SysInfo sinfo) {
		String ny = "n";
		String sql;
		if (sinfo.isOnlyOne()) {
			ny = "y";
			sql = "insert into system_infomation "
					+ "(sysinfo_id, isonlyone, neirong, aboutuser, releaseuser, type_huifu) values "
					+ "(" + sinfo.getId() + ", '" + ny + "', '"
					+ sinfo.getNeirong() + "', " + sinfo.getAboutUser() + ", "
					+ sinfo.getReleaseuser() + ", " + sinfo.getType_huifu()
					+ ")";
		} else {
			sql = "insert into system_infomation "
					+ "(sysinfo_id, isonlyone, neirong, releaseuser) values "
					+ "(" + sinfo.getId() + ", '" + ny + "', '"
					+ sinfo.getNeirong() + "', " + sinfo.getReleaseuser()
					+ ", " + sinfo.getType_huifu() + ")";
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			int i = stmt.executeUpdate(sql);
			if (i > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn, stmt, rs);
		}

	}

	// 判断好友是否已存在
	public boolean isFrind(String myId, String frindId) {
		// select er.fenzu_item
		// from frindlist_erjimulu er, frindlist_yijimulu yi
		// where er.yiji_id = yi.yiji_id and user_me = 2 and er.fenzu_item = 1

		String sql = "select er.fenzu_item "
				+ "from frindlist_erjimulu er, frindlist_yijimulu yi "
				+ "where er.yiji_id = yi.yiji_id and user_me = " + myId
				+ " and " + "er.fenzu_item = " + frindId;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return true;
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 添加好友
	public int addFrind(String zhanghao, Integer fenzuItem) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "insert into frindlist_erjimulu values (erjimuluid_sqe.nextval, "
				+ fenzuItem + ", " + zhanghao + ")";

		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			int i = stmt.executeUpdate(sql);
			return i;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 下线：
	public boolean OutLine(String zhanghao) {
		boolean outLineOk = false;

		String sql = "update user_zhuangtai_ip set user_zhuangtai='离线' , user_ip='' where user_zhanghao="
				+ zhanghao;
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = getlianjie();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			int i = stmt.executeUpdate(sql);
			conn.commit();
			if (i != 0) {
				outLineOk = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, null);
		}

		return outLineOk;
	}

	// 查找没有好友的分组
	public ArrayList<HashMap<HashMap<Integer, String>, user>> selectNullfenzu(
			String Myzhanghao) {
		ArrayList<HashMap<HashMap<Integer, String>, user>> al = new ArrayList<HashMap<HashMap<Integer, String>, user>>();

		String sql = "select yiji_id, fenzu_item from frindlist_yijimulu yi where yi.yiji_id not in (select yiji_id from frindlist_erjimulu) and user_me="
				+ Myzhanghao;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs != null && rs.next()) {
				HashMap<HashMap<Integer, String>, user> hm = new HashMap<HashMap<Integer, String>, user>();
				HashMap<Integer, String> hm1 = new HashMap<Integer, String>();

				int id = rs.getInt(rs.findColumn("yiji_id"));
				String name = rs.getString(rs.findColumn("fenzu_item"));
				hm1.put(id, name);

				hm.put(hm1, null);
				al.add(hm);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return al;
	}

	// 查询联系人列表
	public ArrayList<HashMap<HashMap<Integer, String>, user>> getuserList(
			String Myzhanghao) {
		String sql = "select yi.yiji_id id, yi.fenzu_item key, er.fenzu_item value"
				+ " from frindlist_yijimulu yi, frindlist_erjimulu er"
				+ " where yi.yiji_id=er.yiji_id and yi.user_me=" + Myzhanghao;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		ArrayList<HashMap<HashMap<Integer, String>, user>> al = new ArrayList<HashMap<HashMap<Integer, String>, user>>();
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				HashMap<HashMap<Integer, String>, user> hm = new HashMap<HashMap<Integer, String>, user>();
				String key = rs.getString("key");
				Integer value = rs.getInt("value");
				Integer id = rs.getInt("id");
				user u = getUser(value + "");
				boolean b = haveNotRead(Myzhanghao, value + "");
				if (b) {
					u.setHaveMassage("是");
				} else {
					u.setHaveMassage("否");
				}
				HashMap<Integer, String> hm1 = new HashMap<Integer, String>();
				hm1.put(id, key);
				hm.put(hm1, u);
				al.add(hm);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		ArrayList<HashMap<HashMap<Integer, String>, user>> al1 = selectNullfenzu(Myzhanghao);
		al.addAll(al1);

		return al;
	}

	// 添加分组
	public boolean addfenzu(String zhanghao, String fenzu_name) {
		String sql = "insert into frindlist_yijimulu values (yijimuluid_sqe.nextval, "
				+ zhanghao + ", '" + fenzu_name + "')";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();

			stmt = conn.createStatement();

			boolean b = stmt.execute(sql);

			return b;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 分组好友全移动
	public boolean removeGroupAllFrinds(int from, int to) {
		boolean remove = false;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "update frindlist_erjimulu set yiji_id = " + to
				+ " where yiji_id = " + from;
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			remove = stmt.execute(sql);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return remove;
	}

	// 修改分组
	public boolean modifyFrindGroup(Modify_groupitem modify_item,
			String zhangHao) {
		boolean modify = false;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "";
			conn = getlianjie();
			stmt = conn.createStatement();
			int type = modify_item.getType();
			switch (type) {
			case 0:
				// 改
				sql = "update frindlist_yijimulu set fenzu_item = '"
						+ modify_item.getItem_Name() + "' where yiji_id = "
						+ modify_item.getId();
				int i = stmt.executeUpdate(sql);
				System.out.println(sql);
				if(i > 0){
					modify = true;
				}
				break;
			case 1:
				// 增
				sql = "insert into frindlist_yijimulu values (yijimuluid_sqe.nextval, "
						+ zhangHao + ", '" + modify_item.getItem_Name() + "')";
				int k = stmt.executeUpdate(sql);
				if(k > 0){
					modify = true;
				}
				break;
			case 2:
				// 删
				sql = "delete frindlist_yijimulu where yiji_id = "
						+ modify_item.getId();
				int j = stmt.executeUpdate(sql);
				if(j > 0){
					modify = true;
				}
				break;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return modify;
	}

	// 查询我与某某之间有没有未读的消息：
	public boolean haveNotRead(String Myzhanghao, String Otherzhanghao) {
		boolean ok = false;
		String sql = "select * from user_liaotianjilu where user_send="
				+ Otherzhanghao + "and user_recive=" + Myzhanghao
				+ "and isRead='否'";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			ok = rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return ok;
	}

	// 更改密码
	public int resetMima(String zhanghao, String name, String mima) {
		int i = 0;
		// user_zhanghao,user_name,user_pswd
		String sql = "update user_zhanghaomima set user_pswd='" + mima
				+ "' where user_zhanghao=" + zhanghao + " and user_name='"
				+ name + "'";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			i = stmt.executeUpdate(sql);
			return i;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 删除好友
	public boolean delFrind(String frindId, String MyId) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "delete frindlist_erjimulu er " + "where er.fenzu_item = "
				+ frindId + " and er.yiji_id in "
				+ "(select yi.yiji_id from frindlist_yijimulu yi"
				+ " where yi.user_me = " + MyId + ")";
		try {

			conn = getlianjie();
			stmt = conn.createStatement();
			int a = stmt.executeUpdate(sql);
			sql = "delete frindlist_erjimulu er " + "where er.fenzu_item = "
					+ MyId + " and er.yiji_id in "
					+ "(select yi.yiji_id from frindlist_yijimulu yi"
					+ " where yi.user_me = " + frindId + ")";
			int b = stmt.executeUpdate(sql);

			if (a > 0 && b > 0) {
				return true;
			}
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			close(conn, stmt, rs);
		}
	}

	// 根据账号获得一个user:
	public user getUser(String zhanghao) {
		user u = new user(Integer.parseInt(zhanghao));
		String sql = "select user_ip,user_name,user_zhuangtai from user_zhanghaomima zm,user_zhuangtai_ip zi where zm.user_zhanghao = zi.user_zhanghao and zm.user_zhanghao = "
				+ zhanghao;

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				u.setIp(rs.getString("user_ip"));
				u.setName(rs.getString("user_name"));
				u.setZhuangtai(rs.getString("user_zhuangtai"));
			} else {
				System.out.println("在数据库中没找到账号为" + zhanghao + "人的信息");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return u;
	}

	// 是否在线：
	public boolean isOnLine(String zhangHao) {
		boolean onLine = false;
		String sql = "select * from user_zhuangtai_ip where user_zhanghao='"
				+ zhangHao + "' and user_zhuangtai='在线'";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			onLine = rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return onLine;
	}

	// 根据字符串查好友
	public ArrayList<user> getusersByString(String s) {
		ArrayList<user> al = new ArrayList<user>();
		s = "%" + s + "%";
		String sql = "select user_zhanghao from user_zhanghaomima where user_name like '"
				+ s + "' or user_zhanghao like '" + s + "'";

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);

			while (rs != null && rs.next()) {
				int zhanghao = rs.getInt(rs.findColumn("user_zhanghao"));
				user u = getUser(zhanghao + "");
				al.add(u);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return al;
	}

	// 增加聊天记录
	public boolean addLiaoTianJiLu(String zhanghao_send,
			String zhanghao_recive, String neirong, String isRead, String isSend) {
		boolean ok = false;
		String sql = "insert into user_liaotianjilu (jilu_id,user_send,user_recive,neirong,nr_time,isRead,isSend) values ("
				+ "jilu_id.nextval,"
				+ zhanghao_send
				+ ","
				+ zhanghao_recive
				+ ",'"
				+ neirong
				+ "',sysdate,'"
				+ isRead
				+ "','"
				+ isSend
				+ "')";

		Connection conn = null;
		Statement stmt = null;

		try {
			conn = getlianjie();
			conn.setAutoCommit(false);
			stmt = conn.createStatement();

			stmt.executeUpdate(sql);
			conn.commit();
			ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, null);
		}

		return ok;
	}

	public String getAlljilu(String Myzhanghao, String frindzhanghao) {
		String sql = "select * from user_liaotianjilu where user_send in ("
				+ Myzhanghao + "," + frindzhanghao + ") and user_recive in ("
				+ Myzhanghao + "," + frindzhanghao + ") order by NR_TIME";
		String jilu = "";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getString("USER_SEND").equals(Myzhanghao)) {
					jilu = jilu + "\n我：\n" + rs.getString("NEIRONG") + "\n";
				} else if (rs.getString("USER_SEND").equals(frindzhanghao)) {
					jilu = jilu + "\n对方：\n" + rs.getString("NEIRONG") + "\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jilu;
	}

	// 获取未读的聊天记录：
	public ArrayList<String> getNotReadjilu(String sendzhanghao,
			String recivezhanghao) {
		String sql = "select neirong from user_liaotianjilu where user_send="
				+ sendzhanghao + " and user_recive=" + recivezhanghao
				+ " and isread='否' order by NR_TIME";
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<String> al = null;

		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			al = new ArrayList<String>();
			while (rs.next()) {
				al.add(rs.getString("neirong"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}

		return al;
	}

	// 把未读改为已读：
	public void setReaded(String sendzhanghao, String recivezhanghao) {
		String sql = "update user_liaotianjilu set isread='是', issend='是' where user_send="
				+ sendzhanghao + " and user_recive=" + recivezhanghao;

		Connection conn = null;
		Statement stmt = null;

		try {
			conn = getlianjie();
			stmt = conn.createStatement();

			stmt.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, null);
		}
	}
	
	//好友移动至某分组中
	public boolean moveFrind(user frind, int groupId){
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "update frindlist_erjimulu set yiji_id = "+groupId+" where fenzu_item = "+frind.getZhanghao();
		try{
			
			conn = getlianjie();
			stmt = conn.createStatement();
			int i = stmt.executeUpdate(sql);
			if(i > 0){
				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}finally{
			close(conn, stmt, rs);
		}
	}

	// 创建新用户:
	public String insertNewUser(String newusername, String newpswd) {
		String zhanghao = "";
		String zhanghaochaxun = "select javaqq_test.nextval zhanghao from dual";

		Connection conn = null;
		Statement stmt = null;

		ResultSet rs = null;

		try {
			conn = getlianjie();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(zhanghaochaxun);

			rs.next();
			zhanghao = rs.getString("zhanghao");

			conn.setAutoCommit(false);

			String sql1 = "insert into user_zhanghaomima (user_zhanghao,user_name,user_pswd) values ("
					+ zhanghao + ",'" + newusername + "','" + newpswd + "')";
			String sql2 = "insert into user_zhuangtai_ip ( user_zhanghao,user_zhuangtai,user_ip) values ("
					+ zhanghao + ",'离线','')";

			stmt.executeUpdate(sql1);
			stmt.executeUpdate(sql2);
			conn.commit();
			// 注册成功后给新用户分配云盘
			File f = new File("H:/yuntest/" + zhanghao);
			f.mkdir();
			// 注册成功后给一个“好友列表”分组
			addfenzu(zhanghao, "好友列表");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, stmt, rs);
		}
		return zhanghao;
	}

}
