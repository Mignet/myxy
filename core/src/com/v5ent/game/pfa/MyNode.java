package com.v5ent.game.pfa;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

public class MyNode {
	private final int index;
	private final int x;
	private final int y;
	private String value;
	private final Array<Connection<MyNode>> connections;

	public MyNode(final int index, final int x, final int y,final String value, final int capacity) {
		this.index = index;
		this.x = x;
		this.y = y;
		this.value = value;
		this.connections = new Array<Connection<MyNode>>(capacity);
	}

	public int getIndex() {
		return index;
	}

	public Array<Connection<MyNode>> getConnections() {
		return connections;
	}

	@Override
	public String toString() {
		return "IndexedNodeFake [index=" + index + ", x=" + getX() + ", y=" + getY()
				+ ", connections=" + connections + "]";
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * set the value
	 */
	public void setValue(String val) {
		this.value = val;
	}
}
