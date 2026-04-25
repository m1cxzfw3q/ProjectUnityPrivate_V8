package unity.type;

import arc.math.Interp;

public class AntiCheatVariables {
    private static final Interp defaultIn = new Interp.Pow(2);
    public final float damageThreshold;
    public final float maxDamageThreshold;
    public final Interp curveType;
    public final float maxDamageTaken;
    public final float resistStart;
    public final float resistScl;
    public final float resistDuration;
    public final float resistTime;
    public final float invincibilityDuration;
    public final int invincibilityArray;

    public AntiCheatVariables(float damageThreshold, float maxDamageThreshold, Interp curveType, float maxDamageTaken, float resistStart, float resistScl, float resistDuration, float resistTime, float invincibilityDuration, int invincibilityArray) {
        this.damageThreshold = damageThreshold;
        this.maxDamageThreshold = maxDamageThreshold;
        this.curveType = curveType;
        this.maxDamageTaken = maxDamageTaken;
        this.resistStart = resistStart;
        this.resistScl = resistScl;
        this.resistDuration = resistDuration;
        this.resistTime = resistTime;
        this.invincibilityDuration = invincibilityDuration;
        this.invincibilityArray = invincibilityArray;
    }

    public AntiCheatVariables(float dt, float mdthr, float mdtkn, float rStrt, float rScl, float rd, float rt, float inD, int inf) {
        this(dt, mdthr, defaultIn, mdtkn, rStrt, rScl, rd, rt, inD, inf);
    }
}
