package life.genny.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

public class StringTuple implements Serializable {
	@Expose
	String frame;
	@Expose
	FramePosition framePosition;
	@Expose
	Double weight;
	
	private StringTuple() {}
	
	public StringTuple(String frame, FramePosition framePosition, Double weight)
	{
		this.frame = frame;
		this.framePosition = framePosition;
		this.weight = weight;
	}

	/**
	 * @return the frame
	 */
	public String getFrame() {
		return frame;
	}

	/**
	 * @param frame the frame to set
	 */
	public void setFrame(String frame) {
		this.frame = frame;
	}

	/**
	 * @return the framePosition
	 */
	public FramePosition getFramePosition() {
		return framePosition;
	}

	/**
	 * @param framePosition the framePosition to set
	 */
	public void setFramePosition(FramePosition framePosition) {
		this.framePosition = framePosition;
	}

	/**
	 * @return the weight
	 */
	public Double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Override
	public String toString() {
		return "FrameTuple3 [frame=" + frame + ", framePosition=" + framePosition + ", weight=" + weight + "]";
	}
	
	
}
