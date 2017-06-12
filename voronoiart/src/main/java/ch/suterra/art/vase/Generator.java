package ch.suterra.art.vase;

import ch.suterra.art.scad.Writer;
import ch.suterra.art.voronoi.assets.*;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
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

    int m_particleCount = 1;
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

        PointCloud centerPoints = new PointCloud();
        for (int i = 0; i < m_triangles.size(); i++) {
            Triangle triangle = m_triangles.get(i);
            centerPoints.add(triangle.m_circumpherence.m_center);
        }
        DelaunayTriangulation voronoi = new DelaunayTriangulation(centerPoints);

//        findNeighbours(m_triangles);
//        m_connections = new VoronoiConnections(m_triangles);

        long stopTime = System.currentTimeMillis();

        writeSCAD(true, m_connections, m_triangles, m_pointCloud, voronoi, m_seed, "vase", stopTime - startTime);
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

    private void writeSCAD(boolean exportTriangulation, VoronoiConnections connections, DelaunayTriangulation triangles, PointCloud points, DelaunayTriangulation voronoi, long seed, String namePrefix, long elapsedTime) throws IOException {
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

            if (exportTriangulation) {
                Vector3d yellow = new Vector3d(1,1,0);
                Vector3d yellow_dark = new Vector3d(.5,.5,0);
                Vector3d green = new Vector3d(0,1,0);
                Vector3d green_dark = new Vector3d(0,.5,0);

                if (points != null) {
                    for (int i = 0; i < points.size(); i++) {
                        Point3d point = points.get(i);
                        Writer.writePoint(out, point, scale, radius*2, resolution, yellow_dark);
                    }
                }

                if (true && triangles != null) {
                    Writer.writeTriangles(out, triangles, scale, radius, resolution, yellow, true, green);
                }

                if (true && voronoi != null) {
                    Writer.writeTriangles(out, voronoi, scale, radius, resolution, green_dark, false, green);
                }
            }

            // export voronoi
//            Vector3d red = new Vector3d(1,0,0);
//            Vector3d blue = new Vector3d(0,0,1);
//            if (triangles != null && connections != null) {
//                for (int i = 0; i < triangles.size(); i++) {
//                    Writer.writePoint(out, triangles.get(i).m_circumpherence.m_center, scale, radius*2, resolution, blue);
//                }
//                for (Line l : connections.getConnections()) {
//                    Writer.writeLine(out, l.m_p1, l.m_p2, scale, radius, resolution, red);
//                }
//            }
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
