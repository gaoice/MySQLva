import java.awt.BorderLayout;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class Msql extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	static Connection conn;
	
	JLabel l=new JLabel();
	JScrollPane sp=new JScrollPane();
	JTextArea ta=new JTextArea();
	JPanel p=new JPanel();
	JButton	b1=new JButton("执   行");
	
	Msql(JFrame f) 
	{
		super(f,false);
		
		l.setText("以分号隔开");
		sp.setViewportView(ta);
		b1.addActionListener(new bProc());
		p.add(b1);
		
		this.setLayout(new BorderLayout());
		this.add(l,BorderLayout.NORTH);
		this.add(sp);
		this.add(p,BorderLayout.SOUTH);
		
		//设置一些属性
		b1.setBackground(Color.WHITE);
				
		this.setSize(500,500);
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
			if(e.getSource()==b1)
			{
				String text=ta.getText();	
				text=text.replaceAll("[\\t\\r\\n]", "");//替换windows换行符
				text=text.replaceAll("；", ";");//替换中文分号
				if(text.equals(""))return;
				
				String sql[]=text.split(";");
				
				int error=0;
				try 
				{
					conn.setAutoCommit(false);
					Statement stmt=conn.createStatement();
					int xianzhi=1000;//缓冲区达到1000时候提交一次
					for(int i=0;i<sql.length;i++)
					{
						if(i>=xianzhi)
						{
							stmt.executeBatch();
							stmt.clearBatch();
							xianzhi+=1000;
						}
						stmt.addBatch(sql[i]); 	
					}
					stmt.executeBatch();	
					conn.commit();
					dispose();//执行成功就关闭窗口
				} 
				catch (SQLException e1) 
				{
					e1.printStackTrace();
					try 
					{
						conn.rollback();
						error=-1; 	
					} 
					catch (SQLException e2)
					{
						e2.printStackTrace();
						error=-2;
					}
				}
				try 
				{
					conn.setAutoCommit(true);
				} catch (SQLException e1) {e1.printStackTrace();}
				Fmain.fdi.setText(sql,error);
			}
		}	
	}
}

