package ch.suterra.art.voronoi.assets;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Created by yannick on 09.12.16.
 */
public class Triangle {
	public int m_id;
	public Circle m_circumpherence = null;
	public int m_id1;
	public Point3d m_p1;
	public int m_id2;
	public Point3d m_p2;
	public int m_id3;
	public Point3d m_p3;

	// neighbours
	public Triangle m_n1; // P1-P2
	public Triangle m_n2; // P2-P3
	public Triangle m_n3; // P3-P1

	public Triangle(int triangleId, int id1, Point3d p1, int id2, Point3d p2, int id3, Point3d p3) {
		m_id = triangleId;
		m_id1 = id1;
		m_id2 = id2;
		m_id3 = id3;
		m_p1 = p1;
		m_p2 = p2;
		m_p3 = p3;
		m_circumpherence = GetCircumcenter(p1, p2, p3);
	}

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

	@Override
	public String toString () {
		return String.format("Triangle: (%.5f, %.5f, %.5f) / (%.5f, %.5f, %.5f) / (%.5f, %.5f, %.5f)",
				m_p1.x, m_p1.y, m_p1.z,
				m_p2.x, m_p2.y, m_p2.z,
				m_p3.x, m_p3.y, m_p3.z);
	}
}

