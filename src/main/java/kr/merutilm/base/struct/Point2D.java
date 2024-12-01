package kr.merutilm.base.struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.functions.LoopedList;
import kr.merutilm.base.util.AdvancedMath;

import java.util.List;
import java.util.Objects;


public record Point2D(double x, double y) implements Struct<Point2D> {

    public static final Point2D ORIGIN = new Point2D(0.0, 0.0);

    @Nullable
    public static Point2D convert(String value) {
        if (value == null) {
            return null;
        }
        String[] arr = value.replace(" ", "").split(",");
        if (arr.length == 1) {
            double v = Double.parseDouble(Objects.equals(arr[0], "null") ? "NaN" : arr[0]);
            return new Point2D(v, v);
        }

        double x = Double.parseDouble(Objects.equals(arr[0], "null") ? "NaN" : arr[0]);
        double y = Double.parseDouble(Objects.equals(arr[1], "null") ? "NaN" : arr[1]);
        return new Point2D(x, y);
    }

    @Override
    public Builder edit() {
        return new Builder(x, y);
    }

    public static final class Builder implements StructBuilder<Point2D> {
        private double x;
        private double y;

        public Builder(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Builder setX(double x) {
            this.x = x;
            return this;
        }

        public Builder setY(double y) {
            this.y = y;
            return this;
        }

        public Builder addX(double x) {
            if (Double.isNaN(x)) {
                return this;
            }
            return setX(this.x + x);
        }

        public Builder addY(double y) {
            if (Double.isNaN(y)) {
                return this;
            }
            return setY(this.y + y);
        }

        public Builder add(double x, double y) {
            return addX(x).addY(y);
        }

        public Builder add(Point2D p) {
            return addX(p.x).addY(p.y);
        }

        public Builder multiplyX(double multiplier) {
            if (Double.isNaN(multiplier)) {
                return this;
            }
            return setX(this.x * multiplier);
        }

        public Builder multiplyY(double multiplier) {
            if (Double.isNaN(multiplier)) {
                return this;
            }
            return setY(this.y * multiplier);
        }

        public Builder multiply(double multiplier) {
            return multiplyX(multiplier).multiplyY(multiplier);
        }

        public Builder invertX() {
            return setX(-this.x);
        }

        public Builder invertY() {
            return setY(-this.y);
        }

        public Builder invert() {
            return invertX().invertY();
        }

        public Builder rotate(double cx, double cy, double r) {
            if (Double.isNaN(cx) || Double.isNaN(cy)) {
                throw new IllegalArgumentException("NaN center position");
            }
            if(r == 0){
                return this;
            }

            add(-cx, -cy);

            double dx = x;
            double dy = y;

            double radius = AdvancedMath.hypot(dx, dy);
            double curAngle = AdvancedMath.atan2(dy, dx);
            double toRadianRot = Math.toRadians(r);

            setX(radius * Math.cos(curAngle + toRadianRot));
            setY(radius * Math.sin(curAngle + toRadianRot));
            add(cx, cy);

            return this;
        }

        public Builder rotate(Point2D center, double rotation) {
            return rotate(center.x(), center.y(), rotation);
        }

        public Builder rotate(double rotation) {
            return rotate(0, 0, rotation);
        }

        @Override
        public Point2D build() {
            return new Point2D(x, y);
        }

    }

    public Point2D invert() {
        return edit().invert().build();
    }

    public Point2D add(Point2D add) {
        return add(add.x(), add.y());
    }

    public Point2D add(double x, double y) {
        return edit().addX(x).addY(y).build();
    }

    public Point2D multiply(double multiplier) {
        return edit().multiply(multiplier).build();
    }

    public double distance(Point2D target) {
        return distance(target.x, target.y);
    }

    public double distance(double x, double y) {
        return AdvancedMath.hypot(x - this.x, y - this.y);
    }

    public PolarPoint toPolar() {
        if (isAbstract()) {
            throw new IllegalArgumentException("NaN center position");
        }
        return new PolarPoint(AdvancedMath.hypot(x, y), Math.toDegrees(AdvancedMath.atan2(y, x)));
    }

    public double angle(Point2D p1, Point2D p2) {
        Point2D cp1 = new Point2D(p1.x - x, p1.y - y);
        Point2D cp2 = new Point2D(p2.x - x, p2.y - y);
        return (Math.toDegrees(AdvancedMath.atan2(cp2.y, cp2.x) - AdvancedMath.atan2(cp1.y, cp1.x)) + 360) % 360;
    }

    public static boolean isClockwise(List<Point2D> polygon) {
        return isClockwise(polygon.toArray(Point2D[]::new));
    }

    public static boolean isClockwise(Point2D[] polygon) {
        LoopedList<Point2D> dots = new LoopedList<>(polygon);
        for (int i = 0; i < polygon.length; i++) {
            if (!isClockwise(dots.get(i), dots.get(i + 1), dots.get(i + 2))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 좌표가 명시되지 않은 경우 - true
     */
    public boolean isAbstract() {
        return Double.isNaN(x) || Double.isNaN(y);
    }

    public static boolean isClockwise(Point2D a, Point2D b, Point2D c) {
        if (a.isAbstract() || b.isAbstract() || c.isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }
        final Point2D d1p2D = new Point2D(a.x(), a.y());
        final Point2D d2p2D = new Point2D(b.x(), b.y());
        final Point2D d3p2D = new Point2D(c.x(), c.y());

        return (d2p2D.x() - d1p2D.x()) * (d3p2D.y() - d1p2D.y()) < (d3p2D.x() - d1p2D.x()) * (d2p2D.y() - d1p2D.y());
    }


    public static Point2D getOnCircleRandomPoint(double distance) {
        if (Double.isNaN(distance)) {
            throw new IllegalArgumentException("NaN");
        }

        double x = AdvancedMath.doubleRandom(1);
        double y = Math.sqrt(1 - x * x);

        x *= distance;
        y *= distance;

        if (AdvancedMath.intRandom(2) == 0) {
            x *= -1;
        }
        if (AdvancedMath.intRandom(2) == 0) {
            y *= -1;
        }
        return new Point2D(x, y);
    }

    public Point2D getInCircleRandomPoint(double radius) {
        if (Double.isNaN(radius) || isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }
        return getInCircleRangeRandomPoint(0, radius);
    }

    public Point2D getInCircleRangeRandomPoint(double innerBound, double outerBound) {
        if (Double.isNaN(innerBound) || Double.isNaN(outerBound) || isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }

        double x = x();
        double y = y();
        double randomRadian = AdvancedMath.doubleRandom(Math.PI * 2);
        double randomRadius = AdvancedMath.random(innerBound, outerBound);
        x += randomRadius * Math.cos(randomRadian);
        y += randomRadius * Math.sin(randomRadian);
        return new Point2D(x, y);
    }


    /**
     * @return 두 점을 연결하는 직선 두 개의 교점, 두 직선이 평행한 경우 null
     */
    @Nullable
    public static Point2D lineIntersection(Point2D s1, Point2D e1, Point2D s2, Point2D e2) {
        if (s1.isAbstract() || s2.isAbstract() || e1.isAbstract() || e2.isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }

        if (s1.x() == e1.x() && s2.x() == e2.x()) {
            return null;
        }
        if (s1.y() == e1.y() && s2.y() == e2.y()) {
            return null;
        }

        if (s1.x() == e1.x()) {
            double ratio = AdvancedMath.getRatio(s2.x(), e2.x(), s1.x());
            return new Point2D(s1.x(), AdvancedMath.ratioDivide(s2.y(), e2.y(), ratio));
        }

        if (s2.x() == e2.x()) {
            double ratio = AdvancedMath.getRatio(s1.x(), e1.x(), s2.x());
            return new Point2D(s2.x(), AdvancedMath.ratioDivide(s1.y(), e1.y(), ratio));
        }

        if (s1.y() == e1.y()) {
            double ratio = AdvancedMath.getRatio(s2.y(), e2.y(), s1.y());
            return new Point2D(AdvancedMath.ratioDivide(s2.x(), e2.x(), ratio), s1.y());
        }
        if (s2.y() == e2.y()) {
            double ratio = AdvancedMath.getRatio(s1.y(), e1.y(), s2.y());
            return new Point2D(AdvancedMath.ratioDivide(s1.x(), e1.x(), ratio), s2.y());
        }

        FunctionEase l1 = getLine(s1, e1);

        double m1 = l1.getInclination(0);
        double n1 = l1.apply(0);

        FunctionEase l2 = getLine(s2, e2);

        double m2 = l2.getInclination(0);
        double n2 = l2.apply(0);

        if (m1 == m2) {
            return null;
        }

        double x = -(n2 - n1) / (m2 - m1);
        double y = l1.apply(x);
        return new Point2D(x, y);
    }

    /**
     * 두 점을 연결하는 선분 두 개의 교점(직선 아님)
     */
    @Nullable
    public static Point2D dotIntersection(Point2D s1, Point2D e1, Point2D s2, Point2D e2) {
        Point2D lineIntersection = lineIntersection(s1, e1, s2, e2);
        if (lineIntersection == null) {
            return null; //두 쌍의 점 x,y가 모두 같은 경우는 여기서 처리함
        }

        return AdvancedMath.isInnerBound(s1.x(), e1.x(), lineIntersection.x())
                && AdvancedMath.isInnerBound(s2.x(), e2.x(), lineIntersection.x())
                && AdvancedMath.isInnerBound(s1.y(), e1.y(), lineIntersection.y())
                && AdvancedMath.isInnerBound(s2.y(), e2.y(), lineIntersection.y())
                ? lineIntersection : null;
    }

    public static FunctionEase getLine(Point2D s, Point2D e) {
        if (s.isAbstract() || e.isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }

        double m = (e.y() - s.y()) / (e.x() - s.x());
        double n = s.y() - m * s.x();

        if (Double.isInfinite(m)) {
            return t -> Double.POSITIVE_INFINITY;
        }

        return t -> m * t + n;
    }

    public static PolarPoint polarCoordinate(Point2D p1, Point2D p2) {
        if (p1.isAbstract() || p2.isAbstract()) {
            throw new IllegalArgumentException("NaN");
        }
        return p2.add(p1.invert()).toPolar();
    }

    /**
     * 점이 영역 내부의 점인지 조사
     * 점에서 오른쪽으로 그은 반직선과 도형의 교차점이 홀수개이면 내부에 있다
     */
    public static boolean isInnerDot(LoopedList<Point2D> area, Point2D dot) {

        int count = 0;
        for (int i = 0; i < area.size(); i++) {

            Point2D cur = area.get(i);
            Point2D next = area.get(i + 1);

            if (cur.isAbstract() || next.isAbstract() || dot.isAbstract()) {
                throw new IllegalArgumentException("NaN");
            }


            double x1;
            double x2;
            double y1;
            double y2;

            if (cur.y() < next.y()) {
                x1 = cur.x();
                y1 = cur.y();
                x2 = next.x();
                y2 = next.y();
            } else {
                x1 = next.x();
                y1 = next.y();
                x2 = cur.x();
                y2 = cur.y();
            }

            //두 점을 이은 직선이 dot 의 오른쪽을 지나는가??
            //y1 이상 y2 '미만'인가? + 교점이 x의 오른쪽인가?
            if (y2 > dot.y() && dot.y() >= y1 && (dot.y() - y1) * (x2 - x1) / (y2 - y1) + x1 > dot.x()) {
                count++;
            }
        }
        return count % 2 == 1;
    }

    public static Point2D ratioDivide(Point2D start, Point2D end, double ratio) {
        return new Point2D(AdvancedMath.ratioDivide(start.x, end.x, ratio), AdvancedMath.ratioDivide(start.y, end.y, ratio));
    }

    public static Point2D ratioDivide(Point2D start, Point2D end, double ratio, FunctionEase ease) {
        return new Point2D(AdvancedMath.ratioDivide(start.x, end.x, ratio, ease), AdvancedMath.ratioDivide(start.y, end.y, ratio, ease));
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Point2D p){
            return p.x == x && p.y == y;
        }
        return false;
    }

    @Nonnull
    @Override
    public String toString() {
        return (Double.isNaN(x) ? "null" : AdvancedMath.fixDouble(x)) + ", " + (Double.isNaN(y) ? "null" : AdvancedMath.fixDouble(y));
    }
}
