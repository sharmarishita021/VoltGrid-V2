package com.evstation.app;


public class EVStationApp {

    public static void main(String[] args) {
        System.out.println("Initializing VoltGrid V2 Hub...");
        MenuController controller = new MenuController();
        controller.run();
    }
}
