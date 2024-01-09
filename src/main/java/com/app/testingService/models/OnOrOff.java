package com.app.testingService.models;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OnOrOff {
    ON(true),
    OFF(false),
    NONE(false);

    private boolean f;

    public static OnOrOff findByName(String name){
            return Arrays.stream(values())
                    .filter(x->x.name().equals(name))
                    .findAny().orElse(NONE);
    }

}
