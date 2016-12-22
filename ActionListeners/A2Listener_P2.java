import java.awt.event.*;

public class A2Listener_P2 implements ActionListener
{

	testP2FieldEdit d;

	public A2Listener_P2(testP2FieldEdit d)
	{
		this.d = d;
	}
	
	public void actionPerformed(ActionEvent e)
	{
		try
		{
			d.setDCasdf ("Doug");
            d.setDClag ("a2b2");
      
			d.appendToStatusArea("button pressed.");
		}
		catch(Exception rt)
		{
			d.appendToStatusArea("ERROR");
		}

	}

}