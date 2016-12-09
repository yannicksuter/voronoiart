package ch.suterra.art.voronoi.assets;

import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by yannick on 28/01/16.
 */
public class BoundingVolume {
	private double m_sizeX, m_sizeY, m_sizeZ;
	private BoundingBox m_boundingBox;
	private QuadArray m_shapeData;
	private Appearance m_appearance;
	private BranchGroup m_bg = new BranchGroup();

	public static BoundingVolume createCube(double size) {
		return new BoundingVolume(size, 1, 1, 1, true);
	}

	public BoundingVolume(double sizeX, double sizeY, double sizeZ, int lineSize, boolean aliased) {
		m_sizeX = sizeX;
		double coordX = m_sizeX / 2f;
		m_sizeY = sizeY;
		double coordY = m_sizeY / 2f;
		m_sizeZ = sizeZ;
		double coordZ = m_sizeZ / 2f;
		m_boundingBox = new BoundingBox(new Point3d(-coordX,-coordX,-coordX), new Point3d(coordX,coordX,coordX));

		Point3d[] shapeCoordinates = {
				new Point3d(-coordX,  coordY,  coordZ),
				new Point3d( coordX,  coordY,  coordZ),
				new Point3d( coordX,  coordY, -coordZ),
				new Point3d(-coordX,  coordY, -coordZ),

				new Point3d(-coordX, -coordY,  coordZ),
				new Point3d( coordX, -coordY,  coordZ),
				new Point3d( coordX, -coordY, -coordZ),
				new Point3d(-coordX, -coordY, -coordZ),

				new Point3d(-coordX,  coordY,  coordZ),
				new Point3d(-coordX, -coordY,  coordZ),
				new Point3d( coordX, -coordY,  coordZ),
				new Point3d( coordX,  coordY,  coordZ),

				new Point3d( coordX,  coordY, -coordZ),
				new Point3d(-coordX,  coordY, -coordZ),
				new Point3d(-coordX, -coordY, -coordZ),
				new Point3d( coordX, -coordY, -coordZ),

				new Point3d(-coordX,  coordY,  coordZ),
				new Point3d(-coordX,  coordY, -coordZ),
				new Point3d(-coordX, -coordY, -coordZ),
				new Point3d(-coordX, -coordY,  coordZ),

				new Point3d(coordX,  coordY,  coordZ),
				new Point3d(coordX,  coordY, -coordZ),
				new Point3d(coordX, -coordY, -coordZ),
				new Point3d(coordX, -coordY,  coordZ),
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

	public Point3d getPointInVolume() {
		Point3d pt = new Point3d(
				ThreadLocalRandom.current().nextDouble(-m_sizeX / 2f, m_sizeX / 2f),
				ThreadLocalRandom.current().nextDouble(-m_sizeY / 2f, m_sizeY / 2f),
				ThreadLocalRandom.current().nextDouble(-m_sizeZ / 2f, m_sizeZ / 2f));
		Assert.that(m_boundingBox.intersect(pt), "Newly generated point is OUTSIDE volume.");
		return pt;
	}

	public Node toNode() {
		return m_bg;
	}
}
