package ch.suterra.art.voronoi;

import ch.suterra.art.voronoi.assets.BoundingVolume;
import ch.suterra.art.voronoi.assets.PointCloud;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import java.awt.*;
import java.io.IOException;

/**
 * Created by yannick on 21/01/16.
 */
public class VoronoiApp extends JFrame {
	private Canvas3D canvas;
	private SimpleUniverse universe;
	private BranchGroup objRoot;
	private TransformGroup objTransform;

	// voronoi vase
	private BoundingVolume boundingVolume;
	private PointCloud pointCloud;

	private void addBackground(BranchGroup objRoot) {
		Background bg = new Background(new Color3f(.5f, .5f, .5f));
		BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),100.0);
		bg.setApplicationBounds(bounds);
		objRoot.addChild(bg);
	}

	public VoronoiApp() throws IOException {
		configureWindow();
		configureCanvas();
		conigureUniverse();

		// setup content
		boundingVolume = BoundingVolume.createCube(0.2);
		pointCloud = PointCloud.create(100, boundingVolume);

		createSceneGraph();
		addBackground(objRoot);

//		BranchGroup boxBranch = new BranchGroup();
//		boxBranch.setCapability(BranchGroup.ALLOW_DETACH);
//		Box box = new Box();
//		boxBranch.addChild(box);
//		objRoot.addChild(boxBranch);

//		objTransform.addChild(boundingVolume.toNode());
		objTransform.addChild(pointCloud.toNode());
		compileSceneGraph();

		universe.addBranchGraph(objRoot);

		canvas.getView().setSceneAntialiasingEnable(true);
	}

	private void configureWindow() {
		setTitle("Voronoi3D");
		setSize(800, 600);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int locationX = (screenSize.width - getWidth()) / 2;
		int locationY = (screenSize.height - getHeight()) / 2;
		setLocation(locationX,locationY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void configureCanvas() {
		GraphicsConfigTemplate3D gct3D= new GraphicsConfigTemplate3D();
		gct3D.setSceneAntialiasing(GraphicsConfigTemplate3D.REQUIRED);
		GraphicsConfiguration gc= java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getBestConfiguration(gct3D);
		canvas = new Canvas3D(gc);

		System.out.printf("cv.getSceneAntialiasingAvailable(): %s\n",canvas.getSceneAntialiasingAvailable());
		System.out.printf("cv.queryProperties().get(\"sceneAntialiasingNumPasses\"): %s\n",canvas.queryProperties().get("sceneAntialiasingNumPasses"));

		getContentPane().add(canvas, BorderLayout.CENTER);
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
		myMouseRotate.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseRotate);

		MouseTranslate myMouseTranslate = new MouseTranslate();
		myMouseTranslate.setTransformGroup(objTransform);
		myMouseTranslate.setSchedulingBounds(new BoundingSphere());
		objRoot.addChild(myMouseTranslate);

		MouseZoom myMouseZoom = new MouseZoom();
		myMouseZoom.setTransformGroup(objTransform);
		myMouseZoom.setSchedulingBounds(new BoundingSphere());
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

	// MAIN ********************************************************************

	public static void main(String[] args) {
		try {
			new VoronoiApp().setVisible(true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}