package io.beanmapper.utils.provider;

public class CharacterProvider implements Provider<Character> {
    @Override
    public Character getDefault() {
        return '\0';
    }

    @Override
    public Character getMaximum() {
        return '~';
    }
}
