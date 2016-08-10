package comm;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Response implements Serializable{
	private String response = "";
	private ArrayList<user> friends = new ArrayList<user>();
	private user responseUser = new user(-1);
	private user sendUser = new user(-1);
	private user me = new user(-1);
	private ArrayList<String> liaotianjilu;
	private ArrayList<user> Allliaotianjilu = new ArrayList<user>();
	private String Alljilu = "";
	

	private String myName = "";
	private String FileName = "";
	private String linshimulu = "";
	private List<String> fileList = new ArrayList<String>();
	private int fileduangkou;
	private Object obj = null;

	public void setResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	public void setFriends(ArrayList<user> friends) {
		this.friends = friends;
	}

	public ArrayList<user> getFriends() {
		return friends;
	}

	public void setResponseUser(user responseUser) {
		this.responseUser = responseUser;
	}

	public user getResponseUser() {
		return responseUser;
	}

	public void setSendUser(user sendUser) {
		this.sendUser = sendUser;
	}

	public user getSendUser() {
		return sendUser;
	}

	public void setLiaotianjilu(ArrayList<String> liaotianjilu) {
		this.liaotianjilu = liaotianjilu;
	}

	public ArrayList<String> getLiaotianjilu() {
		return liaotianjilu;
	}

	public void setAllliaotianjilu(ArrayList<user> allliaotianjilu) {
		Allliaotianjilu = allliaotianjilu;
	}

	public ArrayList<user> getAllliaotianjilu() {
		return Allliaotianjilu;
	}

	public void setMe(user me) {
		this.me = me;
	}

	public user getMe() {
		return me;
	}

	public void setFileList(List<String> fileList) {
		this.fileList = fileList;
	}

	public List<String> getFileList() {
		return fileList;
	}

	public void setLinshimulu(String linshimulu) {
		this.linshimulu = linshimulu;
	}

	public String getLinshimulu() {
		return linshimulu;
	}

	public void setFileName(String fileName) {
		FileName = fileName;
	}

	public String getFileName() {
		return FileName;
	}

	public void setMyName(String myName) {
		this.myName = myName;
	}

	public String getMyName() {
		return myName;
	}

	public void setFileduangkou(int fileduangkou) {
		this.fileduangkou = fileduangkou;
	}

	public int getFileduangkou() {
		return fileduangkou;
	}

	public void setAlljilu(String alljilu) {
		Alljilu = alljilu;
	}

	public String getAlljilu() {
		return Alljilu;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
