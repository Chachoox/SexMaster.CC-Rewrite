package me.pignol.swift.api.value;

import me.pignol.swift.client.event.events.ValueEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

public class Value<T> {

    private String name;
    private String description;

    private T value;
    private T defaultValue;

    private T min;
    private T max;

    private Predicate<T> visibility;

    public Value(String name, T value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.description = "";
    }

    public Value(String name, T value, T min, T max) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.min = min;
        this.max = max;
        this.description = "";
    }

    public Value(String name, T value, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.description = "";
        this.visibility = visibility;
    }

    public Value(String name, T value, T min, T max, Predicate<T> visibility) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.min = min;
        this.max = max;
        this.description = "";
        this.visibility = visibility;
    }

    public Value(String name, T value, String description) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.description = description;
    }

    public Value(String name, T value, T min, T max, String description) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
        this.min = min;
        this.max = max;
        this.description = description;
    }

    public <T> T clamp(T value, T min, T max) {
        return ((Comparable) value).compareTo(min) < 0 ? min : (((Comparable) value).compareTo(max) > 0 ? max : value);
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        if (min != null && max != null) {
            if (((Number)min).floatValue() > ((Number) value).floatValue()) {
                value = min;
            }
            if (((Number)max).floatValue() < ((Number) value).floatValue()) {
                value = max;
            }
        }
        this.value = value;
        MinecraftForge.EVENT_BUS.post(new ValueEvent(this));
    }

    public int getEnum(String input) {
        for (int i = 0; i < this.value.getClass().getEnumConstants().length; i++) {
            final Enum e = (Enum) this.value.getClass().getEnumConstants()[i];
            if (e.name().equalsIgnoreCase(input)) {
                return i;
            }
        }
        return -1;
    }


    public void setEnumValue(String value) {
        Enum[] array;
        for (int length = (array = ((Enum) getValue()).getClass().getEnumConstants()).length, i = 0; i < length; i++) {
            if (array[i].name().equalsIgnoreCase(value)) {
                setValue((T) array[i]);
            }
        }
    }

    public void increment() {
        Enum[] array;
        for (int length = (array = ((Enum) value).getClass().getEnumConstants()).length, i = 0; i < length; i++) {
            if (array[i].name().equalsIgnoreCase(getFixedName())) {
                i++;
                if (i > array.length - 1) {
                    i = 0;
                }
                setEnumValue(array[i].toString());
            }
        }
    }

    public void decrement() {
        Enum[] array;
        for (int length = (array = ((Enum) getValue()).getClass().getEnumConstants()).length, i = 0; i < length; i++) {
            if (array[i].name().equalsIgnoreCase(getFixedName())) {
                i--;
                if (i < 0) {
                    i =  array.length - 1;
                }
                setEnumValue(array[i].toString());
            }
        }
    }

    public String getFixedName() {
        Enum currentValue = ((Enum) value);
        return Character.toString(currentValue.name().charAt(0)) + currentValue.name().toLowerCase().replaceFirst(Character.toString(currentValue.name().charAt(0)).toLowerCase(), "");
    }

    public String getCapitalizedName() {
        return this.getName().charAt(0) + this.getName().toLowerCase().replaceFirst(Character.toString(this.getName().charAt(0)).toLowerCase(), "");
    }

    public String getCapitalizedValue() {
        return this.getValue().toString().charAt(0) + this.getValue().toString().toLowerCase().replaceFirst(Character.toString(this.getValue().toString().charAt(0)).toLowerCase(), "");
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isVisible() {
        if (visibility == null) {
            return true;
        }
        return visibility.test(getValue());
    }

}
