package kr.merutilm.base.struct;


public interface Struct<T extends Record & Struct<T>> {
    StructBuilder<T> edit();
}
