package ch.suterra.art.scad;

import ch.suterra.art.voronoi.assets.DelaunayTriangulation;
import ch.suterra.art.voronoi.assets.Triangle;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yannick on 11.05.17.
 */
public class Writer {
    public static void writePoint(BufferedWriter out, Point3d point, float scale, int radius, int resolution, Vector3d color) throws IOException {
        out.write(String.format("translate([%f,%f,%f])", point.x*scale, point.y*scale, point.z*scale));
        out.write("\n\t");
        if (color == null) {
            out.write(String.format("sphere(r = %d, center = true, $fn = %d);\n", radius, resolution));
        } else {
            out.write(String.format("color([%.3f, %.3f, %.3f]) sphere(r = %d, center = true, $fn = %d);\n", color.x, color.y, color.z, radius, resolution));
        }
    }

    public static void writeLine(BufferedWriter out, Point3d p0, Point3d p1, float scale, int radius, int resolution, Vector3d color) throws IOException {
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
        if (color == null) {
            out.write(String.format("cylinder(h = %f, r1 = %d, r2 = %d, center = false, $fn = %d);\n", r * scale, radius, radius, resolution));
        } else {
            out.write(String.format("color([%.3f, %.3f, %.3f]) cylinder(h = %f, r1 = %d, r2 = %d, center = false, $fn = %d);\n", color.x, color.y, color.z, r * scale, radius, radius, resolution));
        }
    }

    public static void writeTriangles(BufferedWriter out, DelaunayTriangulation triangles, float scale, int radius, int resolution, Vector3d color, boolean renderCenters, Vector3d centerColor) throws IOException {
        int lines = 0;
        Set<String> index = new HashSet<String>();
        for (int i = 0; i < triangles.size(); i++) {
            Triangle triangle = triangles.get(i);
            if (!index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id2)) && !index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id1))) {
                Writer.writeLine(out, triangle.m_p1, triangle.m_p2, scale, radius, resolution, color);
                index.add(String.format("%d-%d", triangle.m_id1, triangle.m_id2));
                lines++;
            }
            if (!index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id3)) && !index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id2))) {
                Writer.writeLine(out, triangle.m_p2, triangle.m_p3, scale, radius, resolution, color);
                index.add(String.format("%d-%d", triangle.m_id2, triangle.m_id3));
                lines++;
            }
            if (!index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id1)) && !index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id3))) {
                Writer.writeLine(out, triangle.m_p3, triangle.m_p1, scale, radius, resolution, color);
                index.add(String.format("%d-%d", triangle.m_id3, triangle.m_id1));
                lines++;
            }

            if (renderCenters) {
                Writer.writePoint(out, triangle.m_circumpherence.m_center, scale, radius * 2, resolution, centerColor);
            }
        }
        System.out.println(String.format("%d lines exported.", lines));
    }
}
