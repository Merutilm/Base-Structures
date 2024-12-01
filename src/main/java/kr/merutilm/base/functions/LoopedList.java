package kr.merutilm.base.functions;

import javax.annotation.Nonnull;

import kr.merutilm.base.exception.EmptyListException;

import java.util.*;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

/**
 * ArrayList 에 기반된 루프 리스트.
 * 해당 Element 로 인덱스 0에서 시작하여 무한히 순환합니다.
 * 배열이 비어있지 않으면 IndexOutOfBounds 예외를 던지지 않습니다.
 */
public class LoopedList<E> implements List<E> {

    private final List<E> looped;

    public LoopedList() {
        looped = new ArrayList<>();
    }

    public LoopedList(List<E> list) {
        looped = new ArrayList<>(list);
    }

    public LoopedList(E[] array) {
        looped = new ArrayList<>(Arrays.stream(array).toList());
    }

    @Override
    public int size() {
        return looped.size();
    }

    @Override
    public boolean isEmpty() {
        return looped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return looped.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return looped.iterator();
    }

    @Override
    public Object[] toArray() {
        return new Object[]{looped};
    }

    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return a;
    }

    @Override
    public boolean add(E e) {
        return looped.add(e);
    }

    public void push() {
        if (isEmpty()) {
            throw new EmptyListException();
        }
        E e = get(size() - 1);
        remove(e);
        add(0, e);
    }

    public void pull() {
        if (isEmpty()) {
            throw new EmptyListException();
        }
        E e = get(0);
        remove(e);
        add(e);
    }

    public void push(int it) {
        for (int i = 0; i < it; i++) {
            push();
        }
    }

    public void pull(int it) {
        for (int i = 0; i < it; i++) {
            pull();
        }
    }

    @Override
    public boolean remove(Object o) {
        return looped.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return new HashSet<>(looped).containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        return looped.addAll(c);
    }

    @Override
    public boolean addAll(int index, @Nonnull Collection<? extends E> c) {
        return looped.addAll(index, c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        return looped.removeAll(c);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        return looped.retainAll(c);
    }

    @Override
    public void clear() {
        looped.clear();
    }

    @Override
    public E get(int index) {
        if (isEmpty()) {
            throw new EmptyListException();
        }
        int r = convertIndex(index);
        return looped.get(r);
    }

    private int convertIndex(int index) {
        int r = (index % size());
        while (r < 0) {
            r += size();
        }
        return r;
    }

    @Override
    public E set(int index, E element) {
        return looped.set(index, element);
    }

    @Override
    public void add(int index, E element) {
        looped.add(index, element);
    }

    @Override
    public E remove(int index) {
        return looped.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return looped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return looped.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return looped.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return looped.listIterator(index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return looped.subList(fromIndex, toIndex);
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return looped.toArray(generator);
    }

    public static <E> List<E> of(E[] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(LoopedList::new));
    }
}
