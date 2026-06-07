import javax.swing.JFrame;
import java.awt.Color ;
import java.awt.Container ;

public class Game {

    Container con;

    JFrame window;
    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        window = new JFrame();
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.getContentPane().setBackground(Color.BLACK);
        // in order to havae a customised layout
        window.setLayout(null);
        window.setVisible(true);
        con = window.getContentPane();

    }
}