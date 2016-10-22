package com.ocronite.pillapilla;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

class LinkLabel extends Label {

    static private final Color tempColor = new Color();

    private LinkLabelStyle style;
    private ClickListener clickListener;
    private CharSequence url;
    private LinkLabelListener listener;

    LinkLabel(CharSequence text, CharSequence url, Skin skin) {
        super(text, skin);
        init(url);
        style = skin.get(LinkLabelStyle.class);
    }

    private void init (CharSequence linkUrl) {
        this.url = linkUrl;

        addListener(clickListener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked (InputEvent event, float x, float y) {
                super.clicked(event, x, y);

                if (listener == null) {
                    Gdx.net.openURI(url.toString());
                } else {
                    listener.clicked(url.toString());
                }
            }
        });
    }

    @Override
    public void draw (Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Drawable underline = style.underline;
        if (underline != null) {
            Color color = tempColor.set(getColor());
            color.a *= parentAlpha;
            if(clickListener.isOver() && style.fontColor != null) color.mul(style.fontColor);
            batch.setColor(color);
            underline.draw(batch, getX(), getY(), getWidth(), 1);
        }
    }

    CharSequence getUrl () {
        return url;
    }

    void setListener(LinkLabelListener listener) {
        this.listener = listener;
    }

    interface LinkLabelListener {
        void clicked(String url);
    }

    private static class LinkLabelStyle extends LabelStyle {
        Drawable underline;
    }
}