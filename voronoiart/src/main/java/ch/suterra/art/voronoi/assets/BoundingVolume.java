package ch.suterra.art.voronoi.assets;

import com.sun.j3d.utils.geometry.ColorCube;

import javax.media.j3d.BoundingBox;
import javax.media.j3d.Node;

/**
 * Created by yannick on 28/01/16.
 */
public class BoundingVolume {
	private BoundingBox bb;

	public static BoundingVolume createCube(double size) {
		return new BoundingVolume();
	}

	public Node toNode() {
		return new ColorCube(0.4);
	}
}
