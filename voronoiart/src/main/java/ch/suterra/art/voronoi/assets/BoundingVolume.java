package ch.suterra.art.voronoi.assets;

import javax.media.j3d.*;
import javax.vecmath.Point3d;

/**
 * Created by yannick on 28/01/16.
 */
public class BoundingVolume {
	private double m_size;
	private BoundingBox m_boundingBox;
	private QuadArray m_shapeData;
	private Appearance m_appearance;
	private BranchGroup m_bg = new BranchGroup();

	public static BoundingVolume createCube(double size) {
		return new BoundingVolume(size, 1, true);
	}

	public BoundingVolume(double size, int lineSize, boolean aliased) {
		m_size = size;
		double coord = size / 2f;
		m_boundingBox = new BoundingBox(new Point3d(coord,coord,coord), new Point3d(-coord,-coord,-coord));

		Point3d[] shapeCoordinates = {
				new Point3d(-1.0,  1.0,  1.0),
				new Point3d( 1.0,  1.0,  1.0),
				new Point3d( 1.0,  1.0, -1.0),
				new Point3d(-1.0,  1.0, -1.0),

				new Point3d(-1.0, -1.0,  1.0),
				new Point3d( 1.0, -1.0,  1.0),
				new Point3d( 1.0, -1.0, -1.0),
				new Point3d(-1.0, -1.0, -1.0),

				new Point3d(-1.0,  1.0,  1.0),
				new Point3d(-1.0, -1.0,  1.0),
				new Point3d( 1.0, -1.0,  1.0),
				new Point3d( 1.0,  1.0,  1.0),

				new Point3d( 1.0,  1.0, -1.0),
				new Point3d(-1.0,  1.0, -1.0),
				new Point3d(-1.0, -1.0, -1.0),
				new Point3d( 1.0, -1.0, -1.0),

				new Point3d(-1.0,  1.0,  1.0),
				new Point3d(-1.0,  1.0, -1.0),
				new Point3d(-1.0, -1.0, -1.0),
				new Point3d(-1.0, -1.0,  1.0),

				new Point3d(1.0,  1.0,  1.0),
				new Point3d(1.0,  1.0, -1.0),
				new Point3d(1.0, -1.0, -1.0),
				new Point3d(1.0, -1.0,  1.0),
		};

		m_shapeData = new QuadArray(24, QuadArray.COORDINATES);
		m_shapeData.setCoordinates(0, shapeCoordinates);

		// create an appearance
		m_appearance = new Appearance();
		PolygonAttributes polyAttrbutes = new PolygonAttributes();
		polyAttrbutes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		polyAttrbutes.setCullFace(PolygonAttributes.CULL_NONE);
		m_appearance.setPolygonAttributes(polyAttrbutes);

		Shape3D pointShape = new Shape3D(m_shapeData, m_appearance);
		m_bg.addChild(pointShape);
	}

	public Node toNode() {
		return m_bg;
	}
}
