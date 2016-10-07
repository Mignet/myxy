package com.v5ent.game.pfa;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

public class MyGraph implements IndexedGraph<MyNode> {

	private Array<MyNode> nodes;

	public MyGraph (Array<MyNode> nodes) {
		this.setNodes(nodes);
	}

	@Override
	public int getIndex (MyNode node) {
		return node.getIndex();
	}

	@Override
	public Array<Connection<MyNode>> getConnections (MyNode fromNode) {
		return fromNode.getConnections();
	}

	@Override
	public int getNodeCount () {
		return getNodes().size;
	}

	/**
	 * @return the nodes
	 */
	public Array<MyNode> getNodes() {
		return nodes;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Array<MyNode> nodes) {
		this.nodes = nodes;
	}

	public MyNode getNode(int x, int y) {
		for(MyNode node:nodes){
			if(node.getX()==x && node.getY()==y){
				return node;
			}
		}
		return null;
	}

	public MyNode get(int index) {
		return nodes.get(index);
	}

	public void swapNodes(int m, int n) {
		String temp = nodes.get(n).getValue();
		MyNode x = nodes.get(m);
		nodes.get(n).setValue(x.getValue());
		x.setValue(temp);
	}

	public void truncatePath(int index) {
		nodes.removeIndex(index);
	}
}
