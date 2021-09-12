package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        final int CALL_COUNT = 60;
        final int OPERATOR_COUNT = 2;
        final BlockingQueue<Integer> incomingCalls = new LinkedBlockingQueue<>();

        Runnable ats = () -> {
            int j = 1;
            int steps = 2;
            while (!Thread.currentThread().isInterrupted() && steps-- != 0) {
                for (int i = 0; i < CALL_COUNT; i++)
                    incomingCalls.add(j++);
                System.out.println(Thread.currentThread().getName() + ": поступило " + CALL_COUNT + " звонков!");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        };
        Runnable operator = () -> {
            Random random = new Random();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    while (incomingCalls.isEmpty())
                        Thread.sleep(3000);
                    Integer callNum = incomingCalls.take();
                    System.out.println((Thread.currentThread().getName() + " принимает звонок " + callNum));
                    Thread.sleep(3000 - random.nextInt(1000));
                    if (incomingCalls.isEmpty()) return;
                } catch (InterruptedException ignored) {
                }
            }
        };

        Thread call = new Thread(ats, "АТС");
        call.start();

        List<Thread> operators = new ArrayList<>();
        for (int i = 0; i < OPERATOR_COUNT; i++) {
            Thread operatorThread = new Thread(operator, "Оператор " + (i + 1));
            operators.add(operatorThread);
            operatorThread.start();
        }

        try {
            call.join();
            for (Thread operatorThread : operators)
                operatorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}