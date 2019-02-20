package TouchAutomation;

import java.sql.Timestamp;

public class Coordinate{
	int x;
	int y;
	Timestamp timestamp;
	long timeDiff;
	
	public Coordinate(int newX, int newY) {
		setX(newX);
		setY(newY);
		timestamp = null;
		timeDiff = 0;
	}
	
	public Coordinate(int newX, int newY, Timestamp time) {
		setX(newX);
		setY(newY);
		recordTimestamp(time);
	}
	
	public Coordinate(int newX, int newY, Timestamp time, long diff) {
		setX(newX);
		setY(newY);
		recordTimestamp(time);
		setTimeDiff(diff);
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
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void recordTimestamp(Timestamp time) {
		timestamp = time;
	}
	
	public void printCoordinate() {
		System.out.println("Coordinate: " + getX() + ", " + getY() + " @"+timestamp.getTime());
	}
	
	/***
	 * @param other
	 * @return milliseconds
	 */
	public long getTimeDifference(Coordinate other) {
		long diff;
		if(other == null) {
			diff = 0;
		}
		else {
			diff = timestamp.getTime()-other.getTimestamp().getTime();
		} 
		return diff;
	}
	
	public void setTimeDiff(long diff) {
		timeDiff = diff;
	}
	
	public long getTimeDiff() {
		return timeDiff;
	}
}

