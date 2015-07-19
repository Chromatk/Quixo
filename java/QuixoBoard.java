public class QuixoBoard {
	public boolean[][] isColored;
	public boolean[][] isBlue;
	int dim;

	public QuixoBoard(boolean[][] c, boolean[][] b) {
		isColored = c;
		isBlue = b;
		dim = isColored.length;
	}

	public QuixoBoard copyOf() {
		boolean[][] c = new boolean[dim][dim];
		boolean[][] b = new boolean[dim][dim];

		for(int i=0;i<dim;i++) {
			for(int j=0;j<dim;j++) {
				c[i][j] = isColored[i][j];
				b[i][j] = isBlue[i][j];
			}
		}
		return(new QuixoBoard(c, b));

	}
}