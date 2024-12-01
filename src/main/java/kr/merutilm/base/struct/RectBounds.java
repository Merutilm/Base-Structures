package kr.merutilm.base.struct;

import java.awt.*;

public record RectBounds(int startX,
                         int startY,
                         int endX,
                         int endY) implements Struct<RectBounds> {

    @Override
    public Builder edit() {
        return new Builder(startX,startY,endX,endY);
    }

    public static final class Builder implements StructBuilder<RectBounds> {
        private int startX;
        private int startY;
        private int endX;
        private int endY;

        private Builder(int startX, int startY, int endX, int endY) {
            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
        }

        public Builder setStartX(int startX) {
            this.startX = startX;
            return this;
        }

        public Builder setStartY(int startY) {
            this.startY = startY;
            return this;
        }

        public Builder setEndX(int endX) {
            this.endX = endX;
            return this;
        }

        public Builder setEndY(int endY) {
            this.endY = endY;
            return this;
        }

        @Override
        public RectBounds build() {
            return new RectBounds(startX, startY, endX, endY);
        }
    }

    public Rectangle convertToRectangle() {
        return new Rectangle(startX, startY, endX - startX, endY - startY);
    }

    public Point location() {
        return new Point(startX, startY);
    }

    public Dimension scale() {
        return new Dimension(sizeX(), sizeY());
    }

    public int sizeX() {
        return endX - startX;
    }

    public int sizeY() {
        return endY - startY;
    }

    public static RectBounds of(Rectangle rectangle) {
        return new RectBounds(rectangle.x, rectangle.y, rectangle.x + rectangle.width, rectangle.y + rectangle.height);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RectBounds b) {
            return b.startX == startX && b.startY == startY && b.endX == endX && b.endY == endY;
        }
        return false;
    }

}
