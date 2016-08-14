import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

class Mcdb extends JDialog
{
	static Connection conn;
	private static final long serialVersionUID = 1L;
	JPanel p=new JPanel();
	JLabel l1=new JLabel("数据库名");
	JTextField tf1=new JTextField();
	JLabel l2=new JLabel("字符集");
	JTextField tf2=new JTextField();
	JButton b=new JButton("执   行");
	
	Mcdb(JFrame f) 
	{
		super(f,false);
		p.setLayout(null);
		p.add(l1);
		p.add(tf1);
		p.add(l2);
		p.add(tf2);
		p.add(b);
		
		b.addActionListener(new bProc());
		
		l1.setBounds(30, 20, 70, 30);
		tf1.setBounds(100,20, 200, 30);
		l2.setBounds(30, 70, 70, 30);
		tf2.setBounds(100,70, 200, 30);
		b.setBounds(160,120, 80, 30);
		this.add(p);
		
		//设置一些属性
		b.setBackground(Color.WHITE);
		
		this.setSize(400,200);
		//将窗体设置在屏幕中间
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = this.getSize();
		int x = (screenSize.width - size.width) / 2;
		int y = (screenSize.height - size.height) / 2;
		this.setLocation( x, y );
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
		this.setModal(true);
		this.setVisible(true);	
	}
	class bProc implements ActionListener
	{
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getSource()==b)
			{
				StringBuffer sql =new StringBuffer("create database ");
				if(tf1.getText().equals(""))
				{
					return;
				}
				sql=sql.append(tf1.getText());
				if(!tf2.getText().equals(""))
				{
					sql=sql.append(" CHARACTER SET "+tf2.getText());
				}
				String error=null;
				try 
				{
					Statement stmt=conn.createStatement();
					stmt.executeUpdate(sql.toString());
					stmt.close();
					dispose();//创建成功就关闭窗口
				}
				catch (SQLException e1) 
				{
					error=e1.getMessage();
					e1.printStackTrace();
				}
				Fmain.fdi.setText(sql.toString(),error);
				Fmain.fdb.addtree();
			}
		}
		
	}
}
