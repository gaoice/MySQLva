import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.tree.*;

class Fdblist extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//连接数据库所用的变量
	static Connection conn;
	//构造界面所用的组件
	//树状列表
	JScrollPane treespane=new JScrollPane();
	DefaultMutableTreeNode top= new DefaultMutableTreeNode("mysqlroot");
	DefaultTreeModel dtm= new DefaultTreeModel(top);  
    JTree tree = new JTree(dtm);
    
    JPanel buttonpanel=new JPanel();
    JButton shuaxin=new JButton("刷 新");
    JLabel shanchu=new JLabel("<HTML><U>删除...</U></HTML>");
    
	Fdblist()
	{
		addtree();
        tree.addMouseListener(new treePro());  
        treespane.setViewportView(tree);
        
        shuaxin.addActionListener(new buttonProc());
        shanchu.addMouseListener(new mouseProc());
        buttonpanel.add(shuaxin);
        buttonpanel.add(shanchu);
        
        this.setLayout(new BorderLayout());
        this.add(treespane);
        this.add(buttonpanel,BorderLayout.SOUTH);
        //设置控件属性
        tree.setFont(new Font("微软雅黑",0,13)); //设置显示字体 
        shuaxin.setBackground(Color.WHITE);
	}
	void addtree()
	{
		tree.clearSelection();
		top.removeAllChildren();
		try 
		{
			Statement stmt= conn.createStatement();
			ResultSet rs= stmt.executeQuery("select schema_name from information_schema.schemata");
			while(rs.next())
			{
				String name=rs.getString("schema_name");
				DefaultMutableTreeNode jiedian= new DefaultMutableTreeNode(name);
				addnode(jiedian,name);
				top.add(jiedian);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		tree.setRootVisible(true);
		tree.expandRow(0);
		tree.setRootVisible(false);
		tree.updateUI();
		
		DefaultTreeCellRenderer treeCellRenderer; //定义绘制对象变量
		treeCellRenderer=(DefaultTreeCellRenderer)tree.getCellRenderer();//获得tree树的绘制对象
		treeCellRenderer.setLeafIcon(null); //设置叶子节点不采用图标
		treeCellRenderer.setClosedIcon(null); //设置折叠时不采用图标
		treeCellRenderer.setOpenIcon(null); //设置展开时不采用图标		
	}
	void addnode(DefaultMutableTreeNode jiedian,String name)
	{
		try 
		{
			Statement stmt= conn.createStatement();
			ResultSet rs= stmt.executeQuery("select table_name from information_schema.tables where table_schema ='"+name+"'");
			while(rs.next())
			{
				DefaultMutableTreeNode zijiedian= new DefaultMutableTreeNode(rs.getString("table_name"));
				jiedian.add(zijiedian);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	class treePro implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent arg0)
		{
			if(arg0.getButton()==MouseEvent.BUTTON1)//响应左键事件
			{
				if(arg0.getClickCount()==2)//响应双击事件
				{
					TreeNode s=(TreeNode) tree.getLastSelectedPathComponent();
					if(s==null)
						return;
					TreePath path = tree.getPathForLocation(arg0.getX(), arg0.getY());
					if(path==null)//可以确保点在节点上才触发事件
						return;
					if(s.getParent()!=top)//为叶子结点（表）打开管理页面
					{
						String pname=s.getParent().toString();
						String sname=s.toString();
						
						int i=Fmain.ft.indexOfTab(pname+"."+sname);
						if(i>-1)//打开过的不重复打开
						{
							Fmain.ft.setSelectedIndex(i);
						}
						else
						{
							if(Ttableview.conn==null)
								Login.setconn();
							Fmain.ft.add(new Ttableview(pname,sname),pname+"."+sname);
							i=Fmain.ft.indexOfTab(pname+"."+sname);
							Fmain.ft.setSelectedIndex(i);
						}
					}
				}
			}
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}		
	}
	class buttonProc implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent arg0)
		{
			if(arg0.getSource()==shuaxin)
			{
				addtree();
			}
		}	
	}
	class mouseProc implements MouseListener
	{
		@Override
		public void mouseClicked(MouseEvent arg0)
		{
			if(arg0.getButton()!=MouseEvent.BUTTON1)
				return;
			String sql="drop ";
			TreeNode s=(TreeNode) tree.getLastSelectedPathComponent();
			if(s==null)
				return;
			int n=JOptionPane.showConfirmDialog(null, "确认删除"+s.toString()+"吗?", "删除", JOptionPane.YES_NO_OPTION);  
	        if (n!=JOptionPane.YES_OPTION)  
	            return; 
			if(s.getParent()!=top)//是叶子
			{
				sql=sql+"table "+s.getParent().toString()+"."+s.toString();
				
			}
			else
			{
				sql=sql+"database "+s.toString();
			}
			
			String error=null;
			try 
			{
				Statement stmt= conn.createStatement();
				stmt.executeUpdate(sql);
				stmt.close();
			}
			catch (SQLException e1) 
			{
				error=e1.getMessage();
				e1.printStackTrace();
			}
			
			Fmain.fdi.setText(sql,error);
			
			//删除某个数据库，或数据表后，打开的页面应关闭
			if(s.getParent()!=top)//是叶子
			{
				int j=Fmain.ft.indexOfTab(s.getParent().toString()+"."+s.toString());
				if(j>-1)//删除打开的表格
				{
					Fmain.ft.remove(j);
				}
			}
			else
			{
				int count=s.getChildCount();
				for(int i=0;i<count;i++)
				{
					int j=Fmain.ft.indexOfTab(s.toString()+"."+s.getChildAt(i).toString());
					if(j>-1)//刷新打开的表格
					{
						Fmain.ft.remove(j);
					}
				}
			}
			addtree();//刷新列表
		}
		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}
		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub	
		}
	}
}

