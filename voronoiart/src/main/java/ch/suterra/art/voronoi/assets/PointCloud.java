package ch.suterra.art.voronoi.assets;

import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by yannick on 28/01/16.
 */
public class PointCloud {
	private int m_count;
	private ArrayList<Point3d> m_points;
	private PointArray m_pointArray;
	private Appearance m_pointAppearance;
	private BranchGroup m_bg = new BranchGroup();
	private Random m_random = new Random();

	public static PointCloud create(int count, BoundingVolume boundingVolume) {
		return new PointCloud(count, boundingVolume, 3, true);
	}

	private double nextDouble(double rangeMin, double rangeMax) {
		return (rangeMin + (rangeMax - rangeMin) * m_random.nextDouble());
	}

	public PointCloud(int count, BoundingVolume boundingVolume, int pointSize, boolean aliased) {
		Assert.that(boundingVolume != null, "Bounding volume needed!");
		m_count = count;
		m_points = new ArrayList<Point3d>();
		m_random.setSeed(12345);

		m_pointArray = new PointArray(m_count, PointArray.COORDINATES | PointArray.COLOR_3);
		for (int i=0; i < m_count; i++) {
			Point3d pt = boundingVolume.getPointInVolume();
			m_points.add(i, pt);
			m_pointArray.setCoordinate(i, pt);
			m_pointArray.setColor(i, new Color3f(1,1,1));
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
