package kr.merutilm.base.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record Vertices2D(List<Point2D> vertices) {
    public Vertices2D(List<Point2D> vertices) {
        this.vertices = Collections.unmodifiableList(vertices);
    }

    public Vertices2D createBezierCurve(int points) {
        double a = 1.0 / points;
        List<Point2D> p = new ArrayList<>();
        for (double i = 0; i <= points; i++) {
            double r = a * i;
            p.add(getBezierPoint(r));
        }
        return new Vertices2D(p);
    }

    private Point2D getBezierPoint(double ratio){
        Vertices2D calc = this;
        while(calc.vertices.size() > 1){
            calc = calc.reduceCurveCalcVertices(ratio);
        }
        return calc.vertices.get(0);
    }

    private Vertices2D reduceCurveCalcVertices(double ratio) {
        List<Point2D> reduced = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            reduced.add(Point2D.ratioDivide(vertices.get(i), vertices.get(i + 1), ratio));
        }
        return new Vertices2D(reduced);
    }


}
