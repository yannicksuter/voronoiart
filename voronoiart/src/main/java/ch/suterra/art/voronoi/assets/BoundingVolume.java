package ch.suterra.art.voronoi.assets;

import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by yannick on 28/01/16.
 */
public class BoundingVolume {
	private double m_sizeX, m_sizeY, m_sizeZ;
	private BoundingBox m_boundingBox;
	private QuadArray m_shapeData;
	private Appearance m_appearance;
	private BranchGroup m_bg = new BranchGroup();
	private Random m_random = new Random();

	private int m_partitionIndex = 0;
	Point3d[] m_partitions = {
			new Point3d(-1, 1, 1),
			new Point3d(1, 1, 1),
			new Point3d(1, 1, -1),
			new Point3d(-1, 1, -1),

			new Point3d(-1, -1, 1),
			new Point3d(1, -1, 1),
			new Point3d(1, -1, -1),
			new Point3d(-1, -1, -1),
	};

	public static BoundingVolume createCube(double size, long seed) {
		return new BoundingVolume(size, 1, 1, 1, true, seed);
	}

	public BoundingVolume(double sizeX, double sizeY, double sizeZ, int lineSize, boolean aliased, long seed) {
		m_random.setSeed(seed);
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

	private double nextDouble(double rangeMin, double rangeMax) {
		return (rangeMin + (rangeMax - rangeMin) * m_random.nextDouble());
	}

	private double nextPartitionedDouble(double rangeMin, double rangeMax, double partitionSide) {
		// -X .. X -> -X .. 0 || 0 .. X
		if (partitionSide == -1) {
			rangeMax = 0;
		} else {
			rangeMin = 0;
		}
		return (rangeMin + (rangeMax - rangeMin) * m_random.nextDouble());
	}

	public Point3d getPointInVolume(boolean partitionedDistribution) {
		Point3d pt = null;
		if (partitionedDistribution) {
			pt = new Point3d(
					nextPartitionedDouble(-m_sizeX / 2f, m_sizeX / 2f, m_partitions[m_partitionIndex].x),
					nextPartitionedDouble(-m_sizeY / 2f, m_sizeY / 2f, m_partitions[m_partitionIndex].y),
					nextPartitionedDouble(-m_sizeZ / 2f, m_sizeZ / 2f, m_partitions[m_partitionIndex].z));
			m_partitionIndex = (m_partitionIndex+1) % m_partitions.length;
		} else {
			pt = new Point3d(
				nextDouble(-m_sizeX / 2f, m_sizeX / 2f),
				nextDouble(-m_sizeY / 2f, m_sizeY / 2f),
				nextDouble(-m_sizeZ / 2f, m_sizeZ / 2f));
		}
		Assert.that(m_boundingBox.intersect(pt), "Newly generated point is OUTSIDE volume.");
		return pt;
	}

	public Node toNode() {
		return m_bg;
	}

	public List<Point3d> getEdgeVertices() {
		double coordX = m_sizeX / 2f;
		double coordY = m_sizeY / 2f;
		double coordZ = m_sizeZ / 2f;
		Point3d[] shapeEdges = {
			new Point3d(-coordX, coordY, coordZ),
			new Point3d(coordX, coordY, coordZ),
			new Point3d(coordX, coordY, -coordZ),
			new Point3d(-coordX, coordY, -coordZ),

			new Point3d(-coordX, -coordY, coordZ),
			new Point3d(coordX, -coordY, coordZ),
			new Point3d(coordX, -coordY, -coordZ),
			new Point3d(-coordX, -coordY, -coordZ),
		};

		return Arrays.asList(shapeEdges);
	}
}
