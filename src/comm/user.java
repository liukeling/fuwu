package comm;

import java.io.Serializable;

public class user implements Serializable {
	private String zhuangtai = "";
	private String name = "";
	private int zhanghao = -1;
	private String ip = "";
	private String haveMassage = "·ñ";

	public user(int zhanghao) {
		this.zhanghao = zhanghao;
	}

	public user(int zhanghao, String zhuangtai, String name, String ip,
			String haveMassage) {
		this.zhanghao = zhanghao;
		this.name = name;
		this.ip = ip;
		this.haveMassage = haveMassage;
		this.zhuangtai = zhuangtai;
	}

	public void setZhuangtai(String zhuangtai) {
		this.zhuangtai = zhuangtai;
	}

	public String getZhuangtai() {
		return zhuangtai;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setZhanghao(int zhanghao) {
		this.zhanghao = zhanghao;
	}

	public int getZhanghao() {
		return zhanghao;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public int hashCode() {
		return zhanghao;
	}

	public boolean equals(Object o) {
		boolean ok = false;

		if (o instanceof user) {
			user u = (user) o;
			if (u.getZhanghao() == this.getZhanghao()) {
				ok = true;
			}
		}

		return ok;
	}

	public void setHaveMassage(String haveMassage) {
		this.haveMassage = haveMassage;
	}

	public String getHaveMassage() {
		return haveMassage;
	}
	@Override
	public String toString() {
		return "×´Ì¬:"+zhuangtai+";ÐÕÃû:"+name+";ip:"+ip;
	}
}
