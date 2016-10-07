package com.v5ent.game.pfa;

/** A {@code Ray} is made up of a starting point and an ending point.
 * 
 * @param <MyNode> 
 * 
 * @author davebaol */
public class MyRay {

	/** The starting point of this ray. */
	public MyNode start;

	/** The ending point of this ray. */
	public MyNode end;

	/** Creates a {@code Ray} with the given {@code start} and {@code end} points.
	 * @param start the starting point of this ray
	 * @param end the starting point of this ray */
	public MyRay (MyNode start, MyNode end) {
		this.start = start;
		this.end = end;
	}

	/** Sets this ray from the given ray.
	 * @param ray The ray
	 * @return this ray for chaining. */
	public MyRay set (MyRay ray) {
		start = (ray.start);
		end = (ray.end);
		return this;
	}

	/** Sets this Ray from the given start and end points.
	 * @param start the starting point of this ray
	 * @param end the starting point of this ray
	 * @return this ray for chaining. */
	public MyRay set (MyNode start, MyNode end) {
		this.start = (start);
		this.end = (end);
		return this;
	}
}
