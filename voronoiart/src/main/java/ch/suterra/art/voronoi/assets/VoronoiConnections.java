package ch.suterra.art.voronoi.assets;

import javax.vecmath.Point3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by yannick on 17.05.17.
 */
public class VoronoiConnections {
    private Random m_random = new Random();
    Map<Integer, Line> m_connections = new HashMap<Integer, Line>();

    public VoronoiConnections(DelaunayTriangulation triangles) {
        m_connections.clear();
        m_random.setSeed(System.currentTimeMillis());

        for (int i=0;i<triangles.size();i++) {
            Triangle t = triangles.get(i);
            if (t.m_n1 != null) {
                Integer hashCode = getHash(t, t.m_n1);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n1));
                }
            } else {
                Line l = createOutgoingLine(t.m_circumpherence.m_center ,t.m_p1, t.m_p2);
                m_connections.put(l.m_id, l);
            }
            if (t.m_n2 != null) {
                Integer hashCode = getHash(t, t.m_n2);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n2));
                }
            } else {
                Line l = createOutgoingLine(t.m_circumpherence.m_center ,t.m_p2, t.m_p3);
                m_connections.put(l.m_id, l);
            }
            if (t.m_n3 != null) {
                Integer hashCode = getHash(t, t.m_n3);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n3));
                }
            } else {
                Line l = createOutgoingLine(t.m_circumpherence.m_center ,t.m_p3, t.m_p1);
                m_connections.put(l.m_id, l);
            }
        }
    }

    protected Line createOutgoingLine(Point3d c, Point3d p1, Point3d p2) {
        Point3d p = new Point3d(p1);
        p2.x += (p2.x-p1.x) * .5f;
        p2.y += (p2.y-p1.y) * .5f;
        p2.z += (p2.z-p1.z) * .5f;
        return new Line(m_random.nextInt(), c, p);
    }

    protected int getHash(Triangle a, Triangle b) {
        String s = String.format("%d=%d", Math.min(a.m_id, b.m_id), Math.max(a.m_id, b.m_id));
        return s.hashCode();
    }

    public ArrayList<Line> getConnections() {
        return new ArrayList<Line>(m_connections.values());
    }
}
