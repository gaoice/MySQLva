import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

class Ttableview extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	//连接数据库所用的变量，需要赋值
	static Connection conn;//需赋值
	String databasename;//需赋值
	String tablename;//需赋值
	//一些全局变量
	int tablecnum;//表中列的个数
	String tablecname[];//表中列的名字
	//空值表示，本函数所用的空值表示，在表格显示的时候，和""空字符串区分开来，在显示和增加，修改，查询过程中都会将该字符串认为空值处理
	static String kongzhi="Null(1)";
	Object oldvalue[][];//保存被修改前的旧值，用以确认唯一的修改对象。注意，每张表都对应一个旧值表，更新内容时候会被同步刷新。
	//构造界面所用的组件
	JPanel panel=new JPanel();
	JScrollPane spanel=new JScrollPane();	
	//此处三个变量在filltable()函数中初始化
	DefaultTableModel tablemodel;	
	JTable table;
	JTable table2;//输入面板
	
	JPanel buttonpanel=new JPanel();
	JButton add=new JButton("增加");
	JButton delete=new JButton("删除");
	JButton select=new JButton("查找");
	JButton refresh=new JButton("刷新");
	
	Ttableview(String dname,String tname)
	{	
		databasename=dname;
		tablename=tname;
		if(conn==null)
			return;
		
		filltable();
		table.setFont(new Font("微软雅黑",0,13));
		table2.setFont(new Font("微软雅黑",0,13));
		spanel.setViewportView(table);
		panel.setLayout(new BorderLayout());
		panel.add(spanel);
		panel.add(table2,BorderLayout.SOUTH);
		
		add.addActionListener(new buttonProc());
		delete.addActionListener(new buttonProc());
		select.addActionListener(new buttonProc());
		refresh.addActionListener(new buttonProc());
		buttonpanel.add(add);
		buttonpanel.add(delete);
		buttonpanel.add(select);
		buttonpanel.add(refresh);
		
		this.setLayout(new BorderLayout());
		this.add(panel);
		this.add(buttonpanel,BorderLayout.SOUTH);
		//设置一些颜色和其他属性	
		add.setBackground(Color.WHITE);//new Color(245,245,245));
		delete.setBackground(Color.WHITE);
		select.setBackground(Color.WHITE);
		refresh.setBackground(Color.WHITE);
	}
	//=======================================================================================================初始化表格
	void filltable()
	{
		try 
		{
			Statement stmt= conn.createStatement();
			String sql="select column_name from information_schema.columns where table_name='"+tablename+"'and table_schema ='"+databasename+"'";
			ResultSet rs= stmt.executeQuery(sql);
			
			while(rs.next())
			{
				tablecnum++;
			}
			tablecname=new String[tablecnum];//存放当前表的列名
			table2=new JTable(1,tablecnum);
			oldvalue=new Object[100][tablecnum];//最多为100行，构造一个最大数组，来放旧值
			
			rs=stmt.executeQuery(sql);//因为rs.next()统计个数，重新初始化			
			for(int j=0;j<tablecnum;j++)
			{
				rs.next();
				tablecname[j]=new String(rs.getString("column_name"));	
			}
			
			tablemodel=new DefaultTableModel(null,tablecname);
			table=new JTable(tablemodel)
					{
						private static final long serialVersionUID = 1L;
						//Implement table cell tool tips.增加行提示
			            public String getToolTipText(MouseEvent e) 
			            {
			                String tip = null;
			                java.awt.Point p = e.getPoint();
			                int rowIndex = rowAtPoint(p);
			                int colIndex = columnAtPoint(p);
			                tip=(String) this.getValueAt(rowIndex,colIndex);
			                return tip;
			            }

						//Implement table header tool tips. 增加表头提示
						protected JTableHeader createDefaultTableHeader() 
						{
							return new JTableHeader(columnModel) 
							{
								private static final long serialVersionUID = 1L;
								public String getToolTipText(MouseEvent e) 
								{
									java.awt.Point p = e.getPoint();
									int index = columnModel.getColumnIndexAtX(p.x);
									int realIndex = columnModel.getColumn(index).getModelIndex();
									return tablecname[realIndex];
								}
							};
						}
					};
			//设置一些表的属性
			table.getTableHeader().setReorderingAllowed(false);//禁止拖动列头排序，不然会造成修改错误
			table2.setSelectionBackground(new Color(100,200,200));
			//截至目前代码，表的列名已经初始化完毕，列数在tablecnum中
			//开始向表格填充数据
			String sql2="select * from "+databasename+"."+tablename+" limit 0,100";
			//System.out.println(sql2);
			rs=stmt.executeQuery(sql2);	
			Object rowvalue[]=new Object[tablecnum];
			
			int i=0;
			while(rs.next())
			{		
				for(int j=0;j<tablecnum;j++)
				{
					rowvalue[j]=rs.getString(tablecname[j]);
					if(rowvalue[j]==null)
						rowvalue[j]=kongzhi;
					
					oldvalue[i][j]=rowvalue[j];//存到缓冲表中
				}
				i++;
				tablemodel.addRow(rowvalue);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}
		tablemodel.addTableModelListener(new tableProc());
	}
//================================================================================================================单表刷新
	void shuaxintable()//刷新表格
	{
		//不能试图在这个函数里面取消编辑状态，会触发修改任务，造成程序失败，应该在某个任务之前取消编辑状态
		try 
		{
			Statement stmt= conn.createStatement();
			tablemodel.setRowCount(0);//先清空
					
			String sql="select * from "+databasename+"."+tablename+" limit 0,100";	
			ResultSet rs=stmt.executeQuery(sql);		
			Object rowvalue[]=new Object[tablecnum];		
			int i=0;
			while(rs.next())
			{		
				for(int j=0;j<tablecnum;j++)
				{
					rowvalue[j]=rs.getString(tablecname[j]);
					if(rowvalue[j]==null)
						rowvalue[j]=kongzhi;
					
					oldvalue[i][j]=rowvalue[j];//存到缓冲表中
				}
				i++;
				tablemodel.addRow(rowvalue);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
		}		
	}
//================================================================================================================修改功能
	class tableProc implements TableModelListener
	{
		public void tableChanged(TableModelEvent e)
		{
			if(e.getType()==TableModelEvent.UPDATE)
			{
				int hang=table.getSelectedRow();//行
				int lie=table.getSelectedColumn();//列
				if(hang<0)//增加这个判断是必要的
					return;	
				String sql=null;
				int i=0;
				String error=null;
				try 
				{
					Statement stmt= conn.createStatement();	
					Object value;
					value=table.getValueAt(hang,lie);
					if(value.equals(oldvalue[hang][lie]))
						return;
					
					if(kongzhi.equals(value))
						sql="update "+databasename+"."+tablename+" set "+tablecname[lie]+" = null where ";
					else
						sql="update "+databasename+"."+tablename+" set "+tablecname[lie]+" = '"+value+"' where ";
						
					value=null;//标记第几次,此处可以用null来判别，而增加功能中不可以
					for(int j=0;j<tablecnum;j++)
					{
						if(value!=null)
								sql=sql+" and ";
						value=table.getValueAt(hang,j);
						if(j==lie)
								value=oldvalue[hang][lie];
						if(kongzhi.equals(value))
							sql=sql+table.getColumnName(j)+" is null ";
						else
							sql=sql+table.getColumnName(j)+" = '"+value+"' ";	
					}
					//System.out.println(sql);//检查sql语句是否正确
					i=stmt.executeUpdate(sql);
					//此处之后语句，若stmt.executeUpdate(sql);抛出异常则执行不到
					stmt.close();
				} 
				catch (SQLException e1) 
				{
					error=e1.getMessage();
					e1.printStackTrace();	
				}
				
				Fmain.fdi.setText(sql,i,error);
				shuaxintable();//刷新当前更改的表，防止修改失败
			}
		}
	}
	
	class buttonProc implements ActionListener 
	{
		public void actionPerformed(ActionEvent e)
		{
//================================================================================================================增加功能
			if(e.getSource()==add)
			{	
				new Addbatch();
			}
//================================================================================================================删除功能
			if(e.getSource()==delete)
			{
				int hang[]=table.getSelectedRows();
				if(hang.length<=0)
					return;
				
				int n=JOptionPane.showConfirmDialog(null, "确认删除吗?", "删除", JOptionPane.YES_NO_OPTION);  
		        if (n!=JOptionPane.YES_OPTION)  
		            return; 
		        
				if(table.isEditing())
					table.getCellEditor().stopCellEditing();//如果要删除条目的表正在编辑状态，就停止编辑状态
				//批量删除
				for(int which=0;which<hang.length;which++)
				{
					String sql="delete from "+databasename+"."+tablename+"  where ";
					Object value=null;
					for(int j=0;j<tablecnum;j++)
					{
						if(value!=null)
						{
							sql=sql+" and ";
						}
						value=table.getValueAt(hang[which],j);
						if(kongzhi.equals(value))
							sql=sql+table.getColumnName(j)+" is null ";
						else
							sql=sql+table.getColumnName(j)+" = '"+table.getValueAt(hang[which],j)+"' ";
					}
					//System.out.println(sql);//检查sql语句是否正确
					int i=0;
					String error=null;
					try 
					{
						Statement stmt= conn.createStatement();
						i=stmt.executeUpdate(sql);
						stmt.close();
					}
					catch (SQLException e1) 
					{
						error=e1.getMessage();
						e1.printStackTrace();
					}
					Fmain.fdi.setText(sql,i,error);
				}
				shuaxintable();
			}
//================================================================================================================查找功能
			if(e.getSource()==select)
			{
				try
				{
					if(table.isEditing())
						table.getCellEditor().stopCellEditing();//如果要增加条目的表正在编辑状态，就停止编辑状态
					if(table2.isEditing())
						table2.getCellEditor().stopCellEditing();//如果要增加条目的表正在编辑状态，就停止编辑状态
					
					tablemodel.setRowCount(0);//先清空
					
					String sql="select * from "+databasename+"."+tablename;
					String value=null;
					for(int j=0;j<tablecnum;j++)
					{
						if(table2.getValueAt(0,j)!=null&&!table2.getValueAt(0,j).equals(""))//不对空值或空字符串进行查找
						{
							if(value==null)
								sql=sql+" where ";
							else
								sql=sql+" and ";
							value=(String)table2.getValueAt(0,j);
							if(kongzhi.equals(value))
								sql=sql+table.getColumnName(j)+" is null ";
							else
								sql=sql+table.getColumnName(j)+" = '"+value+"' ";
						}
					}
					sql=sql+" limit 0,100";
					//System.out.println(sql);//检查sql语句是否正确
					Statement stmt= conn.createStatement();
					ResultSet rs=stmt.executeQuery(sql);
					Object rowvalue[]=new Object[tablecnum];	
					int i=0;
					while(rs.next())
					{		
						for(int j=0;j<tablecnum;j++)
						{
							rowvalue[j]=rs.getString(tablecname[j]);
							if(rowvalue[j]==null)
								rowvalue[j]=kongzhi;
							
							oldvalue[i][j]=rowvalue[j];//存到缓冲表中
						}
						i++;
						tablemodel.addRow(rowvalue);
					}
					rs.close();
					stmt.close();
				}
				catch (SQLException e1)
				{
					e1.printStackTrace();
				}
			}
//================================================================================================================刷新功能
			if(e.getSource()==refresh)
			{
				if(table.isEditing())
					table.getCellEditor().stopCellEditing();//如果要刷新的表正在编辑状态，就停止编辑状态
				if(table2.isEditing())
					table2.getCellEditor().stopCellEditing();//如果要刷新的表正在编辑状态，就停止编辑状态	
				table.clearSelection();
				table2.clearSelection();
				shuaxintable();
			}
		}
	}
	//增加功能的弹窗
	class Addbatch extends JDialog
	{
		private static final long serialVersionUID = 1L;
		
		JLabel l=new JLabel();
		
		JScrollPane sp=new JScrollPane();
		Object data[][]=new Object[20][tablecnum];
		DefaultTableModel addmodel=new DefaultTableModel(data,tablecname);
		JTable add=new JTable(addmodel);
		
		JPanel p=new JPanel();
		JButton	b1=new JButton("全   选");
		JButton	b2=new JButton("增   加");
		JButton	b3=new JButton("扩展十行");
		JLabel	b4=new JLabel("<HTML><U>重置...</U></HTML>");
		
		Addbatch() 
		{
			super(Fmain.f,false);
			
			l.setText("选择要增加的行,可多选");
			sp.setViewportView(add);
			b1.addActionListener(new bProc());
			b2.addActionListener(new bProc());
			b3.addActionListener(new bProc());
			b4.addMouseListener(new MouseAdapter()
			{
				public void mouseClicked(MouseEvent arg0)
				{
					if(add.isEditing())
						add.getCellEditor().stopCellEditing();//如果要刷新的表正在编辑状态，就停止编辑状态	
					add.clearSelection();
					addmodel.setRowCount(0);
					addmodel.setRowCount(20);
				}
			});
			p.add(b2);
			p.add(b1);
			p.add(b3);
			p.add(b4);
			this.setLayout(new BorderLayout());
			this.add(l,BorderLayout.NORTH);
			this.add(sp);
			this.add(p,BorderLayout.SOUTH);
			
			//设置一些属性
			b1.setBackground(Color.WHITE);
			b2.setBackground(Color.WHITE);
			b3.setBackground(Color.WHITE);
			add.getTableHeader().setReorderingAllowed(false);//禁止拖动列头排序，不然会造成修改错误
			
			this.setSize(800,500);
			//将窗体设置在屏幕中间
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = this.getSize();
			int x = (screenSize.width - size.width) / 2;
			int y = (screenSize.height - size.height) / 2;
			this.setLocation( x+50, y+100);	
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
					add.selectAll();
				}
				if(e.getSource()==b2)
				{
					int hang[]=add.getSelectedRows();
					if(hang.length<=0)
						return;
					if(hang.length==1)
					{
						int n=JOptionPane.showConfirmDialog(null, "只选择了一行，继续执行吗？", "提示", JOptionPane.YES_NO_OPTION);  
						if (n!=JOptionPane.YES_OPTION)  
							return; 
					}
					if(table.isEditing())
					{
						table.getCellEditor().stopCellEditing();//如果要增加条目的表正在编辑状态，就停止编辑状态
					}
					if(add.isEditing())
					{
						add.getCellEditor().stopCellEditing();//如果要增加条目的表正在编辑状态，就停止编辑状态
					}
					//批量增加
					String sql="insert into "+databasename+"."+tablename+"  values(";
					int cishu1=0;//标记第几次
					for(int which=0;which<hang.length;which++)
					{
						if(cishu1!=0)//加逗号
							sql=sql+",(";
						Object value=null;
						int cishu2=0;//标记第几次
						for(int j=0;j<tablecnum;j++)
						{
							if(cishu2!=0)//加逗号
								sql=sql+" , ";
							value=add.getValueAt(hang[which],j);
							if(kongzhi.equals(value))//区别null空值
								sql=sql+"null";	
							else
							{
								if(value==null)//把输入框默认的null(空值)作为''(空白字符串)
									sql=sql+"''";
								else
									sql=sql+"'"+value+"'";		
							}
							cishu2++;
						}
						sql=sql+")";
						cishu1++;
						//System.out.println(sql);//检查sql语句是否正确
					}
					int i=0;
					String error=null;
					try
					{	
						Statement stmt= conn.createStatement();
						i=stmt.executeUpdate(sql);
						stmt.close();
					}
					catch (SQLException e1)
					{
						error=e1.getMessage();
						e1.printStackTrace();
					}
					Fmain.fdi.setText(sql,i,error);
					shuaxintable();
				}
				if(e.getSource()==b3)
				{
					int i=addmodel.getRowCount();
					addmodel.setRowCount(i+10);
				}
			}
		}
	}
}
