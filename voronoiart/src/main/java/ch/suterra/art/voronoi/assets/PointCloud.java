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
	private ArrayList<Point3d> m_points = new ArrayList<Point3d>();;
	private BranchGroup m_bg = new BranchGroup();

	public static PointCloud create(int count, boolean partitionedDistribution, BoundingVolume boundingVolume, boolean addBoundingEdges) {
		return new PointCloud(count, partitionedDistribution, boundingVolume, 3, addBoundingEdges, true);
	}

	public PointCloud() {
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
	}

	public void add(Point3d p) {
		m_points.add(p);
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
