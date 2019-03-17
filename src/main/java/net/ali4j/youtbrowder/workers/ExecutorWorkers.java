package net.ali4j.youtbrowder.workers;


import net.ali4j.youtbrowder.Constants;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ehsan on 3/17/2019.
 */
public class ExecutorWorkers {
    static ExecutorService executor;

    static {
        executor = Executors.newFixedThreadPool(Constants.THREADPOOLSIZE );}
    }
