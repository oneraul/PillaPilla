package com.ocronite.pillapilla;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

class OptionsButton<T extends Enum<T>> extends TextButton {

    private final String buttonText;
    private final T[] options;
    private int currentOption;

    OptionsButton(String buttonText, T... options) {
        super("", PillaPilla.skin());

        this.buttonText = buttonText;
        this.options = options;
        this.setText(buttonText + options[currentOption].toString());
        this.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                callback();
            }
        });
    }

    private void callback() {
        currentOption++;
        if (currentOption >= options.length)
            currentOption = 0;

        this.setText(buttonText + options[currentOption].toString());
    }

    T getOption() {
        return options[currentOption];
    }

    void setOption(T option) {
        if(option != null) {

            int i = 0;
            while(!(options[currentOption] == option)) {
                callback();

                i++;
                if(i >= options.length) {
                    throw new IllegalArgumentException("The OptionButton doesn't contain the option \"" + option.toString() + "\"");
                }
            }
        }
    }
}