package tablut.client;

import tablut.board.Board;
import tablut.game.MoveFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GameClient {

    private Socket socket;
    private BufferedReader receive;
    private PrintWriter write;
    private Board board;
    //private MoveFactory moveFactory;
    //private Random random;
    private int playerColor;

    public GameClient(String host, int port) throws IOException {
        board = new Board();
        socket = new Socket(host, port);
        write = new PrintWriter(socket.getOutputStream(), true);
        receive = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        //moveFactory = new MoveFactory();
        //rng = new Random();
    }


    public void runConnection() throws IOException{}


    public void runGame() throws IOException{}


    public void sendMessage(String msg){
        write.println(msg);
        System.out.println("Send: "+msg);

    }

    public void receiveMessage() throws IOException{
        String message = receive.readLine();
        System.out.println("Receive: " + message);


    }

}
