package fuwu;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.SQLException;

import tools.oraclecaozuo;

import dbdao.dbdao;

public class fuwu {
	public static void main(String[] args) {

		dbdao dao = new dbdao();
		try {
			dao.init();

			Frame f = new Frame("·þÎñ¼àÌý");
			f.setLayout(new BorderLayout());
			f.setSize(250, 300);
			f.setVisible(true);
			f.setResizable(false);

			f.addWindowListener(new WindowListener() {
				public void windowActivated(WindowEvent e) {
				}

				public void windowClosed(WindowEvent e) {
				}

				public void windowClosing(WindowEvent e) {

					if (oraclecaozuo.conn != null) {
						try {
							oraclecaozuo.conn.close();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					System.exit(1);
				}

				public void windowDeactivated(WindowEvent e) {
				}

				public void windowDeiconified(WindowEvent e) {
				}

				public void windowIconified(WindowEvent e) {
				}

				public void windowOpened(WindowEvent e) {
				}
			});

			qqfuwu qfw = new qqfuwu();
			qfw.start();
			yunfuwu yfw = new yunfuwu();
			yfw.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
