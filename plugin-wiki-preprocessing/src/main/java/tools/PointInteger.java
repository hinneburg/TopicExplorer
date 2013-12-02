package tools;

public class PointInteger {

	private Integer startPoint;
	private Integer endPoint;

	public PointInteger(Integer startPoint, Integer endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	public Integer getStartPosition() {
		return this.startPoint;
	}

	public Integer getEndPosition() {
		return this.endPoint;
	}

	public void setStartPoint(Integer startPoint) {
		this.startPoint = startPoint;
	}

	public void setEndPoint(Integer endPoint) {
		this.endPoint = endPoint;
	}
}
