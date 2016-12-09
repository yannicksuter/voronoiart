package ch.suterra.art.voronoi.assets;

import com.sun.j3d.utils.geometry.Sphere;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

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

	public boolean inside(Point3d p) {
		Vector3d d = new Vector3d(p.x - m_center.x, p.y - m_center.y, p.z - m_center.z);
		return d.length() <= m_radius;
	}

	public Node toNode() {
		Sphere sphere = new Sphere((float)m_radius, 1, 56);

		Appearance app = new Appearance();
		Material mat = new Material();
		mat.setDiffuseColor(1,1,0,.5f);
		app.setMaterial(mat);

		PolygonAttributes polyAttrbutes = new PolygonAttributes();
		polyAttrbutes.setCullFace(PolygonAttributes.CULL_FRONT);
		app.setPolygonAttributes(polyAttrbutes);

		TransparencyAttributes ta = new TransparencyAttributes( );
		ta.setTransparency( 0.75f );
		ta.setTransparencyMode( TransparencyAttributes.BLENDED );
		app.setTransparencyAttributes( ta );
		sphere.setAppearance(app);

		TransformGroup tg = new TransformGroup();
		Transform3D transform = new Transform3D();
		Vector3f vector = new Vector3f(m_center);
		transform.setTranslation(vector);
		tg.setTransform(transform);
		tg.addChild(sphere);
		return tg;
	}
}
