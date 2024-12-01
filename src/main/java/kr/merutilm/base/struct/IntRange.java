package kr.merutilm.base.struct;

import kr.merutilm.base.util.AdvancedMath;

public record IntRange(int b1, int b2) implements Struct<IntRange> {
    public Builder edit() {
        return new Builder(b1, b2);
    }

    public static final class Builder implements StructBuilder<IntRange> {
        private int b1;
        private int b2;

        private Builder(int b1, int b2) {
            this.b1 = b1;
            this.b2 = b2;
        }

        public Builder setB1(int b1) {
            this.b1 = b1;
            return this;
        }

        public Builder setB2(int b2) {
            this.b2 = b2;
            return this;
        }

        @Override
        public IntRange build() {
            return new IntRange(b1, b2);
        }
    }

    private int interval() {
        return Math.abs(b2 - b1);
    }

    public int min() {
        return Math.min(b1, b2);
    }

    public int max() {
        return Math.max(b1, b2);
    }

    public boolean inRange(int t) {
        int min = Math.min(b1, b2);
        int max = Math.max(b1, b2);
        return min <= t && t <= max;
    }

    public int random() {
        if (b1 == b2) {
            return b1;
        }
        return Math.min(b1, b2) + AdvancedMath.intRandom(interval() + 1);
    }

}
