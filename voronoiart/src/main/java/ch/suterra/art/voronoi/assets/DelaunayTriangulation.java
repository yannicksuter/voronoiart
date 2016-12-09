package ch.suterra.art.voronoi.assets;

import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import java.util.*;

/**
 * Created by yannick on 09.12.16.
 */
public class DelaunayTriangulation {
	private TriangleArray m_shapeData;
	private Appearance m_appearance;
	private BranchGroup m_bg = new BranchGroup();
	private ArrayList<Triangle> m_triangles;
	private int triangleId = 0;

	public DelaunayTriangulation(PointCloud points) {
		Assert.that(points.size() >= 3, "Minimal number of points to triangulate is 3.");
		m_triangles = new ArrayList<Triangle>();
		// create initial triangle
		m_triangles.add(new Triangle(triangleId++, points.get(0), points.get(1), points.get(2)));
		System.out.printf("add: %s\n", m_triangles.get(0).toString());
		// add all additional points restructure the triangulation for cavities
		for (int i=3; i<points.size(); i++) {
			addPoint(i, points);
		}
	}

	private void addPoint(int nextId, PointCloud points) {
		int id = 0;
		Point3d pt = points.get(nextId);
//		System.out.printf("new point: %d / (%.5f, %.5f, %.5f)\n", nextId, pt.x, pt.y, pt.z);
		System.out.printf("new point: %d / (%.5f, %.5f)\n", nextId, pt.x, pt.y);
		Triangle tConflict = locate(points.get(nextId));
		if (tConflict != null) {
			System.out.printf("del: %s\n", tConflict.toString());
			m_triangles.remove(tConflict);
			id = tConflict.m_id;
		}
		// generate new triangles
		ArrayList<Triangle> queue = new ArrayList<Triangle>();
		List<Set<Integer>> res = Permutation.createPermutations(nextId);
		for (Set<Integer> s : res) {
			Integer[] IDs = s.toArray(new Integer[s.size()]);
			Triangle t = new Triangle(triangleId++, points.get(IDs[0]), points.get(IDs[1]), points.get(nextId));
//			if (locate(points.get(nextId)) == null) {
			if (!checkInclusion(t, points, s)) {
				System.out.printf("add: %s\n", t.toString());
				queue.add(t);
			}
		}
		if (id == 2) {
			m_triangles.clear();
		}
		m_triangles.addAll(queue);
	}

	public boolean checkInclusion(Triangle t, PointCloud points, Set<Integer> IDs) {
		for (int i=0; i<points.size(); i++) {
			if (!IDs.contains(i) && t.m_circumpherence.inside(points.get(i))) {
				return true;
			}
		}
		return false;
	}

	private Triangle locate(Point3d p) {
		for (Triangle t : m_triangles) {
			if (t.m_circumpherence.inside(p)) {
				return t;
			}
		}
		return null;
	}

	public Node toNode(boolean showCircumpherences) {
		m_shapeData = new TriangleArray(m_triangles.size()*3, TriangleArray.COORDINATES|TriangleArray.COLOR_4);
		for (int i=0; i<m_triangles.size(); i++) {
			int ii = i*3;
			m_shapeData.setCoordinate(ii+0, m_triangles.get(i).m_p1);
			m_shapeData.setColor(ii+0, new Color4f(1,0,0,.5f));
			m_shapeData.setCoordinate(ii+1, m_triangles.get(i).m_p2);
			m_shapeData.setColor(i*3+1, new Color4f(1,0,0,.5f));
			m_shapeData.setCoordinate(ii+2, m_triangles.get(i).m_p3);
			m_shapeData.setColor(ii+2, new Color4f(1,0,0,.5f));
		}

		// create an appearance
		m_appearance = new Appearance();
		PolygonAttributes polyAttrbutes = new PolygonAttributes();
		polyAttrbutes.setPolygonMode(PolygonAttributes.POLYGON_LINE);
		polyAttrbutes.setCullFace(PolygonAttributes.CULL_NONE);
		m_appearance.setPolygonAttributes(polyAttrbutes);

		Shape3D pointShape = new Shape3D(m_shapeData, m_appearance);
		m_bg.addChild(pointShape);

		if (showCircumpherences) {
			// add circumpherences
			for (Triangle t : m_triangles) {
				m_bg.addChild(t.m_circumpherence.toNode());
			}
		}
		return m_bg;
	}
}
