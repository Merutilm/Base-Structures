package kr.merutilm.base.functions;

import java.util.Arrays;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.util.AdvancedMath;

@FunctionalInterface
public interface FunctionEase {
    double INTERVAL = 0.000001;

    default double getInclination(double t) {
        return (apply(t + INTERVAL) - apply(t)) / INTERVAL;
    }

    double apply(double t);

    /**
     * 증가함수의 역을 계산합니다.
     */
    static double inverseFunction(FunctionEase ease, double value) {
        double result = 0.5;
        double add = 0.25;
        while (add > 0.000001) {
            if (value > ease.apply(result)) {
                result += add;
            } else {
                result -= add;
            }
            add /= 2;
        }
        return AdvancedMath.fixDouble(result);
    }

    static FunctionEase merge(Ease... ease) {
        return merge(Arrays.stream(ease).map(Ease::fun).toArray(FunctionEase[]::new));
    }

    /**
     * 가감속 합성
     */
    static FunctionEase merge(FunctionEase... eases) {
        FunctionEase result = t -> t;
        for (FunctionEase ease : eases) {
            result = result.andThen(ease);
        }
        return result.multiply(eases.length);
    }

    default FunctionEase andThen(Ease ease) {
        return andThen(ease.fun());
    }

    default FunctionEase andThen(FunctionEase ease) {
        return t -> this.apply(t) + ease.apply(t);
    }

    default FunctionEase multiply(double m) {
        return t -> m * this.apply(t);
    }
}
