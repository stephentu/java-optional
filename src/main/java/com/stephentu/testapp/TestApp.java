package com.stephentu.testapp;

import static com.stephentu.JavaOptional.*;

public class TestApp {

  public static void main(String[] args) {
    runMain(TestApp.class, args);
  }
  
  public static void optMain(int numClients, double replicationFactor, String bindHost) {
    System.out.println("numClients: " + numClients);
    System.out.println("replicationFactor: " + replicationFactor);
    System.out.println("bindHost: " + bindHost);
  }

}
