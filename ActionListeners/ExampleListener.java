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
			d.getDCname();
			d.appendToStatusArea("button pressed.");
		}
		catch(Exception tr)
		{
			d.appendToStatusArea(tr.getMessage());
		}

	}

}
