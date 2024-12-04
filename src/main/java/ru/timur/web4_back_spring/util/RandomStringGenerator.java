package ru.timur.web4_back_spring.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomStringGenerator {

    private final SecureRandom randomizer = new SecureRandom();

    public String generate() {
        return generate(this.randomizer.nextInt(25, 75 + 1));
    }

    public String generate(Integer seqLen) {
        return this.randomizer
                .ints(seqLen, 0x21, 0x7F)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
