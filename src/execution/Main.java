package execution;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) 
	{
		
		EventQueue.invokeLater(() -> {
			UserInterfaceFrame frame = new UserInterfaceFrame();
			/*
			List<String> stringlist = new ArrayList<String>();
			stringlist.add("a");
			stringlist.add("b");
			stringlist.add("c");
			stringlist.add("d");
			stringlist.add("e");
			
			List<List<String>> perm = frame.generatePerm(stringlist);
			for(List<String> item : perm)
			{
				System.out.println(item);
			}
			*/
			frame.setVisible(true);
		});
		
		
	}
}
