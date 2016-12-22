import java.awt.event.*;

public class ExampleListener implements ActionListener
{

	ExampleFieldEdit d;

	public ExampleListener(ExampleFieldEdit d)
	{
		this.d = d;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{	
			/*d.setDCName();
			d.setDCStudent_ID();
			d.setDCA1();
			d.setDCA2();
			d.setDCA3();
			d.setDCA4();
			d.setDCAverage();*/

			/*d.getDCName();
			d.getDCStudent_ID();
			d.getDCA1();
			d.getDCA2();
			d.getDCA3();
			d.getDCA4();
			d.getDCAverage();*/
			
			d.appendToStatusArea("button pressed.");
		}
		catch(Exception tr)
		{
			d.appendToStatusArea(tr.getMessage());
		}

	}

}
