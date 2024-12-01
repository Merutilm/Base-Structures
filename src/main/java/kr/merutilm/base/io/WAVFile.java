package kr.merutilm.base.io;

import javax.sound.sampled.*;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ArrayFunction;
import kr.merutilm.base.util.ConsoleUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;


public record WAVFile(float sampleRate, short[][] waveform) {

    private static final int PS = 2500;
    private static final int STL = 500;
    private static final int FFT_MIN_FREQUENCY = 32;
    private static final int FFT_MAX_FREQUENCY = 2048;
    /**
     * The minimum distance ratio determined to be valid.
     * It is propotional to the maximum value,
     * and is calculated using the extreme value of the function.
     */
    private static final double FFT_MIN_DISTANCE_RATIO = 0.4;
    /**
     * Multiplies this value and frequency, and calculates using the result value.
     * It is faster when the value is high, but decreses the accuracy of result.
     * If value is 1, calculates all of frequencies from min to max.
     */
    private static final double FFT_ACCURACY_MULTIPLIER = 1.005;


    public int length() {
        return waveform[0].length;
    }

    public double lengthSec() {
        return length() / sampleRate;
    }

    public short sample(int channel, int frame) {
        return waveform[channel][frame];
    }

    public static WAVFile getMono(AudioInputStream inputStream) throws IOException {

        WAVFile wavFile = get(inputStream);
        short[] monoWaveform = wavFile.getMonoWaveform();

        return new WAVFile(wavFile.sampleRate(), new short[][]{monoWaveform});
    }

    public short[] getMonoWaveform() {
        short[] monoWaveform = new short[waveform()[0].length];

        for (int frame = 0; frame < waveform()[0].length; frame++) { // 평균 구하기
            short sum = 0;
            for (short[] channel : waveform()) {
                sum += (short) (channel[frame] / waveform().length);
            }
            monoWaveform[frame] = sum;
        }

        return monoWaveform;

    }

    public static WAVFile get(String filePath) {
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filePath))) {
            return get(stream);
        } catch (IOException | UnsupportedAudioFileException e) {
            ConsoleUtils.logError(e);
            throw new IllegalArgumentException();
        }
    }

    public static WAVFile get(AudioInputStream inputStream) throws IOException {
        int frameLength = (int) inputStream.getFrameLength();
        int numChannels = inputStream.getFormat().getChannels();
        int bits = inputStream.getFormat().getSampleSizeInBits();

        short[][] waveform = new short[numChannels][frameLength];
        int sampleIndex = 0;

        byte[] allBytes = inputStream.readAllBytes();
        int t = 0;
        double highest = Short.MIN_VALUE;
        while (t < allBytes.length) {
            for (int channel = 0; channel < numChannels; channel++) {

                if (bits / 8 == 3) {
                    t++;
                }

                byte low = allBytes[t];
                t++;
                byte high = allBytes[t];
                t++;
                short sample16Bit = get16BitSample(high, low);
                waveform[channel][sampleIndex] = sample16Bit;
                highest = Math.max(highest, sample16Bit);
            }
            sampleIndex++;
        }
        return new WAVFile(inputStream.getFormat().getSampleRate(), waveform);
    }

    public WAVFile pitch(DoubleUnaryOperator operator) {
        short[][] calc = new short[waveform.length][];
        short[][] result = new short[waveform.length][];

        for (int i = 0; i < waveform.length; i++) {
            short[] channels = waveform[i];
            calc[i] = new short[channels.length];

            int rIndex = 0;
            int len = partsLength();
            double prevMultiplier = 1;

            for (int part = 0; part < len; part++) {
                double multiplier = operator.applyAsDouble((double) part / len);
                short[] parts = pitch(i, part, prevMultiplier, multiplier);

                while (rIndex + parts.length >= calc[i].length) {
                    calc[i] = exp2xArr(calc[i]);
                }

                arraySetAll(calc[i], parts, rIndex);
                rIndex += parts.length;
                prevMultiplier = multiplier;
            }

            result[i] = new short[rIndex + 1];
            System.arraycopy(calc[i], 0, result[i], 0, rIndex);

        }
        return new WAVFile(sampleRate, result);
    }

    public WAVFile pitch(double multiplier) {
        short[][] result = new short[waveform.length][];

        for (int i = 0; i < waveform.length; i++) {
            short[] channels = waveform[i];
            result[i] = new short[channels.length];
            int rIndex = 0;
            for (int part = 0; part < partsLength(); part++) {
                short[] parts = pitch(i, part, multiplier, multiplier);
                arraySetAll(result[i], parts, rIndex);
                rIndex += parts.length;
            }
        }
        return new WAVFile(sampleRate, result);
    }

    private short[] pitch(int channel, int part, double prevMultiplier, double multiplier) {
        short[] result = new short[PS];
        short[] channels = waveform[channel];

        for (int j = 0; j < result.length; j++) {

            int pmIndex = (int) AdvancedMath.restrict(0, channels.length - 1, (part - 1) * PS + (j + result.length) * prevMultiplier);
            int cmIndex = (int) AdvancedMath.restrict(0, channels.length - 1, part * PS + j * multiplier);

            short prevValue = channels[pmIndex];
            short currValue = channels[cmIndex];

            short value = j < STL ? (short) AdvancedMath.ratioDivide(prevValue, currValue, (double) j / STL, Ease.INOUT_QUAD.fun()) : currValue;

            result[j] = value;
        }

        return result;
    }

    private int partsLength() {
        return waveform[0].length / PS;
    }

    public WAVFile speed(DoubleUnaryOperator operator) {
        short[][] calc = new short[waveform.length][];
        short[][] result = new short[waveform.length][];

        for (int i = 0; i < waveform.length; i++) {
            short[] channels = waveform[i];
            calc[i] = new short[channels.length];

            int rIndex = 0;
            int len = partsLength();
            double prevMultiplier = 1;

            for (int part = 0; part < len; part++) {
                double multiplier = operator.applyAsDouble((double) part / len);
                short[] parts = speed(i, part, prevMultiplier, multiplier);

                while (rIndex + parts.length >= calc[i].length) {
                    calc[i] = exp2xArr(calc[i]);
                }

                arraySetAll(calc[i], parts, rIndex);
                rIndex += parts.length;
                prevMultiplier = multiplier;
            }

            result[i] = new short[rIndex + 1];
            System.arraycopy(calc[i], 0, result[i], 0, rIndex);
        }
        return new WAVFile(sampleRate, result);
    }

    public WAVFile speed(double multiplier) {
        short[][] result = new short[waveform.length][];

        for (int i = 0; i < waveform.length; i++) {
            short[] channels = waveform[i];
            result[i] = new short[(int) (channels.length / multiplier)];
            int rIndex = 0;

            for (int part = 0; part < partsLength(); part++) {
                short[] parts = speed(i, part, multiplier, multiplier);
                arraySetAll(result[i], parts, rIndex);
                rIndex += parts.length;
            }
        }
        return new WAVFile(sampleRate, result);
    }

    private short[] speed(int channel, int part, double prevMultiplier, double multiplier) {

        short[] channels = waveform[channel];
        short[] result = new short[(int) (PS / multiplier)];
        for (int j = 0; j < result.length; j++) {

            int pmIndex = AdvancedMath.restrict(0, channels.length - 1, (part - 1) * PS + (j + (int) (PS / prevMultiplier)));
            int cmIndex = AdvancedMath.restrict(0, channels.length - 1, part * PS + j);

            short prevValue = channels[pmIndex];
            short currValue = channels[cmIndex];

            short value = j < STL ? (short) AdvancedMath.ratioDivide(prevValue, currValue, (double) j / STL, Ease.INOUT_QUAD.fun()) : currValue;

            result[j] = value;
        }

        return result;
    }

    public WAVFile rate(DoubleUnaryOperator operator) {
        short[][] calc = new short[waveform.length][];
        short[][] result = new short[waveform.length][];

        for (int i = 0; i < waveform.length; i++) {

            short[] channels = waveform[i];
            calc[i] = new short[channels.length];

            int rIndex = 0;
            int len = partsLength();
            for (int part = 0; part < len; part++) {
                double multiplier = operator.applyAsDouble((double) part / len);
                short[] parts = rate(i, part, multiplier);

                while (rIndex + parts.length >= calc[i].length) {
                    calc[i] = exp2xArr(calc[i]);
                }

                arraySetAll(calc[i], parts, rIndex);
                rIndex += parts.length;
            }
            result[i] = new short[rIndex + 1];
            System.arraycopy(calc[i], 0, result[i], 0, rIndex);
        }
        return new WAVFile(sampleRate, result);
    }


    public WAVFile rate(double multiplier) {
        short[][] result = new short[waveform.length][];
        for (int channel = 0; channel < waveform.length; channel++) {
            result[channel] = new short[(int) (length() / multiplier)];
            for (int frame = 0; frame < result[channel].length; frame++) {
                result[channel][frame] = waveform[channel][(int) (frame * multiplier)];
            }
        }
        return new WAVFile(sampleRate, result);
    }

    private short[] rate(int channel, int part, double multiplier) {

        short[] channels = waveform[channel];
        short[] result = new short[(int) (PS / multiplier)];

        for (int j = 0; j < result.length; j++) {
            int cmIndex = (int) AdvancedMath.restrict(0, length() - 1, part * PS + j * multiplier);
            result[j] = channels[cmIndex];
        }

        return result;
    }


    public enum WaveType {
        SINE(v -> Math.sin(Math.PI * 2 * v)),
        LINEAR(v -> 1 - v % 1);
        private final DoubleUnaryOperator function;

        WaveType(DoubleUnaryOperator function) {
            this.function = function;
        }

    }

    public static short[] generateFrequencies(WaveType waveType, double sampleRate, double offsetSec, double lengthSec, short amplitude, int... frequencies) {
        short[] result = new short[(int) (sampleRate * lengthSec)];

        if (frequencies.length == 0) {
            return result;
        }

        int amp = amplitude == -1 ? Short.MAX_VALUE / frequencies.length : amplitude;

        for (int i = 0; i < lengthSec * sampleRate; i++) {
            short value = 0;
            for (double w : frequencies) {
                double v = waveType.function.applyAsDouble(w * (i / sampleRate + offsetSec));


                value += (short) (amp * v);
            }

            result[i] = value;
        }
        return result;
    }

    /**
     * Do FFT on a given waveform.
     * <li> It requires double array, you can {@link ArrayFunction#toDoubleShortArray(short[]) transform} short array to double array here.
     */
    public static int[] fft(double[] waveform, double offSec, double durSec, float sampleRate) {

        int off = (int) (sampleRate * offSec);
        int len = (int) (sampleRate * durSec);

        off = Math.min(off, waveform.length - 1);
        len = Math.min(len, waveform.length - 1 - off);

        if (len == off || len <= 0) {
            return new int[0];
        }

        double[] samples = new double[len];
        System.arraycopy(waveform, off, samples, 0, len);

        double[] distanceByFrequency = new double[FFT_MAX_FREQUENCY];

        for (int f = FFT_MIN_FREQUENCY; f < FFT_MAX_FREQUENCY; f++) {
            double x = 0;
            double y = 0;
            for (int t = 0; t < samples.length; t++) {
                x += samples[t] * Math.cos(2 * Math.PI * f * t / sampleRate) / len;
                y += samples[t] * Math.sin(2 * Math.PI * f * t / sampleRate) / len;
            }

            distanceByFrequency[f] = AdvancedMath.hypot(x, y);
        }

        List<Double> distances = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();

        int pf = 0;
        int cf = FFT_MIN_FREQUENCY;

        while (true) {
            int nf = (int) Math.max(cf + 1, cf * FFT_ACCURACY_MULTIPLIER);

            if (nf >= FFT_MAX_FREQUENCY) {
                break;
            }

            double curr = distanceByFrequency[cf];
            double prev = distanceByFrequency[pf];
            double next = distanceByFrequency[nf];

            if (curr - prev > 0 && next - curr < 0) { // 극댓값
                distances.add(curr);
                frequencies.add(cf);
            }

            pf = cf;
            cf = nf;
        }

        double max = Arrays.stream(distanceByFrequency).max().orElse(0);

        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < distances.size(); i++) {
            if (distances.get(i) > max * FFT_MIN_DISTANCE_RATIO) {
                result.add(frequencies.get(i));
            }
        }

        return result.stream()
                .mapToInt(e -> e)
                .toArray();
    }


    public void export(String filePath) {
        export(0, 1, filePath);
    }


    public void export(double startRatio, double endRatio, String filePath) {

        int channels = waveform.length;
        int maxFrameSize = (int) ((endRatio - startRatio) * length());
        int frameOffset = (int) (startRatio * length());

        AudioFormat format = new AudioFormat(sampleRate(), 16, channels, true, false);
        byte[] data = new byte[2 * channels * maxFrameSize];

        for (int frame = 0; frame < maxFrameSize; frame++) {
            for (int channel = 0; channel < channels; channel++) {
                short sample = waveform[channel][frameOffset + frame];
                data[2 * channel + frame * 2 * channels] = (byte) (sample & 0x00ff);
                data[2 * channel + 1 + frame * 2 * channels] = (byte) (sample >> 8);
            }

        }

        try (AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), format, data.length / format.getFrameSize())) {
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filePath));
        } catch (IOException e) {
            ConsoleUtils.logError(e);
        }
    }

    public WAVFile cut(double startSec, double endSec) {
        int off = (int) (startSec * sampleRate);
        int len = (int) ((endSec - startSec) * sampleRate);
        short[][] result = new short[waveform.length][len];
        System.arraycopy(waveform[0], off, result[0], 0, len);
        System.arraycopy(waveform[1], off, result[1], 0, len);
        return new WAVFile(sampleRate, result);
    }

    public void volume(int volume) {
        for (int i = 0; i < waveform.length; i++) {
            for (int j = 0; j < waveform[i].length; j++) {
                waveform[i][j] = (short) Math.min(Short.MAX_VALUE, waveform[i][j] * volume / 100.0);
            }
        }
    }

    public WAVFile function(Function<Short, Number> function) {
        short[][] waveform = waveform();
        short[][] result = new short[waveform.length][];
        for (int channel = 0; channel < waveform.length; channel++) {
            result[channel] = new short[length()];
            for (int frame = 0; frame < result[channel].length; frame++) {
                result[channel][frame] = (short) AdvancedMath.restrict(Short.MIN_VALUE, Short.MAX_VALUE, function.apply(waveform[channel][frame]).doubleValue());
            }
        }
        return new WAVFile(sampleRate, result);
    }

    public WAVFile reverse() {
        short[][] result = new short[waveform.length][];
        for (int channel = 0; channel < waveform.length; channel++) {
            result[channel] = new short[length()];
            for (int frame = 0; frame < result[channel].length; frame++) {
                result[channel][frame] = waveform[channel][length() - frame - 1];
            }
        }
        return new WAVFile(sampleRate, result);
    }

    public WAVFile copy() {
        return new WAVFile(sampleRate, copyWave());
    }

    public short[][] copyWave() {
        short[][] current = new short[waveform.length][length()];

        for (int channel = 0; channel < current.length; channel++) {
            System.arraycopy(waveform[channel], 0, current[channel], 0, length());
        }
        return current;
    }

    private static short get16BitSample(byte high, byte low) {
        return (short) (((high << 8) & 0xff00) + (low & 0x00ff));
    }

    private short[] exp2xArr(short[] arr) {
        short[] newArr = new short[arr.length * 2];
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        return newArr;
    }

    private static void arraySetAll(short[] target, short[] setter, int index) {
        System.arraycopy(setter, 0, target, index, setter.length);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WAVFile w) {
            return sampleRate == w.sampleRate() && Arrays.deepEquals(this.waveform(), w.waveform());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(sampleRate);
        result = 31 * result + Arrays.deepHashCode(waveform);
        return result;
    }

    @Override
    public String toString() {
        return sampleRate + " | " + Arrays.deepToString(waveform);
    }

}
