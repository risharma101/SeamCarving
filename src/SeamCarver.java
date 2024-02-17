import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {

    private int[][] picarr;
    private int width;
    private int height;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException();

        width = picture.width();
        height = picture.height();
        picarr = new int[height][width];


        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                picarr[row][col] = picture.getRGB(col, row);
            }
        }
    }

    // current picture
    public Picture picture() {
        Picture pic = new Picture(width, height);
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                pic.setRGB(col, row, picarr[row][col]);
            }
        }
        return pic;
    }

    // width of current picture
    public int width() {
        return width;

    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) throw new IllegalArgumentException();
        if (x == 0 || x == width - 1 || y == 0 || y == height - 1) return 1000;

        int n1 = picarr[y][x - 1];
        int[] rgb1 = new int[]{(n1 >> 16) & 0xFF, (n1 >> 8) & 0xFF, n1 & 0xFF};

        int n2 = picarr[y][x + 1];
        int[] rgb2 = new int[]{(n2 >> 16) & 0xFF, (n2 >> 8) & 0xFF, n2 & 0xFF};

        int n3 = picarr[y - 1][x];
        int[] rgb3 = new int[]{(n3 >> 16) & 0xFF, (n3 >> 8) & 0xFF, n3 & 0xFF};

        int n4 = picarr[y + 1][x];
        int[] rgb4 = new int[]{(n4 >> 16) & 0xFF, (n4 >> 8) & 0xFF, n4 & 0xFF};

        int dx = 0;
        int dy = 0;
        for (int i = 0; i < rgb1.length; i++) {
            dx += Math.pow(rgb1[i] - rgb2[i], 2);
            dy += Math.pow(rgb3[i] - rgb4[i], 2);
        }

        return Math.pow(dx + dy, 0.5);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] costarr = new double[height][width];
        int[][] fromarr = new int[height][width];
        for (int j = 0; j < width; j++) {
            for (int i = 0; i < height; i++) {
                double[] minimumH = minimumH(i, j - 1, costarr);
                costarr[i][j] = minimumH[1] + energy(j, i);
                fromarr[i][j] = (int) minimumH[0];
            }
        }
        double min = Double.POSITIVE_INFINITY;
        int ind = -1;
        for (int x = 0; x < height; x++) {
            if (costarr[x][width - 1] < min) {
                min = costarr[x][width - 1];
                ind = x;
            }
        }
        int[] seam = new int[width];
        for (int i = width - 1; i >= 0; i--) {
            seam[i] = ind;
            ind = fromarr[ind][i];
        }
        return seam;

    }

    private double[] minimumH(int i, int j, double[][] costarr) {
        if (j == -1) return new double[]{-1, 0};
        int[] xind = {i - 1, i, i + 1};
        Double[] arr = new Double[3];
        for (int x = 0; x < 3; x++) {
            if (xind[x] >= 0 && xind[x] < height) arr[x] = costarr[xind[x]][j];
            else arr[x] = null;
        }
        double min = Double.MAX_VALUE;
        int ind = -1;
        for (int x = 0; x < 3; x++) {
            if (arr[x] != null && arr[x] < min) {
                min = arr[x];
                ind = x;
            }
        }
        return new double[]{(double) (ind + i - 1), min};
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] costarr = new double[height()][width()];
        int[][] fromarr = new int[height()][width()];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[] minimumV = minimumV(i - 1, j, costarr);
                costarr[i][j] = minimumV[1] + energy(j, i);
                fromarr[i][j] = (int) minimumV[0];
            }
        }
        double min = Double.POSITIVE_INFINITY;
        int ind = -1;
        for (int x = 0; x < width; x++) {
            if (costarr[height - 1][x] < min) {
                min = costarr[height - 1][x];
                ind = x;
            }
        }
        int[] seam = new int[height];
        for (int i = height - 1; i >= 0; i--) {
            seam[i] = ind;
            ind = fromarr[i][ind];
        }
        return seam;

    }

    private double[] minimumV(int i, int j, double[][] costarr) {
        if (i == -1) return new double[]{-1, 0};
        int[] xind = {j - 1, j, j + 1};
        Double[] arr = new Double[3];
        for (int x = 0; x < 3; x++) {
            if (xind[x] >= 0 && xind[x] < width) arr[x] = costarr[i][xind[x]];
            else arr[x] = null;
        }
        double min = Double.MAX_VALUE;
        int ind = -1;
        for (int x = 0; x < 3; x++) {
            if (arr[x] != null && arr[x] < min) {
                min = arr[x];
                ind = x;
            }
        }
        return new double[]{(double) (ind + j - 1), min};
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || seam.length != width) throw new IllegalArgumentException("seam is not the right size");
        for (int i = 0; i < seam.length; i++) {
            if (i != 0 && seam[i] != seam[i - 1] && seam[i] != seam[i - 1] + 1 && seam[i] != seam[i - 1] - 1)
                throw new IllegalArgumentException();
            if (seam[i] < 0 || seam[i] > height - 1) throw new IllegalArgumentException();
        }
        height--;
        for (int i = 0; i < seam.length; i++) {
            picarr[seam[i]][i] = Integer.MIN_VALUE;
        }

        int j = 0;
        for (int col = 0; col < width; col++) {
            int i = 0;
            for (int row = 0; row < height; row++) {
                if (picarr[i][j] == Integer.MIN_VALUE) {
                    row--;
                } else {
                    picarr[row][col] = picarr[i][j];
                }
                i++;
            }
            j++;
        }
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || seam.length != height) throw new IllegalArgumentException("");
        for (int i = 0; i < seam.length; i++) {
            if (i != 0 && seam[i] != seam[i - 1] && seam[i] != seam[i - 1] + 1 && seam[i] != seam[i - 1] - 1)
                throw new IllegalArgumentException();
            if (seam[i] < 0 || seam[i] > width - 1) throw new IllegalArgumentException();
        }
        width--;

        for (int i = 0; i < seam.length; i++) {
            picarr[i][seam[i]] = Integer.MIN_VALUE;
        }

        int i = 0;
        for (int row = 0; row < height; row++) {
            int j = 0;
            for (int col = 0; col < width; col++) {
                if (picarr[i][j] == Integer.MIN_VALUE) {
                    col--;
                } else {
                    picarr[row][col] = picarr[i][j];
                }
                j++;
            }
            i++;
        }
    }

    //  unit testing (optional)
    public static void main(String[] args) {

        Picture p = new Picture("testFiles//chameleon.png");
        int rowsToRemove = 100;
        int colsToRemove = 300;

        StdOut.printf("image is %d columns by %d rows\n", p.width(), p.height());

        SeamCarver sc = new SeamCarver(p);

        Stopwatch sw = new Stopwatch();
        for (int i = 0; i < rowsToRemove; i++) {
            int[] seam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(seam);
        }
        for (int i = 0; i < colsToRemove; i++) {
            int[] seam = sc.findVerticalSeam();
            sc.removeVerticalSeam(seam);
        }

        Picture outputImg = sc.picture();

        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

        StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        p.show();
        outputImg.show();
    }

}
