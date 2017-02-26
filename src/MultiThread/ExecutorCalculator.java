package MultiThread;

/**
 * Created by zhuzheng on 17/2/26.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ExecutorCalculator {

    private ExecutorService exec;
    private int cpuCoreNumber;
    private List<Future<Integer>> tasks = new ArrayList<Future<Integer>>();

    // 内部类
    class SumCalculator implements Callable<Integer> {
        private int[] numbers;
        private int start;
        private int end;

        public SumCalculator(final int[] numbers, int start, int end) {
            this.numbers = numbers;
            this.start = start;
            this.end = end;
        }

        public Integer call() throws Exception {
            Integer max = 0;
            for (int i = start; i < end; i++) {
                if (max < numbers[i]) {
                    max = numbers[i];
                }
            }
            return max;
        }
    }

    public ExecutorCalculator() {
        //cpuCoreNumber = Runtime.getRuntime().availableProcessors();
        cpuCoreNumber = 20;
        exec = Executors.newFixedThreadPool(cpuCoreNumber);
    }

    public Integer max(final int[] numbers) {
        // 根据CPU核心个数拆分任务，创建FutureTask并提交到Executor
        for (int i = 0; i < cpuCoreNumber; i++) {
            int increment = numbers.length / cpuCoreNumber + 1;
            int start = increment * i;
            int end = increment * i + increment;
            if (end > numbers.length)
                end = numbers.length;
            SumCalculator subCalc = new SumCalculator(numbers, start, end);
            FutureTask<Integer> task = new FutureTask<Integer>(subCalc);
            tasks.add(task);
            if (!exec.isShutdown()) {
                exec.submit(task);
            }
        }
        return getResult();
    }

    /**
     * 迭代每个子任务，获得部分和，相加返回
     *
     * @return
     */
    public Integer getResult() {
        Integer result = 0;
        for (Future<Integer> task : tasks) {
            try {
                // 如果计算未完成则阻塞
                Integer subMax = task.get();
                if(result < subMax){
                    result = subMax;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void close() {
        exec.shutdown();
    }
}


