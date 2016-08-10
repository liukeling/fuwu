package comm;

import java.io.Serializable;

public class Modify_groupitem implements Serializable {
    //0£ºÐÞ¸Ä£»1£ºÌí¼Ó£»2£ºÉ¾³ý
    int type = -1;
    int id = -1;
    String item_Name;
    int removeId = -1;

    public int getRemoveId() {
        return removeId;
    }

    public void setRemoveId(int removeId) {
        this.removeId = removeId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItem_Name() {
        return item_Name;
    }

    public void setItem_Name(String item_Name) {
        this.item_Name = item_Name;
    }

	@Override
	public String toString() {
		return "Modify_groupitem [id=" + id + ", item_Name=" + item_Name
				+ ", removeId=" + removeId + ", type=" + type + "]";
	}
    
}
