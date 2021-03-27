package game;

import city.cs.engine.*;
import city.cs.engine.Shape;
import com.sun.source.doctree.HiddenTree;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.Control;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Platform extends StaticBody{ /*This creates a new platform with a random height and width*/

    Platform(World W){
        super(W);
        int Height;
        int Width;
        int X_coord;
        int Y_coord;
        Random random = new Random();
        Width = random.nextInt(4) +3;
        Height = random.nextInt(2)+1;
        int Nve = random.nextInt(2);
        if (Nve == 1){
            X_coord = -1 * random.nextInt(25);
        }
        else{
            X_coord = random.nextInt(25);
        }
        int Nve2 = random.nextInt(2);
        if (Nve2 == 1){
            Y_coord = -1 * random.nextInt(18);
        }
        else{
            Y_coord = random.nextInt(18);
        }
        BoxShape b = new BoxShape(Width, Height);
        SolidFixture SF = new SolidFixture(this,b);
        this.setPosition(new Vec2(X_coord, Y_coord));
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class New_Game{

    private static int Level = 1;
    private World Stage;

    private My_Stage Stage_View;

    private static int Player_1_Wins = 0;
    private static int Player_2_Wins = 0;

    private Control_Panel Panel;

    static Music_Controller MC;

    public New_Game() throws IOException {

        MC = new Music_Controller();

        if (Level == 1){
            Stage = new Areana();
        }
        else if (Level == 2){
            Stage = new Areana2();
        }
        else if (Level == 3){
            Stage = new Areana3();
        }
        else if (Level == 4){
            Stage = new WinnerScreen();
        }

        Stage_View = new My_Stage(Stage, 1000, 750);

        if (Level == 1){
            Player_1_Controller P1Cont = new Player_1_Controller(Areana.GetPlayer1(),Stage);
            Stage_View.addKeyListener(P1Cont);
        }
        else if (Level == 2){
            Player_1_Controller P1Cont = new Player_1_Controller(Areana2.GetPlayer1(),Stage);
            Stage_View.addKeyListener(P1Cont);
        }
        else if (Level == 3){
            Player_1_Controller P1Cont = new Player_1_Controller(Areana3.GetPlayer1(),Stage);
            Stage_View.addKeyListener(P1Cont);
        }

        if (Level == 1){
            Player_2_Controller P2Cont = new Player_2_Controller(Areana.GetPlayer2(),Stage);
            Stage_View.addKeyListener(P2Cont);
        }
        else if (Level == 2){
            Player_2_Controller P2Cont = new Player_2_Controller(Areana2.GetPlayer2(),Stage);
            Stage_View.addKeyListener(P2Cont);
        }
        else if (Level == 3){
            Player_2_Controller P2Cont = new Player_2_Controller(Areana3.GetPlayer2(),Stage);
            Stage_View.addKeyListener(P2Cont);
        }

        Stage_View.addMouseListener(new game.GiveFocus(Stage_View));

        final JFrame Frame = new JFrame("Dual");
        Frame.add(Stage_View);

        Panel = new Control_Panel(Stage, MC);
        Frame.add(Panel.Get_Panel(),BorderLayout.NORTH);

        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Frame.setLocationByPlatform(true);

        Frame.setResizable(false);

        Frame.pack();

        Frame.setVisible(true);
    }

    public static void main(String[] args) throws IOException {
        new New_Game();
    }

    public static int Get_Level(){
        return Level;
    }

    public static void Increase_Level() throws IOException {
        Level++;
        MC.Stop_Music();
        new New_Game();
    }

    public static void Increase_Player_1_Score(){
        Player_1_Wins++;
    }

    public static void Increase_Player_2_Score(){
        Player_2_Wins++;
    }

    public static int Get_Player_1_Score(){
        return Player_1_Wins;
    }

    public static int Get_Player_2_Score(){
        return Player_2_Wins;
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_1 extends Walker{
    private static int health;
    private static final Shape Player1Shape = new PolygonShape(
            -0.11f,1.8f, // Will change after testing
            0.87f,1.48f,
            0.99f,0.29f,
            0.24f,-2.32f,
            -1.12f,-2.27f,
            -1.24f,1.21f);
    private static final BodyImage image = new BodyImage("data/Robot_Sprite_Player_1_rightPNG.png", 5f);

    public Player_1(World Stage) {
            super(Stage, Player1Shape);
            addImage(image);
            if (New_Game.Get_Level() == 3){
                health = 5;
            }
            else{
                health = 3;
            }
    }

    public static void Player1_Hit() throws IOException {
        health--;
        System.out.println("Player 1 has been hit");
        if (health == 0){
            New_Game.Increase_Player_2_Score();
            New_Game.Increase_Level();
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_2 extends Walker{
    private static int health;
    private static final Shape Player_2_Shape = new PolygonShape(
            -0.11f,1.8f, // Will change after testing
            0.87f,1.48f,
            0.99f,0.29f,
            0.24f,-2.32f,
            -1.12f,-2.27f,
            -1.24f,1.21f);
    private static final BodyImage image =
            new BodyImage("data/Robot_Sprite_Player_2_rightPNG.jpg", 5f);

    public Player_2(World Stage) {
        super(Stage, Player_2_Shape);
        addImage(image);
        health = 3;
    }

    public static void Player2_Hit() throws IOException {
        health--;
        if (health == 0){
            New_Game.Increase_Player_1_Score();
            New_Game.Increase_Level();
        }
    }
/*
    public int Get_Player2_Health(){
        return health;
    }*/
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Areana extends World{

    private static Player_1 Player1;
    private static Player_2 Player2;
    //private Player_1_Controller P1Cont;

    private Timer timer;

    public Areana(){
        Random random = new Random();
        int Amount_Of_Platforms = 0;
        while (Amount_Of_Platforms < 6){
            Amount_Of_Platforms = random.nextInt(11);
        }
        /*Make random number between 5 and 10 this will be the amount of platforms*/
        Platform[] Platforms = new Platform[Amount_Of_Platforms];
        for(int i = 0; i < Amount_Of_Platforms; i++){ /*Makes random platforms and appends them to an array*/
            Platform tempplatform = new Platform(this);
            Platforms[i] = tempplatform;
        }

        Shape shape = new BoxShape(5, 1);
        StaticBody ground = new StaticBody(this, shape);
        ground.setPosition(new Vec2(-16, -8));

        Shape shape2 = new BoxShape(5, 1);
        StaticBody ground2 = new StaticBody(this, shape2);
        ground2.setPosition(new Vec2(16, -8));

        Player1 = new Player_1(this);
        Player1.setPosition(new Vec2( -16,-4.5f));
        Player_1_Hit Player1_Hit = new Player_1_Hit(Player1);
        Player1.addCollisionListener(Player1_Hit);

        Player2 = new Player_2(this);
        Player2.setPosition(new Vec2(16, -4.5f));
        Player_2_Hit Player2_Hit = new Player_2_Hit(Player2);
        Player2.addCollisionListener(Player2_Hit);

        Death_Zone DZ = new Death_Zone(this);
        DZ.setPosition(new Vec2(0,-20));
    }

    static Player_1 GetPlayer1(){
        return Player1;
    }

    static Player_2 GetPlayer2(){
        return Player2;
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class My_Stage extends UserView{
    private Image Background;

    FileReader FR;
    FileWriter FW;

    public My_Stage(World W, int width, int height) throws IOException {
        super(W, width, height);

        if(New_Game.Get_Level() == 1){
            Background = new ImageIcon("data/Steampunk Background.jpg").getImage();
        }
        else if(New_Game.Get_Level() == 2){
            Background = new ImageIcon("data/Steampunk Background2.jpg").getImage();
        }
        else if (New_Game.Get_Level() == 3){
            Background = new ImageIcon("data/Steampunk Background3.jpg").getImage();
        }
        else if (New_Game.Get_Level() == 4 && New_Game.Get_Player_1_Score() >= 2){
            Player_1_Record_Win();
            Background = new ImageIcon("data/Player 1 Wins Screen.jpg").getImage();
            System.out.println("Player 1 wins");
        }
        else if (New_Game.Get_Level() == 4 && New_Game.Get_Player_2_Score() >= 2){
            Player_2_Record_Win();
            Background = new ImageIcon("data/Player 2 Wins Screen.jpg").getImage();
            System.out.println("Player 2 wins");
        }
    }

    protected void paintBackground(Graphics2D g) {
        g.drawImage(Background, 0 , 0, this);
    }

    protected void Player_1_Record_Win() throws IOException {
        try{
            FR = new FileReader("data/Score_keeper.txt");
            BufferedReader Reader = new BufferedReader(FR);
            String Line1 = Reader.readLine();
            String Line2 = Reader.readLine();
            int Score1 = Line1.charAt(0);
            FW = new FileWriter("data/Score_keeper.txt");
            try{
                int Score2 = Line1.charAt(1);
                if (Score2 == 9){
                    Score2 = 0;
                    Score1++;
                }
                else{
                    Score2++;
                }
                FW.write(Score1 + Score2 + "\n" + Line2);
            }
            catch (StringIndexOutOfBoundsException e) {
                Score1++;
                FW.write(Score1 + "\n" + Line2);
            }
        }
        catch (java.io.IOException e) {
            System.out.println("Whoops something went wrong");
        }
        finally {
            if (FW != null){
                FW.close();
            }
            FR.close();
        }

    }

    protected void Player_2_Record_Win() throws IOException {
        try{
            FR = new FileReader("data/Score_keeper.txt");
            BufferedReader Reader = new BufferedReader(FR);
            String Line1 = Reader.readLine();
            String Line2 = Reader.readLine();
            int Score1 = Line2.charAt(0);
            FW = new FileWriter("data/Score_keeper.txt");
            try{
                int Score2 = Line2.charAt(1);
                if (Score2 == 9){
                    Score2 = 0;
                    Score1++;
                }
                else{
                    Score2++;
                }
                FW.write(Score1 + Score2 + "\n" + Line1);
            }
            catch (StringIndexOutOfBoundsException e) {
                Score1++;
                FW.write(Score1 + "\n" + Line1);
            }
        }
        catch (java.io.IOException e) {
            System.out.println("Whoops something went wrong");
        }
        finally {
            if (FW != null){
                FW.close();
            }
            FR.close();
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_1_Controller implements KeyListener{

    private static final float speed = 4;

    private final Player_1 Player1;
    private final World Stage1;

    Player_1_Controller(Player_1 P1, World Stage){
        Player1 = P1;
        Stage1 = Stage;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        int code = e.getKeyCode();
        // other key commands omitted
        if (code == KeyEvent.VK_A) {
            Player1.startWalking(-speed);
        }
        else if (code == KeyEvent.VK_D) {
            Player1.startWalking(speed);
        }
        else if (code == KeyEvent.VK_W){
            if (New_Game.Get_Level() == 2){
                Player1.jump(-8);
            }
            else{
                Player1.jump(8);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_D) {
            if (New_Game.Get_Level() == 1) {
                Areana.GetPlayer1().stopWalking();
            }
            else if (New_Game.Get_Level() == 2){
                Areana2.GetPlayer1().stopWalking();
            }
            else if (New_Game.Get_Level() == 3){
                Areana3.GetPlayer1().stopWalking();
            }
        }
        else if (code == KeyEvent.VK_SPACE){
            Player_1_Bullet Bullet1 = new Player_1_Bullet(Stage1, Player1);
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_2_Controller implements KeyListener{
    private static final float speed = 4;

    private final Player_2 Player2;
    private final World Stage1;

    Player_2_Controller(Player_2 P2, World Stage){
        Player2 = P2;
        Stage1 = Stage;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        // other key commands omitted
        if (code == KeyEvent.VK_L) {
            Player2.startWalking(speed);
        }
        else if (code == KeyEvent.VK_J) {
            Player2.startWalking(-speed);
        }
        else if (code == KeyEvent.VK_I){
            if (New_Game.Get_Level() == 2){
                Player2.jump(-8);
            }
            else{
                Player2.jump(8);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_L || code == KeyEvent.VK_J) {
            if (New_Game.Get_Level() == 1){
                Areana.GetPlayer2().stopWalking();
            }
            else if(New_Game.Get_Level() == 2){
                Areana2.GetPlayer2().stopWalking();
            }
            else if (New_Game.Get_Level() == 3){
                Areana3.GetPlayer2().stopWalking();
            }
        }

        else if (code == KeyEvent.VK_CONTROL){
            Player_2_Bullet Bullet2 = new Player_2_Bullet(Stage1, Player2);
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class GiveFocus implements MouseListener{

    private UserView view;

    public GiveFocus(UserView v){
        this.view = v;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        view.requestFocus();
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_1_Bullet extends DynamicBody{
    private static final Shape Bullet_Shape = new CircleShape(0.1f);

    @Override
    public void setGravityScale(float g) {
        super.setGravityScale(0);
    }

    public Player_1_Bullet(World Stage, Player_1 Player1){
        super(Stage,Bullet_Shape);
        this.setFillColor(Color.RED);
        if (New_Game.Get_Level() == 2){
            this.setLinearVelocity(new Vec2(12,-10));
        }
        else{
            this.setLinearVelocity(new Vec2(12,10));
        }
        this.setPosition(new Vec2(Player1.getPosition()));
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_2_Bullet extends DynamicBody{
    private static final Shape Bullet_Shape = new CircleShape(0.1f);

    @Override
    public void setGravityScale(float g) {
        super.setGravityScale(0);
    }

    public Player_2_Bullet(World Arena, Player_2 Player2){
        super(Arena,Bullet_Shape);
        this.setFillColor(Color.RED);
        if (New_Game.Get_Level() == 2){
            this.setLinearVelocity(new Vec2(-12,-10));
        }
        else{
            this.setLinearVelocity(new Vec2(-12,10));
        }
        this.setPosition(new Vec2(Player2.getPosition().x -1, Player2.getPosition().y));
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Areana2 extends World{

    private static Player_1 Player1;
    private static Player_2 Player2;

    Areana2(){
        this.setGravity(-getGravity());
        Random random = new Random();
        int Amount_Of_Platforms = 0;
        while (Amount_Of_Platforms < 6){
            Amount_Of_Platforms = random.nextInt(11);
        }

        /*Make random number between 5 and 10 this will be the amount of platforms*/;
        Platform[] Platforms = new Platform[Amount_Of_Platforms];
        for(int i = 0; i < Amount_Of_Platforms; i++){ /*Makes random platforms and appends them to an array*/
            Platform tempplatform = new Platform(this);
            Platforms[i] = tempplatform;
        }

        Shape shape = new BoxShape(5, 1);
        StaticBody ground = new StaticBody(this, shape);
        ground.setPosition(new Vec2(-16, 8));

        Shape shape2 = new BoxShape(5, 1);
        StaticBody ground2 = new StaticBody(this, shape2);
        ground2.setPosition(new Vec2(16, 8));

        Player1 = new Player_1(this);
        Player1.setPosition(new Vec2( -16,4.5f));
        Player_1_Hit Player1_Hit = new Player_1_Hit(Player1);
        Player1.addCollisionListener(Player1_Hit);

        Player2 = new Player_2(this);
        Player2.setPosition(new Vec2(16, 4.5f));
        Player_2_Hit Player2_Hit = new Player_2_Hit(Player2);
        Player2.addCollisionListener(Player2_Hit);

        Death_Zone DZ = new Death_Zone(this);
        DZ.setPosition(new Vec2(0,20));
    }

    public static Player_1 GetPlayer1() {
        return Player1;
    }

    public static Player_2 GetPlayer2() {
        return Player2;
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_1_Hit implements CollisionListener{

    private Player_1 P1;

    public Player_1_Hit(Player_1 Player1){
        this.P1 = Player1;
    }

    @Override
    public void collide(CollisionEvent e) {
        if(e.getOtherBody() instanceof Player_2_Bullet){
            e.getOtherBody().destroy();
            try {
                P1.Player1_Hit();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
        if (e.getOtherBody() instanceof Death_Zone){
            try {
                P1.Player1_Hit();
                P1.Player1_Hit();
                P1.Player1_Hit();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Player_2_Hit implements CollisionListener{

    private Player_2 P2;

    public Player_2_Hit(Player_2 Player2){
        this.P2 = Player2;
    }

    @Override
    public void collide(CollisionEvent e) {
        if(e.getOtherBody() instanceof Player_1_Bullet){
            e.getOtherBody().destroy();
            try {
                P2.Player2_Hit();
            } catch (IOException fileNotFoundException) {
                System.out.println("Not working");
            }

        }
        if (e.getOtherBody() instanceof Death_Zone){
            try {
                P2.Player2_Hit();
                P2.Player2_Hit();
                P2.Player2_Hit();
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Areana3 extends World {

    private static Player_1 Player1;
    private static Player_2 Player2;

    Areana3() {
        Random random = new Random();
        int Amount_Of_Platforms = 0;
        while (Amount_Of_Platforms < 6){
            Amount_Of_Platforms = random.nextInt(11);
        }
        Platform[] Platforms = new Platform[Amount_Of_Platforms];
        for(int i = 0; i < Amount_Of_Platforms; i++){ /*Makes random platforms and appends them to an array*/
            Platform tempplatform = new Platform(this);
            Platforms[i] = tempplatform;
        }

        Shape shape = new BoxShape(5, 1);
        StaticBody ground = new StaticBody(this, shape);
        ground.setPosition(new Vec2(-16, -8));

        Shape shape2 = new BoxShape(5, 1);
        StaticBody ground2 = new StaticBody(this, shape2);
        ground2.setPosition(new Vec2(16, -8));

        Player1 = new Player_1(this);
        Player1.setPosition(new Vec2( -16,-4.5f));
        Player_1_Hit Player1_Hit = new Player_1_Hit(Player1);
        Player1.addCollisionListener(Player1_Hit);

        Player2 = new Player_2(this);
        Player2.setPosition(new Vec2(16, -4.5f));
        Player_2_Hit Player2_Hit = new Player_2_Hit(Player2);
        Player2.addCollisionListener(Player2_Hit);

        Death_Zone DZ = new Death_Zone(this);
        DZ.setPosition(new Vec2(0,-20));
    }

    public static Player_1 GetPlayer1() {
        return Player1;
    }

    public static Player_2 GetPlayer2() {
        return Player2;
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class WinnerScreen extends World{}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Time_Listener implements ActionListener{

    World Stage;
    int Amount_Of_Timers = 0;

    Time_Listener(World Stage){
        this.Stage = Stage;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Amount_Of_Timers++;
        if (Amount_Of_Timers == 1){
            System.out.println("3");
        }
        else if (Amount_Of_Timers == 2){
            System.out.println("2");
        }
        else if (Amount_Of_Timers == 3){
            System.out.println("1");
        }
        else{
            Stage.start();
            Control_Panel.Stop_Timer();
            System.out.println("GO");
        }
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Music_Controller{
    SoundClip Game_Music;

    public void Start_Music(){
        try {
            Game_Music = new SoundClip("data/Excomunicado John Wick Soundtrack.wav");   // Open an audio input stream
            Game_Music.play();
            Game_Music.loop();
        } catch (UnsupportedAudioFileException|IOException|LineUnavailableException f) {
            System.out.println("Sound not found");
        }
    }

    public void Stop_Music(){
        Game_Music.pause();
    }
}

//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------
//---------------------------------------------------------------------------------------------------------------

class Death_Zone extends StaticBody{

    public Death_Zone(World w) {
        super(w);
        BoxShape Shape = new BoxShape(25, 1);
        SolidFixture SF = new SolidFixture(this,Shape);
        this.setFillColor(Color.RED);
    }
}