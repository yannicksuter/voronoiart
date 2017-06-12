package ch.suterra.art.voronoi.assets;

import javax.vecmath.Point3d;

/**
 * Created by yannick on 17.05.17.
 */
public class Line {
    public Integer m_id;
    public Triangle m_t1;
    public Point3d m_p1;
    public Triangle m_t2;
    public Point3d m_p2;

    public Line(Integer id, Triangle t1, Triangle t2) {
        m_id = id;
        m_t1 = t1;
        m_p1 = t1.m_circumpherence.m_center;
        m_t2 = t2;
        m_p2 = t2.m_circumpherence.m_center;
    }
    public Line(Integer id, Point3d p1, Point3d p2) {
        m_id = id;
        m_t1 = null;
        m_p1 = new Point3d(p1);
        m_t2 = null;
        m_p2 = new Point3d(p2);
    }
}
