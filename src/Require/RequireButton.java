package Require;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JPanel;

import Elevator.Elevator;
import Elevator.ElevatorConst;

public class RequireButton extends JPanel 
{
	public static LinkedList<Integer> requireList=new LinkedList<Integer>();
	public static JButton []requireButton=new JButton[ElevatorConst.totalFloor*2];
	public static boolean []require=new boolean[ElevatorConst.totalFloor*2];
	public RequireButton()
	{
		for (int i=0; i<ElevatorConst.totalFloor*2; i++)
			require[i]=false;
		
		setLayout(null);
		for (int i=0; i<ElevatorConst.totalFloor; i++)
		{
			requireButton[i]=new JButton((i+1)+"¡÷");
		}
		for (int i=ElevatorConst.totalFloor; i<2*ElevatorConst.totalFloor; i++)
		{
			requireButton[i]=new JButton(2*ElevatorConst.totalFloor-i+"¨Œ");
		}
		
		for (int i=0; i<ElevatorConst.totalFloor*2-1; i++)
		{
			if (i==ElevatorConst.totalFloor-1) continue;
			int j;
			if (i<ElevatorConst.totalFloor-1) j=ElevatorConst.totalFloor-i-1;
			else j=i-20;
			
			requireButton[i].setMargin(new Insets(1,1,1,1));
			requireButton[i].setFont(new Font(requireButton[i].getFont().getFontName(),requireButton[i].getFont().getStyle(),15));
			requireButton[i].setBounds(i/ElevatorConst.totalFloor*ElevatorConst.elevatorButtonWide, 
					j*(ElevatorConst.floorHigh+ElevatorConst.floorSpace)+ElevatorConst.floorSpace
					, ElevatorConst.floorWide,ElevatorConst.floorHigh);
			
			requireButton[i].setBackground(Color.white);
			requireButton[i].setForeground(Color.black);
			requireButton[i].addActionListener(requireButtonListener);
			add(requireButton[i]);
		}
	}
	ActionListener requireButtonListener=new ActionListener()
	{
		public void actionPerformed(ActionEvent	e)
		{
			JButton pressButton=(JButton) e.getSource();
			int which = 0;
			for (int i=0; i<ElevatorConst.totalFloor*2; i++)
			{
				if (pressButton==requireButton[i])
				{
					which=i;
					break;
				}
			}
			if (pressButton.getBackground()==Color.red) return ;
			pressButton.setBackground(Color.red);
			require[which]=true;
			requireList.add(new Integer(which));
		}
	};
	public static boolean isShortest(Elevator ele, int floor)
	{
		for (int i=0; i<5; i++)
		{
			if (ElevatorConst.elevator[i].getElevatorState()==0 && Math.abs(ele.getFloor()-floor)>Math.abs(ElevatorConst.elevator[i].getFloor()-floor)) return false;
		}
		return true;
	}
	
	public static int getRequire()
	{
		while (true)
		{
			if (requireList.isEmpty()) return -1;
			int floor=requireList.getFirst();
			if (require[floor]) return floor;
			else requireList.removeFirst();
		}
	}
}
