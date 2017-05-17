package ch.suterra.art.voronoi.assets;

/**
 * Created by yannick on 17.05.17.
 */
public class Line {
    public Integer m_id;
    public Triangle m_t1;
    public Triangle m_t2;

    public Line(Integer id, Triangle t1, Triangle t2) {
        m_id = id;
        m_t1 = t1;
        m_t2 = t2;
    }
}
