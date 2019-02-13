package TouchAutomation;

public class Coordinate{
	int x;
	int y;
	public Coordinate(int newX, int newY) {
		setX(newX);
		setY(newY);
	}
	
	public int getX() {
		return x;
	}
	public void setX(int newX) {
		x = newX;
	}
	public int getY() {
		return y;
	}
	public void setY(int newY) {
		y = newY; 
	}
	public void printCoordinate() {
		System.out.println("Coordinate: " + getX() + ", " + getY());
	}
}

