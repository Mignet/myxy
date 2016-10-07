package com.v5ent.game.pfa;

import com.badlogic.gdx.ai.pfa.Heuristic;

public class ManhattanDistance implements Heuristic<MyNode> {
	@Override
	public float estimate (final MyNode node, final MyNode endNode) {
		return Math.abs(endNode.getX() - node.getX()) + Math.abs(endNode.getY() - node.getY());
	}
}