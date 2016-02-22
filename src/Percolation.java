import com.javamex.classmexer.MemoryUtil;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stopwatch;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private byte[][] grid;

    private WeightedQuickUnionUF weightedQuickUnionUF;
    private int N;
    private boolean percolates = false;
    private byte open = 0b1;
    private byte bottom = 0b011;
    private byte top = 0b101;
    private byte topAndBottom = 0b111;

    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("Argument 'N' must be grater than 0");
        }
        this.N = N;
        grid = new byte[N][N];
        int middleEvaluation = N * N;
        weightedQuickUnionUF = new WeightedQuickUnionUF(middleEvaluation);
    }

    public void open(int i, int j) {
        validateCell(i, j);
        //Check if already opened
        if (isOpen(i, j)) {
            return;
        }
        byte status = open;
        int convertedCell = from2xyTo1x(i - 1, j - 1);
        if (i == 1) {
            status = top;
        } else if (i == N) {
            status = bottom;
        }

        if (i == 1 && i == N) {
            status = topAndBottom;
            percolates = true;
        }

        status = checkIfOpenAndConnect(convertedCell, status, i - 1, j);
        status = checkIfOpenAndConnect(convertedCell, status, i + 1, j);
        status = checkIfOpenAndConnect(convertedCell, status, i, j - 1);
        status = checkIfOpenAndConnect(convertedCell, status, i, j + 1);
        grid[i - 1][j - 1] = status;

    }

    private byte checkIfOpenAndConnect(int convertedCell,
                                       byte status, int ni, int nj) {

        if (notValid(ni, nj) || !isOpen(ni, nj)) {
            return status;
        }
        ni = ni - 1;
        nj = nj - 1;
        int convertedNeighboardCell = from2xyTo1x(ni, nj);
        int nRoot = weightedQuickUnionUF.find(convertedNeighboardCell);
        if (convertedNeighboardCell != nRoot) {
            ni = from1xTo2xyX(nRoot);
            nj = from1xTo2xyY(nRoot);
        }

        byte nRootStatus = grid[ni][nj];
        if (status != nRootStatus) {
            status = (byte) (status | nRootStatus);
            grid[ni][nj] = status;
            int fRoot = weightedQuickUnionUF.find(convertedCell);
            if (convertedCell != fRoot) {
                convertedCell = fRoot;
                grid[from1xTo2xyX(fRoot)][from1xTo2xyY(fRoot)] = status;
            }
        }
        weightedQuickUnionUF.union(nRoot, convertedCell);
        if (status == topAndBottom) {
            percolates = true;
        }
        return status;
    }

    private int from2xyTo1x(int i, int j) {
        return (i) * N + j;
    }

    private int from1xTo2xyX(int x) {
        return x / N;
    }

    private int from1xTo2xyY(int x) {
        return x % N;
    }


    public boolean isOpen(int i, int j) {
        validateCell(i, j);
        return grid[i - 1][j - 1] > 0;
    }

    public boolean isFull(int i, int j) {
        validateCell(i, j);
        i = i - 1;
        j = j - 1;
        int convertedVal = from2xyTo1x(i, j);
        int rootVal = weightedQuickUnionUF.find(convertedVal);
        if (convertedVal != rootVal) {
            i = from1xTo2xyX(rootVal);
            j = from1xTo2xyY(rootVal);
        }
        byte gridVal = grid[i][j];

        return gridVal == top || gridVal == topAndBottom;
    }

    public boolean percolates() {
        return percolates;
    }

    private boolean notValid(int i, int j) {
        boolean notValid = false;
        if (i < 1 || j < 1 || i > N || j > N) {
            notValid = true;
        }
        return notValid;
    }

    private void validateCell(int i, int j) {
        if (notValid(i, j)) {
            String message = "Invalid cell arguments i = " + i + " j = " + j;
            throw new IndexOutOfBoundsException(message);
        }
    }

    public static void main(String[] args) {

        Stopwatch stopwatch = new Stopwatch();
        In in = new In("D:\\tmp\\percolation\\input100.txt");      // input file
        int N = in.readInt();         // N-by-N percolation system
        Percolation perc = new Percolation(N);
        while (!in.isEmpty()) {
            int i = in.readInt();
            int j = in.readInt();
            perc.open(i, j);
            perc.isOpen(i, j);
            perc.percolates();
            perc.isFull(i, j);
        }
        long noBytes = MemoryUtil.memoryUsageOf(perc);
        System.out.println("Bytes " + noBytes);
        System.out.println("Time " + stopwatch.elapsedTime());
    }
}