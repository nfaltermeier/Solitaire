package solitaire.game;

import org.jetbrains.annotations.Nullable;
import solitaire.graphics.IDrawable;

import java.awt.*;
import java.io.File;

public class Game implements IDrawable {
    // Represents a complete game with all it's CardStacks and data

    @Nullable
    private File loadedFrom;

    @Override
    public void draw(Graphics g, int x, int y) {

    }
}
