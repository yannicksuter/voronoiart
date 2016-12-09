package ch.suterra.art.voronoi.assets;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

/**
 * Created by yannick on 28/01/16.
 */
public class PointCloud {
	private int m_count;
	private PointArray m_pointArray;
	private Appearance m_pointAppearance;
	private BranchGroup m_bg = new BranchGroup();

	public static PointCloud create(int count, BoundingVolume boundingVolume) {
		return new PointCloud(count, 2, true);
	}

	public PointCloud(int count, int pointSize, boolean aliased) {
		m_count = count;
		m_pointArray = new PointArray(m_count, PointArray.COORDINATES | PointArray.COLOR_3);
		for (int i=0; i < m_count; i++) {
			m_pointArray.setCoordinate(i, new Point3d(0,0,0));
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
}
