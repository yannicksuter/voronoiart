package ch.suterra.art.scad;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Created by yannick on 11.05.17.
 */
public class Writer {
    public static void writePoint(BufferedWriter out, Point3d point, float scale, int radius, int resolution) throws IOException {
        out.write(String.format("translate([%f,%f,%f])", point.x*scale, point.y*scale, point.z*scale));
        out.write("\n\t");
        out.write(String.format("sphere(r = %d, center = true, $fn = %d);\n", radius, resolution));
    }

    public static void writeLine(BufferedWriter out, Point3d p0, Point3d p1, float scale, int radius, int resolution) throws IOException {
        Vector3d dir = new Vector3d(p1.x - p0.x, p1.y - p0.y, p1.z - p0.z);

        double r = dir.length();
        double alpha = Math.acos(dir.y/r);
        double beta = Math.atan(dir.x / dir.z);
        if (dir.z < 0) {
            beta += Math.PI;
        }

        out.write(String.format("translate([%f,%f,%f])", p0.x * scale, p0.y * scale, p0.z * scale));
        out.write("\n\t");
        // todo: fix NaN issue with some exported values
        out.write(String.format("rotate([%f,%f,%f])", -90 + Math.toDegrees(alpha), Math.toDegrees(beta), 0.f));
        out.write("\n\t");
        out.write(String.format("cylinder(h = %f, r1 = %d, r2 = %d, center = false, $fn = %d);\n", r*scale, radius, radius, resolution));
    }
}
