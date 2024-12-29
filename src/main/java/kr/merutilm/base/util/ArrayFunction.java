package kr.merutilm.base.util;

import java.util.*;

public final class ArrayFunction {
    private ArrayFunction() {

    }


    /**
     * 정렬된 리스트에서 해당 숫자가 들어갈 인덱스 찾기<p>
     * 같은 값일 경우, 첫 번째 인덱스를 리턴합니다.<p>
     * <strong>Complexity : O(log n)</strong>
     */
    public static int searchIndex(List<? extends Number> list, double element) {
        return searchIndex(list, element, 0, list.size());
    }

    /**
     * 정렬된 리스트에서 해당 숫자가 들어갈 인덱스 찾기<p>
     * 같은 값일 경우, 마지막 인덱스를 리턴합니다.<p>
     * <strong>Complexity : O(log n)</strong>
     */
    public static int searchLastIndex(List<? extends Number> list, double element) {
        return searchLastIndex(list, element, 0, list.size());
    }

    private static int searchIndex(List<? extends Number> list, double element, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return startIndex;
        }
        if (startIndex + 1 == endIndex) {
            double e = list.get(startIndex).doubleValue();
            if (e < element) {
                return endIndex;
            } else {
                return startIndex;
            }
        }

        int avgIndex = (startIndex + endIndex) / 2;
        if (list.get(avgIndex).doubleValue() < element) {
            return searchIndex(list, element, avgIndex, endIndex);
        } else {
            return searchIndex(list, element, startIndex, avgIndex);
        }
    }

    private static int searchLastIndex(List<? extends Number> list, double element, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return endIndex;
        }
        if (startIndex + 1 == endIndex) {
            double e = list.get(startIndex).doubleValue();
            if (e <= element) {
                return endIndex;
            } else {
                return startIndex;
            }
        }

        int avgIndex = (startIndex + endIndex) / 2;
        if (list.get(avgIndex).doubleValue() <= element) {
            return searchLastIndex(list, element, avgIndex, endIndex);
        } else {
            return searchLastIndex(list, element, startIndex, avgIndex);
        }
    }

    /**
     * 정렬된 리스트에서 해당 숫자가 들어갈 인덱스 찾기<p>
     * 같은 값일 경우, 첫 번째 인덱스를 리턴합니다.<p>
     * <strong>Complexity : O(log n)</strong>
     */
    public static int searchIndex(double[] arr, double element) {
        return searchIndex(arr, element, 0, arr.length);
    }

    private static int searchIndex(double[] arr, double element, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return startIndex;
        }
        if (startIndex + 1 == endIndex) {
            double e = arr[startIndex];
            if (e < element) {
                return endIndex;
            } else {
                return startIndex;
            }
        }

        int avgIndex = (startIndex + endIndex) / 2;
        if (arr[avgIndex] < element) {
            return searchIndex(arr, element, avgIndex, endIndex);
        } else {
            return searchIndex(arr, element, startIndex, avgIndex);
        }
    }

    public static short[] toShortArray(List<Short> list) {
        short[] result = new short[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static short[][] toDeepShortArray(List<List<Short>> list) {
        short[][] result = new short[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            result[i] = toShortArray(list.get(i));
        }
        return result;
    }

    public static double[] toDoubleShortArray(short[] arr){

        double[] result = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i] = arr[i];
        }
        return result;
    }
    
    public static long[] exp2xArr(long[] arr){
        long[] arr2 = new long[arr.length * 2];
        System.arraycopy(arr, 0, arr2, 0, arr.length);
        return arr2;
    }
    public static int[] exp2xArr(int[] arr){
        int[] arr2 = new int[arr.length * 2];
        System.arraycopy(arr, 0, arr2, 0, arr.length);
        return arr2;
    }
    public static double[] exp2xArr(double[] arr){
        double[] arr2 = new double[arr.length * 2];
        System.arraycopy(arr, 0, arr2, 0, arr.length);
        return arr2;
    }
}
