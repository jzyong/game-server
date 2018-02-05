/**
 * 特别鸣谢修仙项目组负责人吴章义
 */
package com.jzy.game.ai.nav.node.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

import com.jzy.game.ai.nav.node.KPolygon;
import com.jzy.game.ai.nav.node.NodeNavMesh;
import com.jzy.game.ai.nav.node.TriangleBlock;
import com.jzy.game.engine.math.Vector3;

/**
 * 地图显示面板
 *
 * @author wzyi
 * @fix JiangZhiYong
 */
public class ViewPane extends JComponent {

    protected Image backImage;
    protected Graphics2D backImageGraphics2D;
    protected final MovePlayer player;

    Vector3 center = new Vector3(220, 0, 220);
    Vector3 test = new Vector3(250, 0, 250);
    Vector3 translate;
    Vector3 direction;
    Vector3 stop;
    KPolygon poly = null;
    protected boolean isRenderRandomPoints; //是否渲染随机点
    protected boolean isRenderXingZouCeng = true; //是否渲行走层

    public ViewPane(MovePlayer player) {
        this.player = player;
        direction = center.unityTranslate(Vector3.ZERO, 45, 60);
        direction.y = 45;

        stop = getStopPoint(center, test, 5);
    }

    public static Vector3 getStopPoint(Vector3 up, Vector3 end, float distance) {
        if (distance >= up.distance(end)) {
            return up;
        }
        return end.createPointFromAngle(end.findAngle(up), distance);
    }

    public void render() {
        if (player == null || player.getMap() == null) {
            return;
        }

        if (poly == null) {
            poly = this.player.getMap().getKPolygon(center, 0, direction, 30, 80);
        }

        center.y += 0.5;
        if (center.y > 360) {
            center.y = 0;
        }
        translate = center.unityTranslate(direction, (float) (center.y), 60);
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

    protected void renderWorld() {
        Graphics2D g = backImageGraphics2D;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        float backGroundGrey = 77f / 255f;
        g.setColor(new Color(backGroundGrey, backGroundGrey, backGroundGrey));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //渲染行走区
        float g4 = 0.2f;

        g.setColor(new Color(g4, g4, g4));
        for (TriangleBlock block : player.getMap().getPathAreas()) {

            KPolygon p = block.getPolygon();
            if (isRenderXingZouCeng) {
                g.fill(p);
            }
            if (isRenderRandomPoints) {
                g.setColor(Color.RED);
                for (Vector3 object : block.getRandomPoints()) {
                    g.fill(new Ellipse2D.Double(object.getX() - 3 / 2f, object.getZ() - 3 / 2f, 3, 3));
                }
                g.setColor(new Color(g4, g4, g4));
            }
        }

        //渲染阻挡
        g4 = 0.1f;
        g.setColor(new Color(g4, g4, g4));
        List<TriangleBlock> blocks = new ArrayList<>(player.getMap().getBlockAreas());
        for (TriangleBlock block : blocks) {
            KPolygon p = block.getPolygon();
            g.fill(p);
        }

        g.setColor(Color.LIGHT_GRAY);
        if (player.pathData.points.size() > 0) {
            Vector3 currentPoint = player.getPos();
            for (int j = 0; j < player.pathData.points.size(); j++) {
                Vector3 nextPoint = player.pathData.points.get(j);
                g.draw(new Line2D.Double(currentPoint.getX(), currentPoint.getZ(), nextPoint.getX(), nextPoint.getZ()));
                float d = 5f;
                g.fill(new Ellipse2D.Double(nextPoint.getX() - d / 2f, nextPoint.getZ() - d / 2f, d, d));
                currentPoint = nextPoint;
            }
        }
        if (poly != null) {
            if (poly.contains(translate)) {
                g.setColor(Color.GREEN);
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }
            g.fill(poly);
        }
        if (center.isInSector(direction, translate, 62, 45)) {
            g.setColor(Color.RED);
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        g.draw(new Line2D.Double(center.x, center.z, translate.x, translate.z));
        g.setColor(Color.ORANGE);
        g.draw(new Ellipse2D.Double(stop.x, stop.z, 3, 3));
        g.setColor(Color.red);
        double r = 5;
        g.fill(new Ellipse2D.Double(player.getPos().x - r, player.getPos().z - r, 2 * r, 2 * r));
        g.fill(new Ellipse2D.Double(direction.x - r, direction.z - r, 2 * r, 2 * r));
        g.setColor(Color.GREEN);
        g.fill(new Ellipse2D.Double(center.x - r, center.z - r, 2 * r, 2 * r));
        g.setColor(Color.BLUE);
        g.fill(new Ellipse2D.Double(translate.x - r, translate.z - r, 2 * r, 2 * r));
        renderOther(g);
    }

    /**
     * @return the player
     */
    public MovePlayer getPlayer() {
        return player;
    }

    public NodeNavMesh getMap() {
        return player.getMap();
    }

    /**
     * 改变随机点是否显示
     */
    public void changeShowRandomPoint() {
        isRenderRandomPoints = !isRenderRandomPoints;
    }

    /**
     * 改变行走层渲染
     */
    public void changeShowXingZouCeng() {
        isRenderXingZouCeng = !isRenderXingZouCeng;
    }
}
