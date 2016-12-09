package ch.suterra.art.voronoi.assets;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Created by yannick on 09.12.16.
 */
class Triangle {
	public Circle m_circumcenter = null;
	public Point3d m_p1;
	public Point3d m_p2;
	public Point3d m_p3;

	public Triangle(Point3d p1, Point3d p2, Point3d p3) {
		m_p1 = p1;
		m_p2 = p2;
		m_p3 = p3;
		m_circumcenter = GetCircumcenter(p1, p2, p3);
	}

//	static public Point3d Midpoint(Point3d a, Point3d b) {
//		return new Point3d(
//				(a.x + b.x) / 2,
//				(a.y + b.y) / 2,
//				(a.z + b.z) / 2)
//		);
//	}

//	ac = c - a ;
//	ab = b - a ;
//	abXac = ab.cross( ac ) ;
//	toCircumsphereCenter = (abXac.cross( ab )*ac.len2() + ac.cross( abXac )*ab.len2()) / (2.f*abXac.len2()) ;
//	circumsphereRadius = toCircumsphereCenter.len() ;
//	ccs = a  +  toCircumsphereCenter ; // now this is the actual 3space location
	static public Circle GetCircumcenter(Point3d p1, Point3d p2, Point3d p3) {
		Vector3d ac = new Vector3d(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
		Vector3d ab = new Vector3d(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
		Vector3d abXac = new Vector3d();
		abXac.cross(ab, ac);

		Vector3d abXacXab = new Vector3d();
		abXacXab.cross(abXac, ab);
		abXacXab.scale(ac.lengthSquared());

		Vector3d acXabXac = new Vector3d();
		acXabXac.cross(ac, abXac);
		acXabXac.scale(ab.lengthSquared());
		double d = 2*abXac.lengthSquared();

		Vector3d toCircumsphereCenter = new Vector3d(
				(abXacXab.x + acXabXac.x) / d,
				(abXacXab.y + acXabXac.y) / d,
				(abXacXab.z + acXabXac.z) / d
			);
		double circumsphereRadius = toCircumsphereCenter.length();
		Point3d circumsphereCenter = new Point3d(
				p1.x + toCircumsphereCenter.x,
				p1.y + toCircumsphereCenter.y,
				p1.z + toCircumsphereCenter.z
				);

		return new Circle(circumsphereCenter, circumsphereRadius);
	}
}

