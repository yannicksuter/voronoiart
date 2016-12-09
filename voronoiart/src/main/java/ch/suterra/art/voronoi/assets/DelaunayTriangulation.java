package ch.suterra.art.voronoi.assets;

import com.sun.j3d.utils.geometry.Sphere;
import sun.jvm.hotspot.utilities.Assert;

import javax.media.j3d.*;
import javax.vecmath.Color4f;
import javax.vecmath.Vector3f;
import java.util.ArrayList;

/**
 * Created by yannick on 09.12.16.
 */
public class DelaunayTriangulation {
	private TriangleArray m_shapeData;
	private Appearance m_appearance;
	private BranchGroup m_bg = new BranchGroup();
	private ArrayList<Triangle> m_triangles;

	public DelaunayTriangulation(PointCloud points) {
		Assert.that(points.size() == 3, "Not more than 3 points supported yet.");
		m_triangles = new ArrayList<Triangle>();
		Triangle t = new Triangle(points.get(0), points.get(1), points.get(2));
		m_triangles.add(t);

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
		m_bg.addChild(showCircle(t.m_circumcenter));
	}

	public Node showCircle(Circle c) {
		Sphere sphere = new Sphere((float)c.m_radius, 1, 56);

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
		Vector3f vector = new Vector3f( c.m_center);
		transform.setTranslation(vector);
		tg.setTransform(transform);
		tg.addChild(sphere);
		return tg;
	}

	public Node toNode() {
		return m_bg;
	}
}
