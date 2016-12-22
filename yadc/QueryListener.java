import java.awt.event.*;

public class QueryListener implements ActionListener
{

	testFieldEdit d;

	public QueryListener(testFieldEdit d)
	{
		this.d = d;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			d.appendToStatusArea("button pressed.");
		}
		catch(Exception tr)
		{
			d.appendToStatusArea("ERROR");
		}

	}

}
