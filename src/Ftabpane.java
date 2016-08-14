import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicButtonUI;

class Ftabpane extends JTabbedPane
{
	private static final long serialVersionUID = 1L;
	JScrollPane sp=new JScrollPane();
	JTextArea ta=new JTextArea();
	Ftabpane()
	{
		ta.setFont(new Font("微软雅黑",0,13));
		ta.setForeground(new Color(57,105,138));
		sp.setViewportView(ta);
		this.add(sp,"Welcome");
		this.setFont(new Font("微软雅黑",0,10));
		this.setForeground(new Color(100,200,250));
	}
	@Override
	public void add(Component component, Object constraints)
	{
		super.add(component, constraints);
		setTabComponentAt(indexOfTab((String) constraints),new ButtonTabComponent(this));
	}
}
class ButtonTabComponent extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private final JTabbedPane pane;
    public ButtonTabComponent(final JTabbedPane pane) 
    {
        //unset default FlowLayout' gaps
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (pane == null)
        {
            throw new NullPointerException("TabbedPane is null");
        }
        this.pane = pane;
        setOpaque(false);
        //make JLabel read titles from JTabbedPane
        JLabel label = new JLabel() 
        {
			private static final long serialVersionUID = 1L;
			public String getText() 
            {
                int i = pane.indexOfTabComponent(ButtonTabComponent.this);
                if (i != -1)
                {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        //add more space between the label and the button
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        //tab button
        JButton button = new TabButton();
        add(button);
        //add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
 
    private class TabButton extends JButton implements ActionListener 
    {
		private static final long serialVersionUID = 1L;
		public TabButton() 
        {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("close this tab");
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(BorderFactory.createEtchedBorder());
            setBorderPainted(false);
            setRolloverEnabled(true);
            addActionListener(this);
        }
        public void actionPerformed(ActionEvent e) 
        {
            int i = pane.indexOfTabComponent(ButtonTabComponent.this);
            if (i != -1)
            {
                pane.remove(i);
            }
        }
        //we don't want to update UI for this button
        public void updateUI() 
        {
        }
        //paint the cross
        protected void paintComponent(Graphics g) 
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            //shift the image for pressed buttons
            if (getModel().isPressed())
            {
                g2.translate(1,1);//将 Graphics2D 上下文的原点平移到当前坐标系统中的点 (x, y)。
            }
            g2.setStroke(new BasicStroke(3));
            g2.setColor(new Color(255,255,255,100));
            if (getModel().isRollover()) 
            {
                g2.setColor(new Color(252,160,160));
            }
            int delta=5;
            g2.drawLine(delta, delta, getWidth() - delta, getHeight() - delta);
            g2.drawLine(getWidth() - delta, delta, delta, getHeight() - delta);
            g2.dispose();
        }
    }
}
