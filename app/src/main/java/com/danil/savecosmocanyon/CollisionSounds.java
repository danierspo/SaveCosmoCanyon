package com.danil.savecosmocanyon;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Sound;

/**
 * Created by mfaella on 04/03/16.
 */
public class CollisionSounds {
    public static Sound meteorToGround;
    public static Sound meteorToScraper;
    public static Sound magicMeteorToScraper;
    public static Sound summonMeteorToScraper;

    public static void init(Audio audio) {
        meteorToGround = audio.newSound("meteortoground.wav");
        meteorToScraper = audio.newSound("metalbang.wav");
        magicMeteorToScraper = audio.newSound("cure.wav");
        summonMeteorToScraper = audio.newSound("break.wav");
    }
}
