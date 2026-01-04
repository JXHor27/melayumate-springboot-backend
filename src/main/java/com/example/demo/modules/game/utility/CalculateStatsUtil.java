package com.example.demo.modules.game.utility;

import com.example.demo.modules.game.dao.CalculatedStats;
import com.example.demo.modules.game.entity.CharacterTemplate;


public class CalculateStatsUtil {

    /**
     * Calculate the stats for a character at a given level based on its template.
     *
     * @param template The character template containing base stats.
     * @param level    The current level of the character.
     * @return The {@link CalculatedStats} object containing the computed stats.
     */
    public static CalculatedStats calculateStatsForLevel(CharacterTemplate template, int level) {
        CalculatedStats stats = new CalculatedStats();
        stats.setHp(template.getBaseHp() + (int)Math.floor(level * 2.5));
        stats.setAtk(template.getBaseAttack() + (int)Math.floor(level * 1.2));
        stats.setDef(template.getBaseDefense() + (int)Math.floor(level * 1.2));
        stats.setSpd(template.getBaseSpeed() + (int)Math.floor(level * 0.8));
        return stats;
    }

}
