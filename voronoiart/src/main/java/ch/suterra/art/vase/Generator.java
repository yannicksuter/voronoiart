package ch.suterra.art.vase;

import ch.suterra.art.scad.Writer;
import ch.suterra.art.voronoi.assets.DelaunayTriangulation;
import ch.suterra.art.voronoi.assets.PointCloud;
import ch.suterra.art.voronoi.assets.Triangle;

import javax.vecmath.Point3d;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yannick on 11.05.17.
 */
public class Generator {
    private long m_seed;

    public void initializeSeed(Long s) {
        if (s != null) {
            m_seed = s;
        } else {
            m_seed = System.currentTimeMillis() % 10000;
        }
    }

    public void createVase() throws IOException {
        initializeSeed(null);
        writeSCAD(null, null, 0, "vase");
    }

    private void writeSCAD(DelaunayTriangulation triangles, PointCloud points, long seed, String namePrefix) throws IOException {
        BufferedWriter out = null;
        try {
//			int nameSuffix = (int) ((new java.util.Date()).getTime()/1000);
            int nameSuffix = 0;
            String home = System.getProperty("user.home");
            FileWriter fstream = new FileWriter(home + "/" + namePrefix + String.valueOf(nameSuffix) + ".scad", false);
            out = new BufferedWriter(fstream);

            out.write(String.format("// Export run: %s\n", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date())));
            out.write(String.format("// Generated in %d ms with seed: %d\n", 0, m_seed));

            int radius = 1;
            int resolution = 10;
            float scale = 100;

            if (points != null) {
                for (int i = 0; i < points.size(); i++) {
                    Point3d point = points.get(i);
                    Writer.writePoint(out, point, scale, radius, resolution);
                }
            }

            if (triangles != null) {
                int lines = 0;
                Set<String> index = new HashSet<String>();
                for (int i = 0; i < triangles.size(); i++) {
                    Triangle triangle = triangles.get(i);
                    if (!index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id2)) && !index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id1))) {
                        Writer.writeLine(out, triangle.m_p1, triangle.m_p2, scale, radius, resolution);
                        index.add(String.format("%d-%d", triangle.m_id1, triangle.m_id2));
                        lines++;
                    }
                    if (!index.contains(String.format("%d-%d", triangle.m_id2, triangle.m_id3)) && !index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id2))) {
                        Writer.writeLine(out, triangle.m_p2, triangle.m_p3, scale, radius, resolution);
                        index.add(String.format("%d-%d", triangle.m_id2, triangle.m_id3));
                        lines++;
                    }
                    if (!index.contains(String.format("%d-%d", triangle.m_id3, triangle.m_id1)) && !index.contains(String.format("%d-%d", triangle.m_id1, triangle.m_id3))) {
                        Writer.writeLine(out, triangle.m_p3, triangle.m_p1, scale, radius, resolution);
                        index.add(String.format("%d-%d", triangle.m_id3, triangle.m_id1));
                        lines++;
                    }
                }
                System.out.println(String.format("%d lines exported.", lines));
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

    public static void main(String[] args) {
        try {
            new Generator().createVase();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
