package kr.merutilm.base.struct;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.base.exception.VertexOverflowException;
import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.functions.LoopedList;
import kr.merutilm.base.util.AdvancedMath;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Point 3 d.
 */
public record Point3D(double x, double y, double z) implements Struct<Point3D> {

    public static final Point3D ORIGIN = new Point3D(0, 0, 0);

    public Point3D(double x, double y, double z) {
        this.x = AdvancedMath.fixDouble(x);
        this.y = AdvancedMath.fixDouble(y);
        this.z = AdvancedMath.fixDouble(z);
    }

    /**
     * Convert point 3 d.
     *
     * @param value the value
     * @return the point 3 d
     */
    @Nullable
    public static Point3D convert(String value) {
        if (value == null) {
            return null;
        }
        String[] arr = value.replace(" ", "").split(",");
        double x = Double.parseDouble(arr[0]);
        double y = Double.parseDouble(arr[1]);
        double z = Double.parseDouble(arr[2]);
        return new Point3D(x, y, z);
    }

    @Override
    public Builder edit() {
        return new Builder(x, y, z);
    }


    /**
     * The type Builder.
     */
    public static final class Builder implements StructBuilder<Point3D> {
        /**
         * The X.
         */
        private double x; /**
         * The Y.
         */
        private double y; /**
         * The Z.
         */
        private double z;

        /**
         * Instantiates a new Builder.
         *
         * @param x the x
         * @param y the y
         * @param z the z
         */
        public Builder(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * Sets x.
         *
         * @param x the x
         * @return the x
         */
        public Builder setX(double x) {
            this.x = x;
            return this;
        }

        /**
         * Sets y.
         *
         * @param y the y
         * @return the y
         */
        public Builder setY(double y) {
            this.y = y;
            return this;
        }

        /**
         * Sets z.
         *
         * @param z the z
         * @return the z
         */
        public Builder setZ(double z) {
            this.z = z;
            return this;
        }

        /**
         * Add x builder.
         *
         * @param x the x
         * @return the builder
         */
        public Builder addX(double x) {
            return setX(this.x + x);
        }

        /**
         * Add y builder.
         *
         * @param y the y
         * @return the builder
         */
        public Builder addY(double y) {
            return setY(this.y + y);
        }

        /**
         * Add z builder.
         *
         * @param z the z
         * @return the builder
         */
        public Builder addZ(double z) {
            return setZ(this.z + z);
        }

        /**
         * Add builder.
         *
         * @param p the p
         * @return the builder
         */
        public Builder add(Point3D p) {
            return addX(p.x).addY(p.y).addZ(p.z);
        }

        /**
         * Multiply x builder.
         *
         * @param x the x
         * @return the builder
         */
        public Builder multiplyX(double x) {
            return setX(this.x * x);
        }

        /**
         * Multiply y builder.
         *
         * @param y the y
         * @return the builder
         */
        public Builder multiplyY(double y) {
            return setY(this.y * y);
        }

        /**
         * Multiply z builder.
         *
         * @param z the z
         * @return the builder
         */
        public Builder multiplyZ(double z) {
            return setZ(this.z * z);
        }

        /**
         * Multiply builder.
         *
         * @param m the m
         * @return the builder
         */
        public Builder multiply(double m) {
            return multiplyX(m).multiplyY(m).multiplyZ(m);
        }

        /**
         * Invert x builder.
         *
         * @return the builder
         */
        public Builder invertX() {
            return setX(-this.x);
        }

        /**
         * Invert y builder.
         *
         * @return the builder
         */
        public Builder invertY() {
            return setY(-this.y);
        }

        /**
         * Invert z builder.
         *
         * @return the builder
         */
        public Builder invertZ() {
            return setZ(-this.z);
        }

        /**
         * Invert builder.
         *
         * @return the builder
         */
        public Builder invert() {
            return invertX().invertY().invertZ();
        }

        /**
         * 바라볼 좌표가 x = 0, y = 0, z > 0이 되도록 바라볼 지점을 기준으로 회전합니다.
         *
         * @param center 바라볼 지점
         * @param lookTo 바라볼 좌표
         * @return the builder
         */
        public Builder rotateToCenter(@Nonnull Point3D center, @Nonnull Point3D lookTo) {
            add(center.invert());
            Point3D relative = lookTo.add(center.invert());
            double rx = -Math.toDegrees(Math.atan2(relative.z, relative.y)) + 90;
            relative = relative.edit().rotate(0, 0, 0, rx, 0, 0).build();
            double ry = -Math.toDegrees(Math.atan2(relative.z, relative.x)) + 90;
            return rotate(0, 0, 0, rx, ry, 0).add(center);
        }

        /**
         * 좌표공간 위의 점 {position}을 {center}를 중심으로 {rotation}만큼 회전합니다.
         * <p>
         * x : 위(+), 아래(-) 회전
         * <p>
         * y : 오른쪽(+), 왼쪽(-) 회전
         * <p>
         * z : 반시계(+), 시계(-) 회전
         * <p>
         *
         * @param cx 중심 좌표 x
         * @param cy 중심 좌표 y
         * @param cz 중심 좌표 z
         * @param rx x축 회전값
         * @param ry y축 회전값
         * @param rz z축 회전값
         * @return this builder
         */
        public Builder rotate(double cx, double cy, double cz, double rx, double ry, double rz) {

            Point2D px = new Point2D.Builder(y, z).rotate(cy, cz, rx).build();
            //dy축 -> x축, dz축 -> y축

            setY(px.x());
            setZ(px.y());

            Point2D py = new Point2D.Builder(x, z).rotate(cx, cz, ry).build();
            //dx축 -> x축, dz축 -> y축

            setX(py.x());
            setZ(py.y());

            Point2D pz = new Point2D.Builder(x, y).rotate(cx, cy, rz).build();
            //dx축 -> x축, dy축 -> y축,

            setX(pz.x());
            setY(pz.y());

            return this;
        }

        public Builder rotate(Point3D center, Point3D rotation) {
            return rotate(center.x, center.y, center.z, rotation.x, rotation.y, rotation.z);
        }

        @Override
        public Point3D build() {
            return new Point3D(x, y, z);
        }
    }

    public Point3D invert() {
        return edit().invert().build();
    }

    public Point3D add(Point3D add) {
        return edit().add(add).build();
    }

    public Point3D multiply(double multiplier) {
        return edit().multiply(multiplier).build();
    }

    /**
     * {z} 경계 상수
     */
    public static final double LIMIT_Z = 0.05;


    /**
     * {z} 값 민감도 상수
     */
    public static final double RESPONSIVENESS_Z = 0.121;

    /**
     * 좌표공간 위의 점을 좌표평면 위의 점으로 변환합니다.
     * <p>
     * <p>
     * z값에 따라 x,y의 값을 거리 상수{UNIT_Z_MULTIPLIER} / {z} 만큼 곱합니다.
     * <p>
     *
     * @return 좌표평면 위의 점
     */
    public Point2D convertTo2D() {

        double xzc = zCorrection(x, z);
        double yzc = zCorrection(y, z);

        return new Point2D(Double.isNaN(xzc) ? 0 : xzc, Double.isNaN(yzc) ? 0 : yzc);
    }

    public static Point3D average(Point3D... plane) {
        if (plane.length == 0) {
            throw new IllegalArgumentException("empty plane");
        }
        Point3D result = new Point3D(0, 0, 0);
        for (Point3D p : plane) {
            result = result.add(p.multiply(1.0 / plane.length));
        }
        return result;
    }

    /**
     * @return p1에서 p2로의 진행 비율
     */
    public static Point3D ratioDivide(Point3D p1, Point3D p2, double ratio) {
        return p1.add(p2.add(p1.invert()).multiply(ratio));
    }

    public static double zCorrection(double value, double z) {
        double correction;
        if (z >= LIMIT_Z) {
            double m = 1 / (RESPONSIVENESS_Z * z);
            correction = value * m;
        } else {
            correction = Double.NaN;
        }
        return correction;
    }

    /**
     * 설정된 값과 보정된 값을 이용하여 z를 구합니다.
     * 모두 양수여야 합니다.
     */
    public static double getZ(double value, double correctedValue) {
        if (value < 0 || correctedValue <= 0) {
            throw new IllegalArgumentException("not allowed negative and zero");
        }
        return (value / correctedValue) / RESPONSIVENESS_Z;
    }

    /**
     * 두 점중 한 점의 {z}좌표가 일정 수치 미만으로 작을 경우, 급격하게 커지는 현상을 막기 위해 {z}좌표의 값을 제한합니다.
     * 둘 다 {z}좌표 제한값 미만이면 길이가 2인 {null} 배열을 반환합니다.
     *
     * @param d1 첫째 점
     * @param d2 둘째 점
     * @return 보정된 선분
     */
    public static Point3D[] zCorrectionLine(Point3D d1, Point3D d2) {
        Point3D cv1 = d1;
        Point3D cv2 = d2;

        final double tdx = d2.x() - d1.x();
        final double tdy = d2.y() - d1.y();
        final double tdz = d2.z() - d1.z();


        final double zRatio = (LIMIT_Z - d1.z()) / tdz;

        if (d1.z() > LIMIT_Z && d2.z() < LIMIT_Z) {
            cv2 = d2.edit()
                    .setX(cv1.x() + tdx * zRatio)
                    .setY(cv1.y() + tdy * zRatio)
                    .setZ(LIMIT_Z)
                    .build();
        }
        if (d1.z() < LIMIT_Z && d2.z() > LIMIT_Z) {
            cv1 = d1.edit()
                    .setX(cv1.x() + tdx * zRatio)
                    .setY(cv1.y() + tdy * zRatio)
                    .setZ(LIMIT_Z)
                    .build();
        }
        if (d1.z() < LIMIT_Z && d2.z() < LIMIT_Z) {
            return new Point3D[]{null, null};
        }
        return new Point3D[]{cv1, cv2};
    }

    /**
     * @param screen        가려질 면
     * @param target        가릴 면
     * @param lightPosition 광원 위치
     * @return 광원에서 타깃을 지나 평면에 맺히는 그림자 영역
     */
    public static Point3D[] getProjection(Point3D[] screen, Point3D[] target, Point3D lightPosition) {

        //광원의 시점에서 바라보았을때 스크린의 꼭짓점 순회 방향과 타깃의 꼭짓점 순회 방향이 모두 시계 방향이고(1)
        //타깃 뒤에 있는가? (광원과 타깃의 모든 점을 연결한 선분 중 하나라도 범위 내에 있을경우)(2)
        if (Point2D.isClockwise(Arrays.stream(screen)
                .map(p -> p.edit()
                        .rotateToCenter(lightPosition, Point3D.average(screen))
                        .build()
                        .convertTo2DIgnoredCr())
                .toList())
            &&
            Point2D.isClockwise(Arrays.stream(target)
                    .map(p -> p.edit()
                            .rotateToCenter(lightPosition, Point3D.average(target))
                            .build()
                            .convertTo2DIgnoredCr())
                    .toList())

        ) {
            Point3D[] targetShadowScreen = Arrays.stream(target).map(p -> linePlaneIntersection(screen[0], screen[1], screen[2], p, lightPosition)).toArray(Point3D[]::new);
            Point3D[] finalShadow = duplicateArea(screen, targetShadowScreen);

            if (Arrays.stream(finalShadow).anyMatch(finalShadowVertex -> {
                Point3D intersection = linePlaneIntersection(target[0], target[1], target[2],
                        lightPosition,
                        finalShadowVertex
                );
                //null : 교차점 없음 -> false
                return intersection != null && lightPosition.distance(intersection) < lightPosition.distance(finalShadowVertex);
            })
            ) {
                return finalShadow;
            }
        }
        return new Point3D[0];
    }

    /**
     * 동일 평면 상의 두 좌표공간 위 도형 두 개의 겹치는 영역을 얻습니다.
     * 없으면 배열의 길이는 0입니다.
     *
     * @return 두 도형의 겹치는 영역
     */
    public static Point3D[] duplicateArea(@Nonnull Point3D[] a, @Nonnull Point3D[] b) {

        LoopedList<Point2D> flatA = Arrays.stream(a).map(Point3D::convertTo2DIgnoredCr).collect(Collectors.toCollection(LoopedList::new));
        LoopedList<Point2D> flatB = Arrays.stream(b).map(Point3D::convertTo2DIgnoredCr).collect(Collectors.toCollection(LoopedList::new));
        LoopedList<Point3D> verticesA = Arrays.stream(a).collect(Collectors.toCollection(LoopedList::new));
        LoopedList<Point3D> verticesB = Arrays.stream(b).collect(Collectors.toCollection(LoopedList::new));

        //special cases

        //둘 중 하나가 도형이 아닌가?
        if (flatA.size() < 3 || flatB.size() < 3) {
            return new Point3D[0];
        }

        //반시계 방향인가?
        if (!Point2D.isClockwise(flatA) || !Point2D.isClockwise(flatB)) {
            return new Point3D[0];
        }

        //도형 a를 도형 b가 감싸고 있는가?
        if (flatA.stream().allMatch(v -> Point2D.isInnerDot(flatB, v))) {
            return a;
        }

        //도형 b를 도형 a가 감싸고 있는가?
        if (flatB.stream().allMatch(v -> Point2D.isInnerDot(flatA, v))) {
            return b;
        }

        //도형 a와 도형 b가 독립되어 있다면, 교점이 전혀 존재하지 않아야 한다.
        boolean intersectionExists = false;
        for (int i = 0; i < flatA.size(); i++) {
            for (int j = 0; j < flatB.size(); j++) {
                Point2D intersection = Point2D.dotIntersection(flatA.get(i), flatA.get(i + 1), flatB.get(j), flatB.get(j + 1));
                intersectionExists = intersectionExists || intersection != null;
            }
        }
        if (!intersectionExists) {
            return new Point3D[0];
        }

        //도형 a의 한 직선과 도형 b의 한 직선이 일치하고, 방향이 서로 반대이면 교점은 없다.

        for (int i = 0; i < flatA.size(); i++) {
            for (int j = 0; j < flatB.size(); j++) {
                FunctionEase line1 = Point2D.getLine(flatA.get(i), flatA.get(i + 1));
                FunctionEase line2 = Point2D.getLine(flatB.get(j), flatB.get(j + 1));

                boolean equalsInclination = line1.getInclination(0) == line2.getInclination(0);
                boolean equalsValue = line1.apply(0) == line2.apply(0);
                boolean equalsAngle = Point2D.polarCoordinate(flatA.get(i), flatA.get(i + 1)).angle() == Point2D.polarCoordinate(flatB.get(j), flatB.get(j + 1)).angle();

                //방향이 다른데 선분이 일치하면 서로 반대로 진행하는 직선이 된다
                if (equalsInclination && equalsValue && !equalsAngle) {
                    return new Point3D[0];
                }
            }
        }


        // now start!!! (기준 도형 : a)
        // step 1. 시작 점을 구하고 다른 도형 내부에 있는지 조사한다. 초기 시작 점은 인덱스 -1이다.
        // step 2. 시작 점과 다음 점을 연결한 선분과 다른 도형과의 교점 개수를 c라고 하자.
        //         교차점이 어느 도형의 꼭짓점일경우, 해당 교차점은 다음 선분에서 교차점으로 간주하지 않지만 카운트는 실행한다.

        // step 3. 변수 d에 대해, 시작 점이 외부에 있는 경우 -1 (시작점이 다른 도형 위의 점일 때 포함), 아닐 경우 0이라 하자.
        //         ㄴ 교점이 있을경우, 교점을 시작점으로 간주하기때문에 시작점 이후 교점의 개수는 실제 교점의 개수보다 1 작다. 따라서 d를 이와 같이 정의한다.
        //
        //         c + d = -1일 때
        //         - 다음 점을 시작 점으로 간주하여 step 2부터 실행한다.
        //
        //         c + d = 0일 때
        //         - 다음 점을 추가한다.
        //         - 다음 점을 시작 점으로 간주하여 step 2부터 실행한다.
        //
        //         c + d = 1일 때
        //         - 해당 선분과의 교점 중 시작 점과의 거리가 먼 것을 추가한다.
        //         - 그 점을 지나는 다른 선분의 양 끝 점 중 인덱스가 작은 것으로 시작 점을 변경하여 step 2부터 실행한다.
        //
        //         c + d = 2일 때
        //         - 시작 점과 끝 점을 연결한 선분 위에 다른 도형의 꼭짓점이 1개 있는 경우 발생한다.
        //         - 해당 꼭짓점을 추가하고 c + d = 1일때와 같이 시작점을 변경하여 step 2부터 실행한다.
        //
        //
        //
        //         c + d = 3일 때 (선분에 매달린 다이아몬드 형태)
        //
        //                       /\
        //          start-------ㅇ-ㅇ---------end
        //                       \/
        //
        //         - 시작 점과 끝 점을 연결한 선분 위에 다른 도형의 꼭짓점이 2개 있는 경우 발생한다.
        //         - 해당 꼭짓점 중 비율이 높은 것을 추가하고, c + d = 1일때와 같이 시작점을 변경하여 step 2부터 실행한다.
        //
        // step 4. 점 추가 단계에서 추가된 점이 세개 이상이고 점이 제자리로 돌아오면 추가를 중지하고 반복문을 빠져나온다.

        List<Point3D> result = new ArrayList<>();

        int startPointIndex = -1;

        LoopedList<Point2D> target = flatA;
        LoopedList<Point2D> others = flatB;
        LoopedList<Point3D> targetVertices = verticesA;
        LoopedList<Point3D> otherVertices = verticesB;

        do {
            Point2D start = target.get(startPointIndex);
            Point2D next = target.get(startPointIndex + 1);
            Point3D startV = targetVertices.get(startPointIndex);
            Point3D nextV = targetVertices.get(startPointIndex + 1);

            int d = Point2D.isInnerDot(others, start) ? 0 : -1;
            int c = 0;
            int maxRatioIndex = 0;
            double maxRatio = 0;

            for (int i = 0; i < others.size(); i++) {

                Point2D dotIntersection = Point2D.dotIntersection(start, next, others.get(i), others.get(i + 1));
                if (dotIntersection != null) {
                    double ratio;

                    if (start.x() == next.x()) {
                        ratio = AdvancedMath.getRatio(start.y(), next.y(), dotIntersection.y());
                    } else {
                        ratio = AdvancedMath.getRatio(start.x(), next.x(), dotIntersection.x());
                    }

                    if (ratio > 0) {
                        maxRatioIndex = ratio > maxRatio ? i : maxRatioIndex;

                        // 한 직선 위에 다른 도형의 꼭짓점이 있는경우, ratio 가 같을수 있다.
                        // 따라서 아래와 같이 꼭짓점과 교점이 일치할 경우, 다음 인덱스를 받아온다 !!!!

                        if (Objects.equals(others.get(i + 1), dotIntersection)) {
                            maxRatioIndex++;
                        }
                        maxRatio = Math.max(ratio, maxRatio);
                    }

                    c++;

                }
            }

            switch (c + d) {
                case -1 -> startPointIndex++;
                case 0 -> {
                    result.add(nextV);
                    startPointIndex++;
                }
                case 1, 2, 3 -> {
                    result.add(Point3D.ratioDivide(startV, nextV, maxRatio));
                    startPointIndex = maxRatioIndex;

                    LoopedList<Point2D> pt = target;
                    target = others;
                    others = pt;

                    LoopedList<Point3D> ptv = targetVertices;
                    targetVertices = otherVertices;
                    otherVertices = ptv;
                }
                default ->
                        throw new UnsupportedOperationException("why c + d equals " + (c + d) + "??\nprovided flats\n a : " + flatA.stream().map(v -> "[" + v + "]").toList() + "\n b : " + flatB.stream().map(v -> "[" + v + "]").toList());
            }
        } while (result.size() < 4 || (!Objects.equals(result.get(result.size() - 1), result.get(0)) && result.size() < 10000));
        if (result.size() >= 10000) {
            throw new VertexOverflowException("TOO MANY VERTICES!!!!\nprovided flats\n a : " + flatA.stream().map(v -> "[" + v + "]").toList() + "\n b : " + flatB.stream().map(v -> "[" + v + "]").toList());
        }

        // V자 방지, 첫 점과 끝 점이 동일하므로 중복 데이터 제거
        // V자 형태란, 꼭짓점이 다른 도형 위에 있고 대상을 바꾸지 않는 형태를 말함.
        //
        // 해당 알고리즘대로 적용 시, A 위치에서 꼭짓점 A는 외부 점으로 간주되어 대상을 두 번 바꾸는데, 이때 동일 점이 두 번 추가된다.
        //
        // V자 형태 그림
        //
        //      \      /                  /
        //       \    /                  /
        //        \  /   (도형 내부)      /
        //         \/ A                /
        //ㅇ--------------------------ㅇ
        //
        //               (도형 외부)
        //

        return new LinkedHashSet<>(result).toArray(Point3D[]::new);
    }

    /**
     * 두 점을 지나는 직선과 평면의 교점을 얻습니다.
     * 서로 평행하여 교점이 존재하지 않으면 null 입니다.
     *
     * @param p1 평면이 지나는 점
     * @param p2 평면이 지나는 점
     * @param p3 평면이 지나는 점
     * @param d1 직선이 지나는 점
     * @param d2 직선이 지나는 점
     * @return 직선과 평면의 교차점
     */
    public static Point3D linePlaneIntersection(Point3D p1, Point3D p2, Point3D p3, Point3D d1, Point3D d2) {

        Flat flat = Flat.of(p1, p2, p3);
        double a = flat.a();
        double b = flat.b();
        double c = flat.c();
        double d = flat.d();
        double x1 = d1.x();
        double y1 = d1.y();
        double z1 = d1.z();
        double x2 = d2.x();
        double y2 = d2.y();
        double z2 = d2.z();

        double t = -(a * x1 + b * y1 + c * z1 + d)
                   / (a * (x2 - x1) + b * (y2 - y1) + c * (z2 - z1));
        if (Double.isInfinite(t)) {
            return null;
        }
        return new Point3D(x1 + t * (x2 - x1), y1 + t * (y2 - y1), z1 + t * (z2 - z1));
    }

    public double distanceOfPlane(Point3D d1, Point3D d2, Point3D d3) {
        Flat flat = Flat.of(d1, d2, d3);
        final double A = flat.a();
        final double B = flat.b();
        final double C = flat.c();
        final double D = flat.d();
        //Ax + By + Cz + D = 0

        return Math.abs(A * x + B * y + C * z - D) * AdvancedMath.rSqrt(A * A + B * B + C * C);
    }

    /**
     * z를 제거하여 좌표평면 위의 점으로 변환합니다.
     *
     * @return 좌표평면 위의 점
     */
    public Point2D convertTo2DIgnoredCr() {
        return new Point2D(x, y);
    }

    /**
     * 두 점 사이 거리를 계산합니다.
     *
     * @param target 측정할 대상
     * @return 거리 double
     */
    public double distance(Point3D target) {
        return Math.hypot(Math.hypot(target.x - x, target.y - y), target.z - z);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point3D p) {
            return p.x == x && p.y == y && p.z == z;
        }
        return false;
    }

    @Nonnull
    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}

