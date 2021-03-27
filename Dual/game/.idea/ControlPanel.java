/*package game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class Control_Panel{
    private JPanel Main_Panel1;
    private JButton Stop_Button;
    private JButton Start_Button;
    private JButton Quit_Button;
    private Areana Stage;

    public Control_Panel(Areana Stage){
        this.Stage = Stage;
        Quit_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        Start_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //add timer for it to count down to start
                Stage.start();
            }
        });
        Stop_Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Stage.stop();
            }
        });
    }

    public JPanel Get_Panel(){
        return Main_Panel1;
    }

}*/
