package com.jzy.game.ai.nav.polygon.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.jzy.game.ai.nav.polygon.Polygon;
import com.jzy.game.ai.nav.polygon.PolygonNavMesh;
import com.jzy.game.engine.math.Vector3;
import com.jzy.game.engine.util.TimeUtil;

/**
 * 地图显示面板
 *
 * @author JiangZhiYong
 */
public class PolygonViewPane extends JPanel {

	private static final long serialVersionUID = 1L;
	protected Image backImage;
	protected Graphics2D backImageGraphics2D;
	protected final MovePlayer player;

	Vector3 dir = new Vector3(0, 45, 0); // 方向向量

	Polygon rectangle;
	Vector3 rectangleOriginPoint = new Vector3(60, 0, 60); // 矩形原点
	Vector3 rectangleComparePoint = new Vector3(100, 0, 100); // 矩形比较点

	Polygon sector;
	Vector3 sectorOriginPoint = new Vector3(200, 0, 60); // 扇形原点
	Vector3 sectorComparePoint = new Vector3(100, 0, 100); // 扇形比较点

	Polygon nPolygon;

	Polygon renderPolygon; // 特殊渲染的多边形

	protected boolean isRenderRandomPoints; // 是否渲染随机点
	protected boolean isShowTriangleIndex; // 是否显示三角形序号
	protected boolean isShowVectorIndex; // 是否显示坐标序号
        
        //定位位置
        private Vector3 locationPosition;

	public PolygonViewPane(MovePlayer player) {
		this.player = player;

		Vector3 rectinit = new Vector3(10f, 10f);
		rectangle = player.getMap().getRectangle(rectinit, 50f, dir, 50f, 80f);
		rectangleOriginPoint = rectinit.unityTranslate(dir.y, 50);

		Vector3 sectorinit = new Vector3(200f, 10f);
		sector = player.getMap().getSector(sectorinit, dir, 30f, 80f, 60f);
		sectorOriginPoint = sectorinit.unityTranslate(dir.y, 30);

		nPolygon = player.getMap().getNPolygon(new Vector3(450, 60), 40, 30);
	}

	public static Vector3 getStopPoint(Vector3 up, Vector3 end, float distance) {
		if (distance >= up.dst(end)) {
			return up;
		}
		return end.createPointFromAngle(end.findAngle(up), distance);
	}

	/**
	 * 渲染界面
	 */
	public void render() {
		if (player == null || player.getMap() == null) {
			return;
		}
		if (getWidth() <= 0 || getHeight() <= 0) {
			System.out.println(this.getClass().getSimpleName() + ": width &/or height <= 0!!!");
			return;
		}
		if (backImage == null || getWidth() != backImage.getWidth(null) || getHeight() != backImage.getHeight(null)) {
			backImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
		}
		backImageGraphics2D = (Graphics2D) backImage.getGraphics();
		renderWorld();
		backImageGraphics2D.dispose();
		if (getGraphics() != null) {
			getGraphics().drawImage(backImage, 0, 0, null);
			Toolkit.getDefaultToolkit().sync();
		}
	}

	protected void renderOther(Graphics2D g) {
	}

	/**
	 * 设置选择渲染的多边形
	 * 
	 * @param polygon
	 */
	public void setRenderPolygon(Polygon polygon) {
		if (this.renderPolygon == null || polygon.index != this.renderPolygon.index) {
			this.renderPolygon = polygon;
		} else {
			renderPolygon = null;
		}
	}

	protected void renderWorld() {
		Graphics2D g = backImageGraphics2D;

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		float backGroundGrey = 25f / 255f;
		g.setColor(new Color(backGroundGrey, backGroundGrey, backGroundGrey));
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// 渲染行走区
		float g4 = 0.4f;
		Color triangleColor = new Color(g4, g4, g4);
		g.setColor(triangleColor);
		for (Polygon polygon : player.getMap().getGraph().getPolygons()) {

			g.fill(polygon);

			// 三角形顶点序号
			if (this.isShowTriangleIndex) {
				g.setColor(Color.GREEN);
				g.drawString(String.valueOf(polygon.index), polygon.center.x, polygon.center.z);
			}
			if (this.isShowVectorIndex && polygon.vectorIndexs != null) {
				g.setColor(Color.BLUE);
				for (int i = 0; i < polygon.vectorIndexs.length; i++) {
					Vector3 point = polygon.getPoint(i);
					g.drawString(String.valueOf(polygon.vectorIndexs[i]), point.x/* +MathUtil.random(-4, 4) */,
							point.z/* +MathUtil.random(-4,4) */);
				}
			}

			// 随机点
			if (isRenderRandomPoints) {
				g.setColor(Color.RED);
				// player.getMap().getGraph().createPathRandomPoint();
				for (Vector3 object : polygon.randomPoints) {
					g.fill(new Ellipse2D.Double(object.getX() - 3 / 2f, object.getZ() - 3 / 2f, 3, 3));
				}
				g.setColor(new Color(g4, g4, g4));
			}
			g.setColor(triangleColor);
		}

		// 渲染寻路路线
		g.setColor(Color.LIGHT_GRAY);
		if (player.getPaths().size() > 0) {
			Vector3 currentPoint = player.getPos();
			for (int j = 0; j < player.getPaths().size(); j++) {
				Vector3 nextPoint = player.getPaths().get(j);
				g.draw(new Line2D.Double(currentPoint.getX(), currentPoint.getZ(), nextPoint.getX(), nextPoint.getZ()));
				float d = 5f;
				g.fill(new Ellipse2D.Double(nextPoint.getX() - d / 2f, nextPoint.getZ() - d / 2f, d, d));
				currentPoint = nextPoint;
			}
		}
		g.setColor(Color.red);
		double r = 5;
		g.fill(new Ellipse2D.Double(player.getPos().x - r, player.getPos().z - r, 2 * r, 2 * r));

		// 渲染矩形
		g.setColor(triangleColor);
		g.fill(rectangle);
		rectangleOriginPoint.y += 0.5f;
		if (rectangleOriginPoint.y > 360) {
			rectangleOriginPoint.y = 0;
		}
		rectangleComparePoint = rectangleOriginPoint.unityTranslate(rectangleOriginPoint.y, 50); // 多边形比较点
		// if(rectangle.contains(rectangleComparePoint)) {
		if (rectangle.isInnerPoint(rectangleComparePoint)) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.fill(new Ellipse2D.Double(rectangleComparePoint.x - r, rectangleComparePoint.z - r, 2 * r, 2 * r));

		// 渲染扇形
		g.setColor(triangleColor);
		g.fill(sector);
		sectorOriginPoint.y += 0.5f;
		if (sectorOriginPoint.y > 360) {
			sectorOriginPoint.y = 0;
		}
		sectorComparePoint = sectorOriginPoint.unityTranslate(sectorOriginPoint.y, 60);

		if (sectorOriginPoint.isInSector(dir, sectorComparePoint, 80f, 60f)) {
			g.setColor(Color.RED);
		} else {
			g.setColor(Color.BLUE);
		}
		g.fill(new Ellipse2D.Double(sectorComparePoint.x - r, sectorComparePoint.z - r, 2 * r, 2 * r));

		// 渲染N边形
		g.setColor(triangleColor);
		g.fill(nPolygon);

		// 点击选择的多边形
		if (renderPolygon != null) {
			g.setColor(Color.GRAY);
			g.fill(renderPolygon);
			g.setColor(Color.green);
			g.drawString(String.valueOf(renderPolygon.index), renderPolygon.center.x, renderPolygon.center.z);
			g.setColor(Color.BLUE);
			for (int i = 0; i < renderPolygon.vectorIndexs.length; i++) {
				Vector3 point = renderPolygon.getPoint(i);
				g.drawString(String.valueOf(renderPolygon.vectorIndexs[i]), point.x, point.z);
			}
		}
                
                //渲染定位坐标
                if(locationPosition!=null){
                    g.setColor(Color.WHITE);
                    g.fill(new Ellipse2D.Double(locationPosition.x , locationPosition.z, 1.5f, 1.5f));
                }
               
                
		renderOther(g);
	}

	/**
	 * @return the player
	 */
	public MovePlayer getPlayer() {
		return player;
	}

	public PolygonNavMesh getMap() {
		return player.getMap();
	}

	/**
	 * p 改变随机点是否显示
	 */
	public void changeShowRandomPoint() {
		isRenderRandomPoints = !isRenderRandomPoints;
	}

	public void changeShowTriangleIndex() {
		this.isShowTriangleIndex = !isShowTriangleIndex;
	}

	public void changeShowVectorIndex() {
		this.isShowVectorIndex = !isShowVectorIndex;
	}

    public Vector3 getLocationPosition() {
        return locationPosition;
    }

    public void setLocationPosition(Vector3 locationPosition) {
        this.locationPosition = locationPosition;
    }

}
