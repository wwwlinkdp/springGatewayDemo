package com.savills.apigateway.helper;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class LoggerThread<T extends BaseLog> extends Thread {

    private static int BATCH_SIZE =10;
    private static int QUEUE_SIZE =256;
    private static volatile BaseLog SHUTDOWN_REQ;
    private static volatile LoggerThread instance;
    private static Object mutex = new Object();

    private static volatile ScheduledExecutorService scheduledExecutorService;
    private AtomicInteger count = new AtomicInteger();
    private BlockingQueue<T> itemsToLog ;


    private volatile boolean shuttingDown, loggerTerminated;

    public static LoggerThread getInstance() {
        LoggerThread result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if(result == null){
                    instance = result = new LoggerThread();


                    SHUTDOWN_REQ = new BaseLog("SHUTDOWN");

                    scheduledExecutorService = Executors.newScheduledThreadPool(1);
                    Runnable command = () -> {
                        instance.bathProcess();
                    };
                    scheduledExecutorService.scheduleWithFixedDelay(command, 0, 500, TimeUnit.MILLISECONDS);


                }
            }
        }
        return result;
    }

    private void bathProcess() {
        int remaining = itemsToLog.size();
        if(remaining>=0)
        {
            try {
                extracted(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        //BATCH_SIZE
    }

    private LoggerThread() {
        itemsToLog= new ArrayBlockingQueue<>(QUEUE_SIZE);
        start();
    }

    public void run() {
        try {
            T item;
            while (true)
            {
                int remaining = itemsToLog.size();
                if(remaining>=BATCH_SIZE)
                {
                    if (extracted(0)) break;
                }else {
                    Thread.sleep(2000);
                }


            }

        } catch (InterruptedException iex) {
        } finally {
            loggerTerminated = true;
        }
    }

    private boolean extracted(int mode) throws InterruptedException {
        T item;
        item = itemsToLog.take();
        if(item == SHUTDOWN_REQ){
            return true;
        }
        List<T> elements = new ArrayList<T>();
        elements.add(item);
        itemsToLog.drainTo(elements);
        int consumedCount = 0;
        for (T e : elements) {
            consumedCount = count.incrementAndGet();
//            if(consumedCount %1000 ==0)
//            {
//                System.out.print(mode ==0 ? "#": "." );
//            }
            //System.out.print(e.toString());
        }
        System.out.print(consumedCount+(mode ==0 ? "#": "." ));



        return false;
    }

    public void log(T str) {
        if(shuttingDown || loggerTerminated)
        {
            return;
        }
        try {
            itemsToLog.put(str);
        } catch (Exception iex) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Unexpected interruption");
        }
    }
    public void shutDown() throws InterruptedException {
        shuttingDown = true;
        //itemsToLog.put(SHUTDOWN_REQ);
    }

}