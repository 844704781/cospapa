package com.sohoer.cospapa.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TreadUtils {

    public static void main(String[] args) {

        List<Map<String,String>> applyHistoryList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Map<String,String> applyHistory = new HashMap();
            applyHistory.put("comment","a");
            applyHistoryList.add(applyHistory);
        }
        long currentTimeMillis = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(5);
        for(Map<String,String> applyHistory : applyHistoryList){
            Callable<Map<String,String>> run = new Callable<Map<String,String>>(){
                @Override
                public Map<String,String> call() throws InterruptedException{
                    String comment = "A";
                    applyHistory.put("comment",comment);
                    Thread.sleep(1000);
                    System.out.println(applyHistory.get("comment"));

                    return applyHistory;
                }
            };
            pool.submit(run);

        }
        pool.shutdown();

        System.out.println("使用线程池一共执行："+String.valueOf(System.currentTimeMillis()-currentTimeMillis)+"ms");
    }

}
