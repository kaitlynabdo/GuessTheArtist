/**
 * MTClient.java
 *
 * This program implements a simple multithreaded chat client.  It connects to the
 * server (assumed to be localhost on port 7654) and starts two threads:
 * one for listening for data sent from the server, and another that waits
 * for the user to type something in that will be sent to the server.
 * Anything sent to the server is broadcast to all clients.
 *
 * The MTClient uses a ClientListener whose code is in a separate file.
 * The ClientListener runs in a separate thread, recieves messages form the server,
 * and displays them on the screen.
 *
 * Data received is sent to the output screen, so it is possible that as
 * a user is typing in information a message from the server will be
 * inserted.
 *
 */

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.Socket;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;

public class MtClient {
  /**
   * main method.
   * @params not used.
   */
  
  private static ArrayList<Player> playerList = new ArrayList<Player>();

  public static void main(String[] args) {
    try {
      String hostname = "localhost";
      int port = 7654;

      System.out.println("Connecting to server on port " + port);
      Socket connectionSock = new Socket(hostname, port);

      DataOutputStream serverOutput = new DataOutputStream(connectionSock.getOutputStream());

      System.out.println("Connection made.");

      // Start a thread to listen and display data sent by the server
      ClientListener listener = new ClientListener(connectionSock);
      Thread theThread = new Thread(listener);
      theThread.start();

      // Read input from the keyboard and send it to everyone else.
      // The only way to quit is to hit control-c, but a quit command
      // could easily be added.

      System.out.print("Enter a username: ");
      Scanner keyboard = new Scanner(System.in);
      String username = keyboard.nextLine();
      serverOutput.writeBytes(username + "\n");
      // check if username is host
      if (username.equalsIgnoreCase("host")) {
        System.out.print("Enter a question for clients: ");
        String question = keyboard.nextLine();
        serverOutput.writeBytes(question + "\n");
        System.out.print("Enter the correct answer: ");
        String hostAnswer = keyboard.nextLine();
      }

      while (true) {
        //commands for the host
        
        if (username.equalsIgnoreCase("host")) {
          System.out.println("Type 'add' to add a client's username to the list");
          System.out.println("Type 'points' to award points to a specific client");
          System.out.println("Type 'leaderboard' to display all clients and their scores");
          String command = keyboard.nextLine();
          if (command.equals("add")) { //add player
            System.out.print("Enter client's username: ");
            String u = keyboard.nextLine();
            Player p = new Player(u);
            playerList.add(p);
            System.out.println("Press 'Enter' key to continue"); 
          } else if (command.equalsIgnoreCase("points")) { //award points to certain client by username
            System.out.print("Enter client's username: ");
            String u = keyboard.nextLine();
            System.out.print("Enter number of points to award to client: ");
            int pts = keyboard.nextInt();
            for (Player p : playerList) {
              if (p.username.equals(u)) {
                p.addPoints(pts);
              }
            }
          } else if (command.equalsIgnoreCase("leaderboard")) { //print leaderboard
            serverOutput.writeBytes("LEADERBOARD" + "\n");
            // display leaderboard in decreasing order
            Collections.sort(playerList, Collections.reverseOrder()); 
            for (Player p : playerList) {
              serverOutput.writeBytes(p.toString() + "\n");
            }
          }
        }
        
        String data = keyboard.nextLine();
        serverOutput.writeBytes(data + "\n");
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
} // MtClient
