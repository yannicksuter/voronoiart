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
					m_sizeX == 0 ? m_sizeX : nextPartitionedDouble(-m_sizeX / 2f, m_sizeX / 2f, m_partitions[m_partitionIndex].x),
					m_sizeY == 0 ? m_sizeY : nextPartitionedDouble(-m_sizeY / 2f, m_sizeY / 2f, m_partitions[m_partitionIndex].y),
					m_sizeZ == 0 ? m_sizeZ : nextPartitionedDouble(-m_sizeZ / 2f, m_sizeZ / 2f, m_partitions[m_partitionIndex].z));
			m_partitionIndex = (m_partitionIndex+1) % m_partitions.length;
		} else {
			pt = new Point3d(
					m_sizeX == 0 ? m_sizeX : nextDouble(-m_sizeX / 2f, m_sizeX / 2f),
					m_sizeY == 0 ? m_sizeY : nextDouble(-m_sizeY / 2f, m_sizeY / 2f),
					m_sizeZ == 0 ? m_sizeZ : nextDouble(-m_sizeZ / 2f, m_sizeZ / 2f));
		}
		return pt;
	}

	public List<Point3d> getEdgeVertices() {
		double coordX = m_sizeX / 2f;
		double coordY = m_sizeY / 2f;
		double coordZ = m_sizeZ / 2f;

		if (m_sizeX == 0) {
			Point3d[] shapeEdges = {
					new Point3d(coordX, coordY, coordZ),
					new Point3d(coordX, coordY, -coordZ),
					new Point3d(coordX, -coordY, coordZ),
					new Point3d(coordX, -coordY, -coordZ),
			};
			return Arrays.asList(shapeEdges);
		}
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
