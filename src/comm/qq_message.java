package comm;

import java.io.Serializable;

public class qq_message implements Serializable {
	private int id;
	private String message;
	private String sendUser_zhanghao;
	private String reciveUser_zhanghao;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSendUser_zhanghao() {
		return sendUser_zhanghao;
	}
	public void setSendUser_zhanghao(String sendUser_zhanghao) {
		this.sendUser_zhanghao = sendUser_zhanghao;
	}
	public String getReciveUser_zhanghao() {
		return reciveUser_zhanghao;
	}
	public void setReciveUser_zhanghao(String reciveUser_zhanghao) {
		this.reciveUser_zhanghao = reciveUser_zhanghao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
