package me.onixdev.ircchat;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world!");
        Server srv = new Server();
        srv.start();
    }
}