package ch.suterra.art.voronoi.assets;

import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yannick on 28/01/16.
 */
public class PointCloud {
	private int m_count;
	private ArrayList<Point3d> m_points;
	private PointArray m_pointArray;
	private Appearance m_pointAppearance;
	private BranchGroup m_bg = new BranchGroup();

	public static PointCloud create(int count, boolean partitionedDistribution, BoundingVolume boundingVolume, boolean addBoundingEdges) {
		return new PointCloud(count, partitionedDistribution, boundingVolume, 3, addBoundingEdges, true);
	}

	public PointCloud(int count, boolean partitionedDistribution, BoundingVolume boundingVolume, int pointSize, boolean addBoundingEdges, boolean aliased) {
		Assert.that(boundingVolume != null, "Bounding volume needed!");

		m_points = new ArrayList<Point3d>();
		for (int i=0; i < count; i++) {
			m_points.add(boundingVolume.getPointInVolume(partitionedDistribution));
		}

		if (addBoundingEdges) {
			m_points.addAll(boundingVolume.getEdgeVertices());
		}

		m_count = m_points.size();
		m_pointArray = new PointArray(m_count, PointArray.COORDINATES | PointArray.COLOR_3);
		for (int id = 0; id < m_points.size(); id++) {
			Point3d pt = m_points.get(id);
			m_pointArray.setCoordinate(id, pt);
			m_pointArray.setColor(id, new Color3f(1, 1, 1));
		}

		m_pointArray.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
		m_pointArray.setCapability(GeometryArray.ALLOW_COORDINATE_WRITE);
		m_pointArray.setCapability(GeometryArray.ALLOW_COUNT_WRITE);
		m_pointArray.setCapability(GeometryArray.ALLOW_COUNT_READ);
		m_pointArray.setCapability(GeometryArray.ALLOW_INTERSECT);

		m_pointAppearance = new Appearance();
		m_pointAppearance.setPointAttributes(new PointAttributes(pointSize, aliased));

		Shape3D pointShape = new Shape3D(m_pointArray, m_pointAppearance);
		m_bg.addChild(pointShape);
	}

	public Node toNode() {
		return m_bg;
	}

	public Point3d get(int i) {
		return m_points.get(i);
	}

	public List<Point3d> getPoints() {
		return m_points;
	}

	public int size() {
		return m_points.size();
	}
}
