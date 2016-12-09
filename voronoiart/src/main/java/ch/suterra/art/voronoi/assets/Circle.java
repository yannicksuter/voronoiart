package ch.suterra.art.voronoi.assets;

import javax.vecmath.Point3d;

/**
 * Created by yannick on 09.12.16.
 */
public class Circle {
	public Point3d m_center;
	public double m_radius;

	public Circle(Point3d center, double radius) {
		m_radius = radius;
		m_center = center;
	}
}
