import javax.swing.JFrame;

import Elevator.Elevator;
import Elevator.ElevatorConst;
import Require.RequireButton;

class View extends JFrame
{
	View()
	{
		RequireButton require=new RequireButton();
		require.setBounds(1250,0, 
				ElevatorConst.elevatorButtonWide*2,
				(ElevatorConst.floorHigh+ElevatorConst.floorSpace)*ElevatorConst.totalFloor);
		add(require);
		
		ElevatorConst.elevator=new Elevator[5];
		for (int i=0; i<5; i++)
		{
			ElevatorConst.elevator[i]=new Elevator();
			ElevatorConst.elevator[i].add(this,i);
		}
		
		setSize(1500,750);
		this.setVisible(true);
		this.setResizable(false);
		for (int i=0; i<5; i++)
		{
			ElevatorConst.elevator[i].start();
		}
	}
}

public class Main {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) 
	{
		View frame=new View();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
