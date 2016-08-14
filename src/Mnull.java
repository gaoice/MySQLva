import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

class Mnull extends JDialog
{
	private static final long serialVersionUID = 1L;
	
	JTextField tf1=new JTextField();
	JButton b=new JButton("更 改");
	
	Mnull(JFrame f) 
	{
		super(f,false);
		this.setLayout(null);
		this.add(tf1);
		this.add(b);
		
		b.addActionListener(new bProc());
		
		tf1.setBounds(30,15,160,30);
		b.setBounds(200,15,60,30);
		//设置一些属性
		b.setBackground(new Color(245,245,245));
		tf1.setText(Ttableview.kongzhi);
		
		this.setSize(300,100);
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
				if(Ttableview.kongzhi.equals(tf1.getText()))
					return;
				Ttableview.kongzhi=tf1.getText();
				Fmain.ft.removeAll();
				Fmain.ft.add(Fmain.ft.ta,"Welcome");
				Fmain.fdi.setText(Ttableview.kongzhi+"被用作表示空值", null);
				dispose();//更改成功就关闭窗口
			}
		}
		
	}
}
