package MultiThread;

/**
 * Created by zhuzheng on 17/2/26.
 */

import ThulacAdapter.thulac.ThulacAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class ExecutorSeparator {

    private ExecutorService exec;
    private int cpuCoreNumber;
    private List<Future<String[]>> tasks = new ArrayList<Future<String[]>>();

    // 内部类
    class SumCalculator implements Callable<String[]> {
        private String[] arr_string;
        private int start;
        private int end;


        public SumCalculator(final String[] arr_string, int start, int end) {
            this.arr_string = arr_string;
            this.start = start;
            this.end = end;
        }

        public String[] call() throws Exception {
            String[] temp = null;
            ThulacAdapter thulac = new ThulacAdapter();
            for (int i = start; i < end; i++) {
                temp = thulac.run(arr_string[i]);
            }
            return temp;
        }
    }

    public ExecutorSeparator() {
        cpuCoreNumber = 20;
        exec = Executors.newFixedThreadPool(cpuCoreNumber);
    }

    public String[] separate(final String[] arr_string) {
        // 根据CPU核心个数拆分任务，创建FutureTask并提交到Executor
        for (int i = 0; i < cpuCoreNumber; i++) {
            int increment = arr_string.length / cpuCoreNumber + 1;
            int start = increment * i;
            int end = increment * i + increment;
            if (end > arr_string.length)
                end = arr_string.length;
            SumCalculator subCalc = new SumCalculator(arr_string, start, end);
            FutureTask<String[]> task = new FutureTask<String[]>(subCalc);
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
    public String[] getResult() {
        String[] result = null;
        for (Future<String[]> task : tasks) {
            try {
                // 如果计算未完成则阻塞
                String[] subResult = task.get();
                result = Arrays.copyOf(result, result.length + subResult.length);//扩容
                System.arraycopy(subResult, 0, result, result.length, subResult.length);//将第二个数组与第一个数组合并
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