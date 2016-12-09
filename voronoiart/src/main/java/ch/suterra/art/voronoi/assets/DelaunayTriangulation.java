package ch.suterra.art.voronoi.assets;

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
	private ArrayList<Triangle> m_triangles = new ArrayList<Triangle>();
	private int triangleId = 0;

	public DelaunayTriangulation(PointCloud points) {
		// create initial triangle
		m_triangles.add(new Triangle(triangleId++, points.get(0), points.get(1), points.get(2)));
		for (int i=3; i<points.size(); i++) {
			addPoint(i, points);
		}
	}

	private void addPoint(int nextId, PointCloud points) {
		ArrayList<Triangle> tConflict = locate(points.get(nextId));
		if (tConflict.size() > 0) {
			m_triangles.removeAll(tConflict);
		}
		// generate new triangles
		ArrayList<Triangle> queue = new ArrayList<Triangle>();
		List<Set<Integer>> res = Permutation.createPermutations(nextId);
		for (Set<Integer> s : res) {
			s.add(nextId);
			Integer[] IDs = s.toArray(new Integer[s.size()]);
			Triangle t = new Triangle(triangleId++, points.get(IDs[0]), points.get(IDs[1]), points.get(IDs[2]));
			if (!checkInclusion(t, points, s)) {
				queue.add(t);
			}
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

	private ArrayList<Triangle> locate(Point3d p) {
		ArrayList<Triangle> queue = new ArrayList<Triangle>();
		for (Triangle t : m_triangles) {
			if (t.m_circumpherence.inside(p)) {
				queue.add(t);
			}
		}
		return queue;
	}

	public Node toNode(boolean showCircumpherences) {
		if (m_triangles.size() == 0) {
			return null;
		}

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

	public int size() {
		return m_triangles.size();
	}
}
