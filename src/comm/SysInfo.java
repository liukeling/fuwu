package comm;

import java.io.Serializable;
import java.sql.Date;

public class SysInfo implements Serializable {
	private String neirong;
	private String beizhu;
	private Date sys_date;
	private Date read_date;
	private Object obj;
	private boolean isRead;
	private boolean isOnlyOne;
	private int id;
	private String aboutUser;
	private int type_huifu;
	private String releaseuser;
	
	public void setNeirong(String neirong) {
		this.neirong = neirong;
	}
	public String getNeirong() {
		return neirong;
	}
	public void setSys_date(Date sys_date) {
		this.sys_date = sys_date;
	}
	public Date getSys_date() {
		return sys_date;
	}
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public Object getObj() {
		return obj;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public boolean isRead() {
		return isRead;
	}
	public void setOnlyOne(boolean isOnlyOne) {
		this.isOnlyOne = isOnlyOne;
	}
	public boolean isOnlyOne() {
		return isOnlyOne;
	}
	public void setAboutUser(String aboutUser) {
		this.aboutUser = aboutUser;
	}
	public String getAboutUser() {
		return aboutUser;
	}
	public void setReleaseuser(String releaseuser) {
		this.releaseuser = releaseuser;
	}
	public String getReleaseuser() {
		return releaseuser;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}
	public String getBeizhu() {
		return beizhu;
	}
	public void setRead_date(Date read_date) {
		this.read_date = read_date;
	}
	public Date getRead_date() {
		return read_date;
	}
	public void setType_huifu(int type_huifu) {
		this.type_huifu = type_huifu;
	}
	public int getType_huifu() {
		return type_huifu;
	}
	
}
