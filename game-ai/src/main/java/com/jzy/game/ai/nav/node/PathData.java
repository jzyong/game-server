/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jzy.game.ai.nav.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.jzy.game.engine.math.Vector3;

/**
 * 寻路数据
 * 
 * @author JiangZhiYong
 *
 */
public class PathData implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	/**
	 * 寻路结果
	 * 
	 * @author JiangZhiYong
	 * @mail 359135103@qq.com
	 */
	public enum Result {
		NO_RESULT {
			public String getMessage() {
				return "无寻路数据";
			}

			public boolean isError() {
				return true;
			}
		},
		SUCCESS {
			public String getMessage() {
				return "Success, path found.";
			}

			public boolean isError() {
				return false;
			}
		},
		ERROR1 {
			public String getMessage() {
				return "开始点到结束点的距离大于最大寻路距离";
			}

			public boolean isError() {
				return true;
			}
		},
		ERROR2 {
			public String getMessage() {
				return "开始节点搜索不到可连接节点和结束节点，增加最大搜索距离和检测节点是否在块中";
			}

			public boolean isError() {
				return true;
			}
		},
		ERROR3 {
			public String getMessage() {
				return "结束节点搜索不到可连接节点和结束节点，增加最大搜索距离和检测节点是否在块中";
			}

			public boolean isError() {
				return true;
			}
		},
		ERROR4 {
			public String getMessage() {
				return "路径未找到，可能障碍物围住了开始位置和结束位置，或者可搜索最大路径不够大";
			}

			public boolean isError() {
				return true;
			}
		};

		// 是否错误
		public abstract boolean isError();

		public abstract String getMessage();

		public String toString() {
			return getMessage();
		}
	}

	Result result;
	// 从起始点到终点的
	public List<Vector3> points;
	// 从终点到起始点的
	public HashSet<KNode> nodes;

	public PathData() {
		reset();
	}

	public PathData(Result result) {
		initLists();
		if (result.isError() == false) {
			throw new IllegalArgumentException("只能传error枚举类型. result.isError() == " + result.isError());
		}
		this.result = result;
	}

	public PathData(List<Vector3> points, HashSet<KNode> nodes) {
		setSuccess(points, nodes);
	}

	public void reset() {
		this.result = Result.NO_RESULT;
		initLists();
	}

	public void initLists() {
		points = new ArrayList<>();
		nodes = new HashSet<>();
	}

	public void setError(Result result) {
		if (result.isError() == false) {
			throw new IllegalArgumentException("必须设置错误类型 result.isError() == " + result.isError());
		}
		this.result = result;
		initLists();
	}

	public void setSuccess(List<Vector3> points, HashSet<KNode> nodes) {
		result = Result.SUCCESS;
		initLists();
		this.points.addAll(points);
		this.nodes.addAll(nodes);
	}

	public boolean isError() {
		return result.isError();
	}

	public Result getResult() {
		return result;
	}

	public HashSet<KNode> getNodes() {
		return nodes;
	}

	public List<Vector3> getPoints() {
		return points;
	}

	@Override
	public PathData clone() {
		try {
			PathData clone = (PathData) super.clone();
			clone.nodes = new HashSet<>(nodes);
			clone.points = new ArrayList<>(points);
			return clone;
		} catch (Exception e) {
			throw new UnsupportedOperationException(e);
		}
	}

}
