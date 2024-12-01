package kr.merutilm.base.struct;

import java.util.Arrays;

import kr.merutilm.base.util.AdvancedMath;

public record Range(double b1, double b2) implements Struct<Range> {
    public Builder edit() {
        return new Builder(b1, b2);
    }

    public static final class Builder implements StructBuilder<Range> {
        private double b1;
        private double b2;

        private Builder(double b1, double b2) {
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
        public Range build() {
            return new Range(b1, b2);
        }
    }
    public double min() {
        return Math.min(b1, b2);
    }

    public double max() {
        return Math.max(b1, b2);
    }

    private double interval() {
        return Math.abs(b2 - b1);
    }

    public double random() {
        return AdvancedMath.random(b1, b2);
    }

    public static double random(Range... ranges) {
        double intervalSum = Arrays.stream(ranges).mapToDouble(Range::interval).sum();
        double randomValue = AdvancedMath.doubleRandom(intervalSum);
        double offset = 0;

        for (Range range : ranges) {
            double currentMin = Math.min(range.b1, range.b2);
            double currentMax = Math.max(range.b1, range.b2);

            double currentIntervalMin = offset;
            double currentIntervalMax = offset + range.interval();

            if (currentIntervalMin <= randomValue && randomValue <= currentIntervalMax) {
                double ratio = AdvancedMath.getRatio(currentIntervalMin, currentIntervalMax, randomValue);
                return AdvancedMath.ratioDivide(currentMin, currentMax, ratio);
            }
            offset = currentIntervalMax;
        }

        throw new IllegalArgumentException("Not found");
    }
}
