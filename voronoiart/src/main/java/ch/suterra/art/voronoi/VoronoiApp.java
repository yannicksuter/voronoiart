package ch.suterra.art.voronoi;

import ch.suterra.art.voronoi.assets.BoundingVolume;
import ch.suterra.art.voronoi.assets.DelaunayTriangulation;
import ch.suterra.art.voronoi.assets.PointCloud;
import ch.suterra.art.voronoi.assets.Triangle;
import ch.suterra.art.voronoi.ui.GlobalKeyDispatcher;
import ch.suterra.art.voronoi.ui.VoronoiConfig;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yannick on 21/01/16.
 */
public class VoronoiApp extends JFrame {
	private Canvas3D m_canvas;
	private SimpleUniverse m_universe;
	private BranchGroup m_objRoot;
	private TransformGroup m_objTransform;

	private long m_seed;
	private BoundingVolume m_boundingVolume;
	private PointCloud m_pointCloud;
	private DelaunayTriangulation m_triangles;

	private int m_particleCount = 10;
	private boolean m_partitionedDistribution = true;

	public VoronoiApp() throws IOException {
		configureWindow();
		configureCanvas();
		conigureUniverse();

		GlobalKeyDispatcher.initialize();

		generateSeed(null);
		generateGraph();
	}

	private void configureWindow() {
		setTitle(String.format("Voronoi3D"));
		setSize(1024, 768);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int locationX = (screenSize.width - getWidth()) / 2;
		int locationY = (screenSize.height - getHeight()) / 2;
		setLocation(locationX, locationY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void configureCanvas() {
		GraphicsConfigTemplate3D gct3D = new GraphicsConfigTemplate3D();
		gct3D.setSceneAntialiasing(GraphicsConfigTemplate3D.REQUIRED);
		GraphicsConfiguration gc = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gct3D);
		m_canvas = new Canvas3D(gc);

		System.out.printf("cv.getSceneAntialiasingAvailable(): %s\n", m_canvas.getSceneAntialiasingAvailable());
		System.out.printf("cv.queryProperties().get(\"sceneAntialiasingNumPasses\"): %s\n", m_canvas.queryProperties().get("sceneAntialiasingNumPasses"));

		getContentPane().add(m_canvas, BorderLayout.CENTER);
		getContentPane().add(new VoronoiConfig(this), BorderLayout.EAST);
	}

	private void conigureUniverse() {
		m_universe = new SimpleUniverse(m_canvas);
		m_universe.getViewingPlatform().setNominalViewingTransform();
		m_canvas.getView().setSceneAntialiasingEnable(true);
	}

	public void createSceneGraph() {
		m_objRoot = new BranchGroup();
		m_objRoot.setCapability(BranchGroup.ALLOW_DETACH);

		m_objTransform = new TransformGroup();
		m_objTransform.setCapability(Group.ALLOW_CHILDREN_EXTEND);
		m_objTransform.setCapability(Group.ALLOW_CHILDREN_WRITE);
		m_objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		m_objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		m_objRoot.addChild(m_objTransform);

		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(m_objTransform);
		MouseTranslate myMouseTranslate = new MouseTranslate();
		myMouseTranslate.setTransformGroup(m_objTransform);
		MouseZoom myMouseZoom = new MouseZoom();
		myMouseZoom.setTransformGroup(m_objTransform);

		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(1.0);
		myMouseRotate.setSchedulingBounds(bounds);
		myMouseTranslate.setSchedulingBounds(bounds);
		myMouseZoom.setSchedulingBounds(bounds);

		Transform3D transform = new Transform3D();
		transform.setScale(1);
		m_objTransform.setTransform(transform);

		m_objRoot.addChild(myMouseRotate);
		m_objRoot.addChild(myMouseTranslate);
		m_objRoot.addChild(myMouseZoom);

		Background bg = new Background(new Color3f(.5f, .5f, .5f));
		BoundingSphere boundsBG = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		bg.setApplicationBounds(boundsBG);
		m_objRoot.addChild(bg);
	}

	public void generateSeed(Long s) {
		if (s != null) {
			m_seed = s;
		} else {
			m_seed = System.currentTimeMillis() % 10000;
		}
		setTitle(String.format("Voronoi3D [seed:%d]", m_seed));
	}

	public int getParticleCount() {
		return m_particleCount;
	}

	public void setParticleCount(int count) {
		m_particleCount = count;
	}

	public void generateGraph() {
		if (m_objRoot != null) {
			m_objRoot.detach();
		}
		createSceneGraph();

		// setup content
		m_boundingVolume = BoundingVolume.createCube(1, m_seed);
		m_pointCloud = PointCloud.create(m_particleCount, m_partitionedDistribution, m_boundingVolume, true);

		m_objTransform.addChild(m_boundingVolume.toNode());
		m_objTransform.addChild(m_pointCloud.toNode());

		long startTime = System.currentTimeMillis();
		m_triangles = new DelaunayTriangulation(m_pointCloud);

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.printf("time: %dms\n", elapsedTime);

		System.out.printf("Triangles generated: %d\n", m_triangles.size());
		m_objTransform.addChild(m_triangles.toNode(false));

		m_universe.addBranchGraph(m_objRoot);
	}

	public void export() {
		try {
			writeSCAD(m_triangles, m_pointCloud, m_seed, "result");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writePoint(BufferedWriter out, Point3d point, float scale, int radius, int resolution) throws IOException {
		out.write(String.format("translate([%f,%f,%f])", point.x*scale, point.y*scale, point.z*scale));
		out.write("\n\t");
		out.write(String.format("sphere(r = %d, center = true, $fn = %d);\n", radius, resolution));
	}

	private void writeLine(BufferedWriter out, Point3d p0, Point3d p1, float scale, int radius, int resolution) throws IOException {
		Vector3d dir = new Vector3d(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);

		double r = dir.length();
		double alpha = Math.acos(dir.y/r);
		double beta = Math.atan(dir.x / dir.z);
		if (dir.z < 0) {
			beta += Math.PI;
		}

		out.write(String.format("translate([%f,%f,%f])", p0.x * scale, p0.y * scale, p0.z * scale));
		out.write("\n\t");
		out.write(String.format("rotate([%f,%f,%f])", -90 + Math.toDegrees(alpha), Math.toDegrees(beta), 0.f));
		out.write("\n\t");
		out.write(String.format("cylinder(h = %f, r1 = %d, r2 = %d, center = false, $fn = %d);\n", r*scale, radius, radius, resolution));
	}

	private void writeSCAD(DelaunayTriangulation triangles, PointCloud points, long seed, String namePrefix) throws IOException {
		BufferedWriter out = null;
		try {
//			int nameSuffix = (int) ((new java.util.Date()).getTime()/1000);
			int nameSuffix = 0;
			String home = System.getProperty("user.home");
			FileWriter fstream = new FileWriter(home + "/" + namePrefix + String.valueOf(nameSuffix) + ".scad", false);
			out = new BufferedWriter(fstream);

			int radius = 1;
			int resolution = 10;
			float scale = 100;

			for (int i=0; i<points.size(); i++) {
				Point3d point = points.get(i);
				writePoint(out, point, scale, radius, resolution);
			}

			int lines = 0;
			Set<String> index = new HashSet<String>();
			for (int i = 0; i < triangles.size(); i++) {
				Triangle triangle = triangles.get(i);
				if (!index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id2)) && !index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id1))) {
					writeLine(out, triangle.m_p1, triangle.m_p2, scale, radius, resolution);
					index.add(String.format("%d-%d", triangle.m_id1, triangle.m_id2));
					lines++;
				}
				if (!index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id3)) && !index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id2))) {
					writeLine(out, triangle.m_p2, triangle.m_p3, scale, radius, resolution);
					index.add(String.format("%d-%d", triangle.m_id2, triangle.m_id3));
					lines++;
				}
				if (!index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id1)) && !index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id3))) {
					writeLine(out, triangle.m_p3, triangle.m_p1, scale, radius, resolution);
					index.add(String.format("%d-%d", triangle.m_id3, triangle.m_id1));
					lines++;
				}
			}
			System.out.println(String.format("%d lines exported.", lines));
		}
		catch (IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		finally {
			if (out != null) {
				out.close();
			}
		}
	}

	// MAIN ********************************************************************

	public static void main(String[] args) {
		try {
			new VoronoiApp().setVisible(true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}