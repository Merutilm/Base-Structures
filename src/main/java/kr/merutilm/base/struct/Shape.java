package kr.merutilm.base.struct;

import javax.annotation.Nonnull;
import java.util.Arrays;


public record Shape(Point2D[] shape) implements Struct<Shape> {
    @Override
    public Builder edit() {
        return new Builder(shape);
    }

    public static final class Builder implements StructBuilder<Shape> {
        private Point2D[] shape;

        private Builder(Point2D[] shape) {
            this.shape = shape;
        }

        public Builder setShape(Point2D[] shape) {
            this.shape = shape;
            return this;
        }

        @Override
        public Shape build() {
            return new Shape(shape);
        }

    }

    @Override
    public boolean equals(Object o){
        if(o instanceof Point2D[] s){
            return Arrays.equals(shape, s);
        }
        return false;
    }
    @Override
    public int hashCode(){
        return Arrays.hashCode(shape);
    }

    @Nonnull
    @Override
    public String toString() {
        return String.join(", ", Arrays.stream(shape).map(Point2D::toString).toList());
    }
}
