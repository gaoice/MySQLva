import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;

import javax.swing.*;

class Fmain
{
	static JFrame f; 
	static int fx=1200,fy=800;
	static Fmenu fm;
	static Fdblist fdb;
	static Ftabpane ft;
	static Fdisplay fdi;
	
	public Fmain()
	{
		f=new JFrame("MySQLva");
		fm=new Fmenu();
		fdb=new Fdblist();
		ft=new Ftabpane();
		fdi=new Fdisplay();
		
		f.add(fm);
		f.add(fdb);
		f.add(ft);
		f.add(fdi);
		
		f.setLayout(null);
		f.addComponentListener(new sizeProc());
		
		Image image;
		if(new File("image/1.png").exists())
			image=new ImageIcon("image/1.png").getImage();
		else
			image=new javax.swing.ImageIcon(getClass().getResource("/image/1.png")).getImage();
		f.setIconImage(image);
		
		f.setSize(fx,fy);
		//将窗体设置在屏幕中间
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = f.getSize();
		int x = (screenSize.width - size.width) / 2;
		int y = (screenSize.height - size.height) / 2;
		f.setLocation( x, y );	
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		f.setVisible(true);
		
		//构建完毕，关闭登录窗口
		Login.fdenglu.dispose();
	}
	class sizeProc implements ComponentListener
	{
		@Override
		public void componentHidden(ComponentEvent arg0) {
			// TODO Auto-generated method stub
		}
		@Override
		public void componentMoved(ComponentEvent arg0) {
			// TODO Auto-generated method stub
		}
		@Override
		public void componentResized(ComponentEvent arg0) 
		{
			Dimension size = f.getSize();
			int x=size.width/4;
			int y=size.height/4;
			fm.setBounds(0, 0,size.width,30);
			fdb.setBounds(0,30,x,size.height-70);
			ft.setBounds(x,30,3*x-20,3*y);
			fdi.setBounds(x,3*y+30,3*x-20,y-70);
			fm.updateUI();
			fdb.updateUI();
			ft.updateUI();
			fdi.updateUI();
		}
		@Override
		public void componentShown(ComponentEvent arg0) {
			// TODO Auto-generated method stub
		}	
	}
}
