package kr.merutilm.base.struct;


public interface StructBuilder<T extends Record & Struct<T>> {
    T build();
}
