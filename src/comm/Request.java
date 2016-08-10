package comm;

import java.io.Serializable;
public class Request implements Serializable{
	private Object obj;
	private String request = "";
	private String zhiling = "";
	private String Myzhanghao = "";
	private String duifangzhanghao = "";
	private int groupId;
	private String mima = "";
	private String ip = "";
	private String sendMassage = "";
	private String zhuceusername = "";
	private String zhucepswd = "";


	private String dlzhanghao = "";
	private String dlpswd = "";
	private String dangqianmulu = "";
	private String liuInt = "";
	private String FileName = "";
	private String[] listItems = null;
	private int duankou;


	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public void setZhiling(String zhiling) {
		this.zhiling = zhiling;
	}

	public String getZhiling() {
		return zhiling;
	}

	public void setMima(String mima) {
		this.mima = mima;
	}

	public String getMima() {
		return mima;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIp() {
		return ip;
	}

	public void setSendMassage(String sendMassage) {
		this.sendMassage = sendMassage;
	}

	public String getSendMassage() {
		return sendMassage;
	}

	public void setMyzhanghao(String myzhanghao) {
		Myzhanghao = myzhanghao;
	}

	public String getMyzhanghao() {
		return Myzhanghao;
	}

	public void setDuifangzhanghao(String duifangzhanghao) {
		this.duifangzhanghao = duifangzhanghao;
	}

	public String getDuifangzhanghao() {
		return duifangzhanghao;
	}

	public void setZhuceusername(String zhuceusername) {
		this.zhuceusername = zhuceusername;
	}

	public String getZhuceusername() {
		return zhuceusername;
	}

	public void setZhucepswd(String zhucepswd) {
		this.zhucepswd = zhucepswd;
	}

	public String getZhucepswd() {
		return zhucepswd;
	}

	public void setDangqianmulu(String dangqianmulu) {
		this.dangqianmulu = dangqianmulu;
	}

	public String getDangqianmulu() {
		return dangqianmulu;
	}

	public void setDlpswd(String dlpswd) {
		this.dlpswd = dlpswd;
	}

	public String getDlpswd() {
		return dlpswd;
	}

	public void setDlzhanghao(String dlzhanghao) {
		this.dlzhanghao = dlzhanghao;
	}

	public String getDlzhanghao() {
		return dlzhanghao;
	}

	public void setLiuInt(String liuInt) {
		this.liuInt = liuInt;
	}

	public String getLiuInt() {
		return liuInt;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getFileName() {
		return FileName;
	}

	public void setListItems(String[] listItems) {
		this.listItems = listItems;
	}

	public String[] getListItems() {
		return listItems;
	}

	public void setDuankou(int duankou) {
		this.duankou = duankou;
	}

	public int getDuankou() {
		return duankou;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Object getObj() {
		return obj;
	}
}
