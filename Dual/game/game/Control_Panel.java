package game;

import city.cs.engine.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Control_Panel{
    private JPanel panel1;
    private JButton quitButton;
    private JButton startButton;
    private JButton stopButton;

    private static Timer timer;
    private World Stage;

    public Control_Panel(World Stage, Music_Controller MC){
        this.Stage = Stage;
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MC.Stop_Music();
                Stage.stop();
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timer = new Timer(1000, new Time_Listener(Stage));
                timer.setInitialDelay(100);
                MC.Start_Music();
                timer.start();
            }
        });
    }

    public static void Stop_Timer(){
        timer.stop();
    }

    public JPanel Get_Panel(){
        return panel1;
    }
}
