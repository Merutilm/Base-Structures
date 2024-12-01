package kr.merutilm.base.struct;

import kr.merutilm.base.util.AdvancedMath;

public record PolarPoint(double radius, double angle) implements Struct<PolarPoint> {

    public PolarPoint(double radius, double angle) {
        this.radius = AdvancedMath.fixDouble(radius);
        this.angle = AdvancedMath.fixDouble(angle);
    }

    @Override
    public Builder edit() {
        return new Builder(radius, angle);
    }

    public static final class Builder implements StructBuilder<PolarPoint> {
        private double radius;
        private double angle;

        public Builder(double radius, double angle) {
            this.radius = radius;
            this.angle = angle;
        }

        public Builder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder addRadius(double a){
            this.radius += a;
            return this;
        }

        public Builder setAngle(double angle) {
            this.angle = angle;
            return this;
        }

        @Override
        public PolarPoint build() {
            return new PolarPoint(radius, angle);
        }

    }
    public Point2D coordinate(Point2D center) {
        double angRad = Math.toRadians(angle);
        return center.add(new Point2D(radius * Math.cos(angRad), radius * Math.sin(angRad)));
    }

    public PolarPoint add(PolarPoint p){
        return this.coordinate().add(p.coordinate()).toPolar();
    }
    public Point2D coordinate() {
        return coordinate(new Point2D(0,0));
    }
}
