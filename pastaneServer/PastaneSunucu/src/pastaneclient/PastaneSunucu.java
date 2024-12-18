/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pastaneclient;


import java.io.*;
import java.net.*;
import java.util.*;

public class PastaneSunucu {
    private static final int PORT = 5000;

    // Pasta listesi
    private static List<Pasta> pastalar = new ArrayList<>();

    public static void main(String[] args) {
        // Başlangıç pastalarını ekle
        pastalar.add(new Pasta("Strawberry Cake", 40, 30));
        pastalar.add(new Pasta("Chocolate Cake", 50, 20));
        pastalar.add(new Pasta("Banana Cake", 60, 14));
        pastalar.add(new Pasta("Chestnut Cake", 70, 11));

        System.out.println("Bakery server is running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {  
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            while (true) {
                // Ana menüyü gönder
                out.println("*** Bakery Automation System ***\n1. View Cake List\nPress e for exit\nChoose an option: ");
                String input = in.readLine();

                if (input == null || input.equals("e")) {
                    out.println("Goodbye!");
                    break;
                }

                if (input.equals("1")) {
                    sendCakeList(out);

                    // Pasta seçimi al
                    String cakeChoice = in.readLine();
                    try {
                        int choice = Integer.parseInt(cakeChoice);

                        if (choice >= 1 && choice <= pastalar.size()) {
                            Pasta selectedCake = pastalar.get(choice - 1);

                            if (selectedCake.getStock() > 0) {
                                selectedCake.decrementStock();
                                out.println("You have purchased " + selectedCake.getName() + "!");

                               
                                
                            } else {
                                out.println("Sorry, " + selectedCake.getName() + " is out of stock.");
                            }
                        } else {
                            out.println("Invalid choice. Returning to main menu.");
                        }
                    } catch (NumberFormatException e) {
                        out.println("Invalid input. Returning to main menu.");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected.");
        }
    }

    private static void sendCakeList(PrintWriter out) {
        out.println("*** Cake List ***");
        for (int i = 0; i < pastalar.size(); i++) {
            Pasta p = pastalar.get(i);
            out.println((i + 1) + ". " + p.getName() + " - Price: " + p.getPrice() + " TL - Stock: " + p.getStock());
        }
        out.println("Enter the number of the cake to purchase: ");
    }

    static class Pasta {
        private String name;
        private int price;
        private int stock;

        public Pasta(String name, int price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public int getPrice() {
            return price;
        }

        public int getStock() {
            return stock;
        }

        public void decrementStock() {
            if (stock > 0) {
                stock--;
            }
        }
    }
}
