package dbdao;

import java.io.IOException;
import java.util.Properties;

import fuwu.fuwu;

public class dbdao {
	public static int yunduankou;
	public static int qqduankou;
	public static String oracleDriver;
	public static String oracleurl;
	public static String oracleuser;
	public static String oraclepswd;
	public void init() throws IOException{

		Properties prp = new Properties();
		prp.load(dbdao.class.getResourceAsStream("../comm/db.properties"));
		
		qqduankou = Integer.parseInt(prp.getProperty("qqduankou"));
		yunduankou = Integer.parseInt(prp.getProperty("yunduankou"));
		oracleDriver = prp.getProperty("oracleDriver");
		oracleurl = prp.getProperty("oracleurl");
		oracleuser = prp.getProperty("oracleuser");
		oraclepswd = prp.getProperty("oraclepswd");
	}
}
