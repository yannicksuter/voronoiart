package ch.suterra.art.voronoi;

import ch.suterra.art.voronoi.assets.BoundingVolume;
import ch.suterra.art.voronoi.assets.DelaunayTriangulation;
import ch.suterra.art.voronoi.assets.PointCloud;
import ch.suterra.art.voronoi.assets.Triangle;
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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by yannick on 21/01/16.
 */
public class VoronoiApp extends JFrame implements KeyListener {
	private Canvas3D canvas;
	private SimpleUniverse universe;
	private BranchGroup objRoot;
	private TransformGroup objTransform;

	private BoundingVolume boundingVolume;
	private PointCloud pointCloud;

	private void addBackground(BranchGroup objRoot) {
		Background bg = new Background(new Color3f(.5f, .5f, .5f));
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
		bg.setApplicationBounds(bounds);
		objRoot.addChild(bg);
	}

	public VoronoiApp() throws IOException {
		long seed = System.currentTimeMillis() % 10000;

		configureWindow(seed);
		configureCanvas();
		conigureUniverse();

		// setup content
		boundingVolume = BoundingVolume.createCube(1, seed);
		pointCloud = PointCloud.create(1, boundingVolume, true);

		createSceneGraph();
		addBackground(objRoot);

		objTransform.addChild(boundingVolume.toNode());
		objTransform.addChild(pointCloud.toNode());

		long startTime = System.currentTimeMillis();
		DelaunayTriangulation triangles = new DelaunayTriangulation(pointCloud);

		exportSCAD(triangles, pointCloud, seed, "result");

		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		System.out.printf("time: %dms\n", elapsedTime);

		System.out.printf("Triangles generated: %d\n", triangles.size());
		objTransform.addChild(triangles.toNode(false));

		compileSceneGraph();

		universe.addBranchGraph(objRoot);

		canvas.getView().setSceneAntialiasingEnable(true);
	}

	private void configureWindow(long seed) {
		setTitle(String.format("Voronoi3D [seed:%d]", seed));
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
		canvas = new Canvas3D(gc);

		System.out.printf("cv.getSceneAntialiasingAvailable(): %s\n", canvas.getSceneAntialiasingAvailable());
		System.out.printf("cv.queryProperties().get(\"sceneAntialiasingNumPasses\"): %s\n", canvas.queryProperties().get("sceneAntialiasingNumPasses"));

		getContentPane().add(canvas, BorderLayout.CENTER);
		canvas.addKeyListener(this);
	}

	private void conigureUniverse() {
		universe = new SimpleUniverse(canvas);
		universe.getViewingPlatform().setNominalViewingTransform();
	}

	public BranchGroup createSceneGraph() {
		objRoot = new BranchGroup();

		objTransform = new TransformGroup();
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

		objRoot.addChild(objTransform);

		MouseRotate myMouseRotate = new MouseRotate();
		myMouseRotate.setTransformGroup(objTransform);
		MouseTranslate myMouseTranslate = new MouseTranslate();
		myMouseTranslate.setTransformGroup(objTransform);
		MouseZoom myMouseZoom = new MouseZoom();
		myMouseZoom.setTransformGroup(objTransform);

		BoundingSphere bounds = new BoundingSphere();
		bounds.setRadius(1.0);
		myMouseRotate.setSchedulingBounds(bounds);
		myMouseTranslate.setSchedulingBounds(bounds);
		myMouseZoom.setSchedulingBounds(bounds);

		Transform3D transform = new Transform3D();
		transform.setScale(1);
		objTransform.setTransform(transform);

		objRoot.addChild(myMouseRotate);
		objRoot.addChild(myMouseTranslate);
		objRoot.addChild(myMouseZoom);

		return objRoot;
	}

	protected boolean compileSceneGraph() {
		if (objRoot != null) {
			objRoot.compile();
			return true;
		}
		return false;
	}

	private void writePoint(BufferedWriter out, Point3d point, float scale, int radius, int resolution) throws IOException {
		out.write(String.format("translate([%f,%f,%f])", point.x*scale, point.y*scale, point.z*scale));
		out.write("\n\t");
		out.write(String.format("sphere(r = %d, center = true, $fn = %d);\n", radius, resolution));
	}

	private void writeLine(BufferedWriter out, Point3d p0, Point3d p1, float scale, int radius, int resolution) throws IOException {
		Vector3d dir = new Vector3d(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);

		double r = dir.length();
		double t = Math.atan(dir.y / dir.x);
		double p = Math.acos(dir.z / r);

		double alpha = Math.atan(dir.x/dir.z);
		if (dir.z > 0) {
			alpha += Math.PI;
		}
		double beta = Math.acos(dir.y/r) + (Math.PI*.5f);


		out.write(String.format("translate([%f,%f,%f])", p0.x * scale, p0.y * scale, p0.z * scale));
		out.write("\n\t");
		out.write(String.format("rotate([%f,%f,%f])", Math.toDegrees(alpha), Math.toDegrees(beta), 0.f));
		out.write("\n\t");
		out.write(String.format("cylinder(h = %f, r1 = %d, r2 = %d, center = false, $fn = %d);\n", r*scale, radius, radius>>1, resolution));
	}

	private void exportSCAD(DelaunayTriangulation triangles, PointCloud points, long seed, String namePrefix) throws IOException {
		BufferedWriter out = null;
		try {
//			int nameSuffix = (int) ((new java.util.Date()).getTime()/1000);
			int nameSuffix = 0;
			String home = System.getProperty("user.home");
			FileWriter fstream = new FileWriter(home + "/" + namePrefix + String.valueOf(nameSuffix) + ".scad", false);
			out = new BufferedWriter(fstream);

			int radius = 2;
			int resolution = 20;
			float scale = 10;

			if (false) {
				for (int i=0; i<points.size(); i++) {
					Point3d point = points.get(i);
					writePoint(out, point, scale, radius, resolution);
				}

				for (int i = 0; i </*triangles.size()*/1; i++) {
					Triangle triangle = triangles.get(i);
					writeLine(out, triangle.m_p1, triangle.m_p2, scale, radius, resolution);
					writeLine(out, triangle.m_p2, triangle.m_p3, scale, radius, resolution);
					writeLine(out, triangle.m_p3, triangle.m_p1, scale, radius, resolution);
				}
			} else {
				Point3d p0 = new Point3d(-1,-1,-1);
				Point3d p1 = new Point3d(1,1,1);
				writePoint(out, p0, scale, radius, resolution);
				writePoint(out, p1, scale, radius, resolution);
				writeLine(out, p0, p1, scale, radius, resolution);
			}
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

	@Override
	public void keyTyped(KeyEvent ke) {
		//do nothing yet
	}

	@Override
	public void keyPressed(KeyEvent ke) {
		//do nothing yet
	}

	@Override
	public void keyReleased(KeyEvent ke) {
		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}
}