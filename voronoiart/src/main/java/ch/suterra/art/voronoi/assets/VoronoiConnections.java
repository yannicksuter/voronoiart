package ch.suterra.art.voronoi.assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yannick on 17.05.17.
 */
public class VoronoiConnections {
    Map<Integer, Line> m_connections = new HashMap<Integer, Line>();

    public VoronoiConnections(DelaunayTriangulation triangles) {
        m_connections.clear();

        for (int i=0;i<triangles.size();i++) {
            Triangle t = triangles.get(i);
            if (t.m_n1 != null) {
                Integer hashCode = getHash(t, t.m_n1);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n1));
                }
            }
            if (t.m_n2 != null) {
                Integer hashCode = getHash(t, t.m_n2);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n2));
                }
            }
            if (t.m_n3 != null) {
                Integer hashCode = getHash(t, t.m_n3);
                if (m_connections.get(hashCode) == null) {
                    m_connections.put(hashCode, new Line(hashCode, t, t.m_n3));
                }
            }
        }
    }

    protected int getHash(Triangle a, Triangle b) {
        String s = String.format("%d=%d", Math.min(a.m_id, b.m_id), Math.max(a.m_id, b.m_id));
        return s.hashCode();
    }

    public ArrayList<Line> getConnections() {
        return new ArrayList<Line>(m_connections.values());
    }
}
