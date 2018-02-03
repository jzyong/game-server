package com.jzy.game.ai.nav.edge;
///*******************************************************************************

import com.jzy.game.ai.pfa.Heuristic;
import com.jzy.game.engine.util.math.Vector3;

// * Copyright 2015 See AUTHORS file.
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// ******************************************************************************/
//
//package com.jzy.game.ai.nav;


/**navmesh 启发式消耗预估
 * <br>
 * @author jsjolund
 */
public class NavMeshHeuristic implements Heuristic<Triangle> {

	private final static Vector3 A_AB = new Vector3();
	private final static Vector3 A_BC = new Vector3();
	private final static Vector3 A_CA = new Vector3();
	private final static Vector3 B_AB = new Vector3();
	private final static Vector3 B_BC = new Vector3();
	private final static Vector3 B_CA = new Vector3();

	/**
	 * Estimates the distance between two triangles, by calculating the distance
	 * between their edge midpoints.
	 *
	 * @param node
	 * @param endNode
	 * @return
	 */
	@Override
	public float estimate(Triangle node, Triangle endNode) {
		float dst2;
		float minDst2 = Float.POSITIVE_INFINITY;
		A_AB.set(node.a).add(node.b).scl(0.5f);
		A_BC.set(node.b).add(node.c).scl(0.5f);
		A_CA.set(node.c).add(node.a).scl(0.5f);

		B_AB.set(endNode.a).add(endNode.b).scl(0.5f);
		B_BC.set(endNode.b).add(endNode.c).scl(0.5f);
		B_CA.set(endNode.c).add(endNode.a).scl(0.5f);

		if ((dst2 = A_AB.dst2(B_AB)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_AB.dst2(B_BC)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_AB.dst2(B_CA)) < minDst2)
			minDst2 = dst2;

		if ((dst2 = A_BC.dst2(B_AB)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_BC.dst2(B_BC)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_BC.dst2(B_CA)) < minDst2)
			minDst2 = dst2;

		if ((dst2 = A_CA.dst2(B_AB)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_CA.dst2(B_BC)) < minDst2)
			minDst2 = dst2;
		if ((dst2 = A_CA.dst2(B_CA)) < minDst2)
			minDst2 = dst2;

		return (float) Math.sqrt(minDst2);
	}

}
