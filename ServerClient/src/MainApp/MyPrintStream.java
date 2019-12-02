
package MainApp;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MyPrintStream{

    private JTextArea area;
    
    public MyPrintStream( Object area ) {
        this.area = (JTextArea)area;
    }
    
    public void println(String s){
        
        SwingUtilities.invokeLater(new Runnable() 
        {
            @Override
            public void run()
            {
                area.append(s + "\n");
            }
        });
    }
    
}
