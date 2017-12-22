package com.jzy.game.ai.nav.node;

import java.io.Serializable;
import java.util.ArrayList;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 多边形转换器，KPolygon和Polygon进行转换
 * 
 * @author JiangZhiYong
 * @date 2017年12月3日
 * @mail 359135103@qq.com
 */
public class PolygonConverter implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    private GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * KPolygon 转 Polygon
     * 
     * @param kpolygon
     * @return
     */
    public Polygon makePolygonFrom(KPolygon kpolygon) {
        Polygon polygon;
        Coordinate[] coordinateArray = new Coordinate[kpolygon.getPoints().size() + 1];
        for (int i = 0; i < kpolygon.getPoints().size(); i++) {
            Vector3 p = kpolygon.getPoints().get(i);
            coordinateArray[i] = new Coordinate(p.x, p.z, p.y);
        }
        // link the first and last points
        coordinateArray[kpolygon.getPoints().size()] = new Coordinate(coordinateArray[0].x, coordinateArray[0].y, coordinateArray[0].z);
        LinearRing linearRing = geometryFactory.createLinearRing(coordinateArray);
        polygon = new Polygon(linearRing, null, geometryFactory);
        return polygon;
    }
    
    /**
     *  Polygon 外边圈 转KPolygon
     *
     * @param polygon
     * @return
     */
    public KPolygon makeKPolygonFromExterior(Polygon polygon) {
        LineString exteriorRingLineString = polygon.getExteriorRing();
        KPolygon kpolygon = makeKPolygonFrom(exteriorRingLineString);
        return kpolygon;
    }
    
    /**
     *  生成KPolygon
     * @param lineString
     * @return
     */
    public KPolygon makeKPolygonFrom(com.vividsolutions.jts.geom.LineString lineString) {
        CoordinateSequence coordinateSequence = lineString.getCoordinateSequence();
        ArrayList<Vector3> points = new ArrayList<>();
        // The loop stops at the second-last coord since the last coord will be
        // the same as the start coord.
        Vector3 lastAddedPoint = null;
        for (int i = 0; i < coordinateSequence.size() - 1; i++) {
            Coordinate coord = coordinateSequence.getCoordinate(i);
            Vector3 p = new Vector3(coord.x, coord.z, coord.y);
            if (lastAddedPoint != null && p.x == lastAddedPoint.x && p.z == lastAddedPoint.z) {
                // Don't add the point since it's the same as the last one
                continue;
            } else {
                points.add(p);
                lastAddedPoint = p;
            }
        }
        if (points.size() < 3) {
            return null;
        }
        KPolygon polygon = new KPolygon(points);
        return polygon;
    }
}
