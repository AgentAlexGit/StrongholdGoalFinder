package com.vision;

import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

public class NetTable {

    private String tableName = "LIFE/autonData";
    private int portNumber = 1735;
    private int updateIntervalTime = 30; //NOTICE: this is the lowest updateInterval time
    private String separator = "/";

    public NetTable() {
        System.out.println("Server is good: " + setTableServer(tableName, portNumber));
    }

    public NetTable(String tableName) {
        System.out.println("Server is good: " + setTableServer(tableName, portNumber));
    }

    public NetTable(String tableName, int serverPortNumber) {
        System.out.println("Server is good: " + setTableServer(tableName, serverPortNumber));
    }

    public NetTable(String tableName, int serverPortNumber, int updateIntervalTime) {
        System.out.println("Server is good: " + setTableServer(tableName, serverPortNumber));
        this.updateIntervalTime = updateIntervalTime;
    }

    public void publishTestData(String dataName) {
        for (int i = 0; i < 10; i++) {
            NetworkTablesJNI.putDouble(tableName + separator + dataName + i, (double) i * Math.random());
            delay();
        }
    }

    public boolean updateTable(Object obj, String dataName) {
        //TODO
        delay();
        return true;
    }

    private void delay() {
        try {
            Thread.sleep(updateIntervalTime);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void stopServer() {
        NetworkTablesJNI.stopServer();
    }

    public boolean setTableServer(String tableName, int serverPortNumber) {
        this.tableName = tableName;
        portNumber = serverPortNumber;
        stopServer();
        try {
            NetworkTablesJNI.startServer(tableName, "", serverPortNumber);
            return true;
        } catch (Exception e1) {
            System.out.println(e1.toString());
            e1.printStackTrace();
            return false;
        }
    }
}
