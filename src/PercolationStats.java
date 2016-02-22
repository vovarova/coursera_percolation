import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private double mean, confidenceHi, confidenceLo, stddev;

    public PercolationStats(int gridSize, int times) {
        if (gridSize <= 0 || times <= 0) {
            throw new IllegalArgumentException("Argument 'N' must be grater than 0");
        }
        double[] results = new double[times];
        int nSquare = gridSize * gridSize;


        for (int i = 0; i < times; i++) {
            results[i] = checkPercolation(gridSize,nSquare);
        }
        mean = StdStats.mean(results);
        stddev = StdStats.stddev(results);
        double middleRes = 1.96 * (stddev / Math.sqrt(times));
        confidenceLo = mean - middleRes;
        confidenceHi = mean + middleRes;
    }

    private double checkPercolation(int gridSize,int nSquare) {
        Percolation percolation = new Percolation(gridSize);
        int[] randomArray = getRandomArray(nSquare);
        int i = 0;
        for (; i < randomArray.length; i++) {
            if (percolation.percolates()) {
                break;
            }
            int arrEl = randomArray[i];
            int row = arrEl / gridSize + 1;
            int column = arrEl % gridSize + 1;
            percolation.open(row, column);

        }
        return (double) i / nSquare;
    }

    private int[] getRandomArray(int nSquare) {
        int[] array = new int[nSquare];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        StdRandom.shuffle(array);
        return array;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLo() {
        return confidenceLo;
    }

    public double confidenceHi() {
        return confidenceHi;
    }

    public static void main(String[] args) {
        Stopwatch stopwatch = new Stopwatch();
        PercolationStats percolationStats =
                new PercolationStats(Integer.parseInt(args[0]),
                        Integer.parseInt(args[1]));

        System.out.println("mean                    = " + percolationStats.mean());
        System.out.println("stddev                  = " + percolationStats.stddev());
        System.out.println("95% confidence interval = "
                + percolationStats.confidenceLo() + ", "
                + percolationStats.confidenceHi());
        System.out.println("Time" + stopwatch.elapsedTime());
    }
}

