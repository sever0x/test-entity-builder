package io.github.sever0x.testentitybuilder.generation.model;

import io.github.sever0x.testentitybuilder.annotation.GenerateBuilder;
import io.github.sever0x.testentitybuilder.generation.model.TestEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@GenerateBuilder
public class ExtendedTypesEntity {
    private byte byteValue;
    private short shortValue;
    private float floatValue;
    private double doubleValue;
    private char charValue;

    private Byte byteWrapper;
    private Short shortWrapper;
    private Float floatWrapper;
    private Double doubleWrapper;
    private Character charWrapper;

    private List<String> stringList;
    private Set<Integer> integerSet;
    private Map<String, String> stringMap;

    private LocalDate dateValue;
    private LocalDateTime dateTimeValue;
    private BigDecimal bigDecimalValue;
    private io.github.sever0x.testentitybuilder.generation.model.TestEnum enumValue;

    public ExtendedTypesEntity() {
    }

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public short getShortValue() {
        return shortValue;
    }

    public void setShortValue(short shortValue) {
        this.shortValue = shortValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public char getCharValue() {
        return charValue;
    }

    public void setCharValue(char charValue) {
        this.charValue = charValue;
    }

    public Byte getByteWrapper() {
        return byteWrapper;
    }

    public void setByteWrapper(Byte byteWrapper) {
        this.byteWrapper = byteWrapper;
    }

    public Short getShortWrapper() {
        return shortWrapper;
    }

    public void setShortWrapper(Short shortWrapper) {
        this.shortWrapper = shortWrapper;
    }

    public Float getFloatWrapper() {
        return floatWrapper;
    }

    public void setFloatWrapper(Float floatWrapper) {
        this.floatWrapper = floatWrapper;
    }

    public Double getDoubleWrapper() {
        return doubleWrapper;
    }

    public void setDoubleWrapper(Double doubleWrapper) {
        this.doubleWrapper = doubleWrapper;
    }

    public Character getCharWrapper() {
        return charWrapper;
    }

    public void setCharWrapper(Character charWrapper) {
        this.charWrapper = charWrapper;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public Set<Integer> getIntegerSet() {
        return integerSet;
    }

    public void setIntegerSet(Set<Integer> integerSet) {
        this.integerSet = integerSet;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public LocalDate getDateValue() {
        return dateValue;
    }

    public void setDateValue(LocalDate dateValue) {
        this.dateValue = dateValue;
    }

    public LocalDateTime getDateTimeValue() {
        return dateTimeValue;
    }

    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        this.dateTimeValue = dateTimeValue;
    }

    public BigDecimal getBigDecimalValue() {
        return bigDecimalValue;
    }

    public void setBigDecimalValue(BigDecimal bigDecimalValue) {
        this.bigDecimalValue = bigDecimalValue;
    }

    public TestEnum getEnumValue() {
        return enumValue;
    }

    public void setEnumValue(io.github.sever0x.testentitybuilder.generation.model.TestEnum enumValue) {
        this.enumValue = enumValue;
    }
}