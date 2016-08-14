import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

class Fdisplay extends JScrollPane
{
	private static final long serialVersionUID = 1L;
	
	JTextPane textpane=new JTextPane();
	SimpleAttributeSet attrset = new SimpleAttributeSet();
	
	Fdisplay()
	{
		textpane.setEditable(false);
		textpane.setFont(new Font("微软雅黑",0,13));
		this.setViewportView(textpane);
	}
	
	void setText(String sql,String error)
	{
		if(sql==null)
			return;
		
		Document docs = textpane.getDocument();//获得文本对象
	    try 
	    {
	    	 docs.insertString(docs.getLength(),sql+"\r\n",null);//对文本进行追加
	    	 if(error==null)
	    	 {
		    	 docs.insertString(docs.getLength(),"执行成功！\r\n",null);
	    	 }
	    	 else
	    	 {
	    		 StyleConstants.setForeground(attrset, Color.red);
		    	 docs.insertString(docs.getLength(),"错误："+error+"\r\n",attrset);
	    	 } 
		} catch (BadLocationException e) {e.printStackTrace();}
	}
	void setText(String sql,int i,String error)
	{
		if(sql==null)
			return;
		
		Document docs = textpane.getDocument();//获得文本对象
	    try 
	    {
	    	docs.insertString(docs.getLength(),sql+"\r\n",null);//对文本进行追加
	    	docs.insertString(docs.getLength(),"受影响行数："+i+"\r\n",null);
	    	if(error==null)
	    	{
		    	docs.insertString(docs.getLength(),"执行成功！\r\n",null);
	    	}
	    	else
	    	{
	    		StyleConstants.setForeground(attrset, Color.red);
		    	docs.insertString(docs.getLength(),"错误："+error+"\r\n",attrset);
	    	} 
		} catch (BadLocationException e) {e.printStackTrace();}
	}
	void setText(String sql[],int error)
	{
		if(sql==null)
			return;
		
		Document docs = textpane.getDocument();//获得文本对象
	    try 
	    {
	    	if(error==-1)
			{
	    		StyleConstants.setForeground(attrset, Color.PINK);
				docs.insertString(docs.getLength(),"事务失败，已回滚，回滚成功\r\n",attrset);//对文本进行追加
				return;
			}
	    	if(error==-2)
			{
	    		StyleConstants.setForeground(attrset, Color.red);
				docs.insertString(docs.getLength(),"事务失败，已回滚，回滚失败\r\n",attrset);//对文本进行追加
				return;
			}
	    	for(int i=0;i<sql.length;i++)
	    	{
	    		docs.insertString(docs.getLength(),sql[i]+"\r\n",null);//对文本进行追加
	    	}
	    	docs.insertString(docs.getLength(),"全部执行成功！\r\n",null);//对文本进行追加
		} catch (BadLocationException e) {e.printStackTrace();}
	}
}
