package ch.suterra.art.vase;

import ch.suterra.art.scad.Writer;
import ch.suterra.art.voronoi.assets.*;

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

    int m_particleCount = 10;
    boolean m_partitionedDistribution = true;

    BoundingVolume m_boundingVolume;
    PointCloud m_pointCloud;
    DelaunayTriangulation m_triangles;
    VoronoiConnections m_connections;

    public void createVase() throws IOException {
        initializeSeed(null);

        long startTime = System.currentTimeMillis();

        m_boundingVolume = BoundingVolume.createCube(1, m_seed);
        m_pointCloud = PointCloud.create(m_particleCount, m_partitionedDistribution, m_boundingVolume, true);
        m_triangles = new DelaunayTriangulation(m_pointCloud);

        findNeighbours(m_triangles);
        m_connections = new VoronoiConnections(m_triangles);

        long stopTime = System.currentTimeMillis();

        writeSCAD(m_connections, m_triangles, m_pointCloud, m_seed, "vase", stopTime - startTime);
    }

    public void initializeSeed(Long s) {
        if (s != null) {
            m_seed = s;
        } else {
            m_seed = System.currentTimeMillis() % 10000;
        }
    }

    private boolean isNeighbour(int p1, int p2, Triangle t) {
        return ((p1 == t.m_id1 && p2 == t.m_id2) ||
                (p1 == t.m_id2 && p2 == t.m_id3) ||
                (p1 == t.m_id3 && p2 == t.m_id1) ||
                (p2 == t.m_id1 && p1 == t.m_id2) ||
                (p2 == t.m_id2 && p1 == t.m_id3) ||
                (p2 == t.m_id3 && p1 == t.m_id1));
    }

    private void findNeighbours(DelaunayTriangulation triangles) {
        for (int i=0; i< triangles.size(); i++) {
            Triangle t = triangles.get(i);
            t.m_n1 = null;
            t.m_n2 = null;
            t.m_n3 = null;
        }

        for (int i=0; i< triangles.size(); i++) {
            Triangle t = triangles.get(i);
            for (int j=0;  j<triangles.size(); j++) {
                if (i!=j) {
                    Triangle n = triangles.get(j);
                    if (isNeighbour(t.m_id1, t.m_id2, n)) {
                        t.m_n1 = n;
                    } else if (isNeighbour(t.m_id2, t.m_id3, n)) {
                        t.m_n2 = n;
                    } else if (isNeighbour(t.m_id3, t.m_id1, n)) {
                        t.m_n3 = n;
                    }
                }
            }
        }
    }

    private void writeSCAD(VoronoiConnections connections, DelaunayTriangulation triangles, PointCloud points, long seed, String namePrefix, long elapsedTime) throws IOException {
        BufferedWriter out = null;
        try {
            int nameSuffix = 0;
            String home = System.getProperty("user.home");
            FileWriter fstream = new FileWriter(home + "/" + namePrefix + String.valueOf(nameSuffix) + ".scad", false);
            out = new BufferedWriter(fstream);

            out.write(String.format("// Export run: %s\n", new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date())));
            out.write(String.format("// Generated in %d ms with seed: %d\n", elapsedTime, seed));

            int radius = 1;
            int resolution = 10;
            float scale = 100;

            if (points != null) {
                for (int i = 0; i < points.size(); i++) {
                    Point3d point = points.get(i);
                    Writer.writePoint(out, point, scale, radius, resolution);
                }
            }

            if (false) {
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

            if (triangles != null && connections != null) {
                for (int i = 0; i < triangles.size(); i++) {
                    Writer.writePoint(out, triangles.get(i).m_circumpherence.m_center, scale, radius, resolution);
                }
                for (Line l : connections.getConnections()) {
                    Writer.writeLine(out, l.m_t1.m_circumpherence.m_center, l.m_t2.m_circumpherence.m_center, scale, radius, resolution);
                }
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
