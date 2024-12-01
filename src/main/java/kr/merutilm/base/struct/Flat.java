package kr.merutilm.base.struct;

/**
 * ax^3 + bx^2 + cx + d = 0;
 */
public record Flat(double a, double b, double c, double d) implements Struct<Flat> {

    public static Flat of(Point3D d1, Point3D d2, Point3D d3) {
        final double A = d1.y() * (d2.z() - d3.z()) + d2.y() * (d3.z() - d1.z()) + d3.y() * (d1.z() - d2.z());
        final double B = d1.z() * (d2.x() - d3.x()) + d2.z() * (d3.x() - d1.x()) + d3.z() * (d1.x() - d2.x());
        final double C = d1.x() * (d2.y() - d3.y()) + d2.x() * (d3.y() - d1.y()) + d3.x() * (d1.y() - d2.y());
        final double D = d1.x() * (d2.y() * d3.z() - d3.y() * d2.z()) + d2.x() * (d3.y() * d1.z() - d1.y() * d3.z()) + d3.x() * (d1.y() * d2.z() - d2.y() * d1.z());
        return new Flat(A, B, C, -D);
    }

    @Override
    public Builder edit() {
        return new Builder(a, b, c, d);
    }

    public static final class Builder implements StructBuilder<Flat> {
        private double a;
        private double b;
        private double c;
        private double d;


        private Builder(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public Builder setA(double a) {
            this.a = a;
            return this;
        }

        public Builder setB(double b) {
            this.b = b;
            return this;
        }

        public Builder setC(double c) {
            this.c = c;
            return this;
        }

        public Builder setD(double d) {
            this.d = d;
            return this;
        }

        @Override
        public Flat build() {
            return new Flat(a, b, c, d);
        }
    }
}
