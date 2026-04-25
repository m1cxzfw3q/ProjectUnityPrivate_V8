package unity.entities.bullet.misc;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import unity.entities.ExpOrbs;
import unity.world.blocks.defense.turrets.BlockOverdriveTurret;
import unity.world.blocks.exp.ExpHolder;

public class BlockStatusEffectBulletType extends BasicBulletType {
    public float strength = 2.0F;
    public int amount = 3;
    public boolean upgrade = false;

    public BlockStatusEffectBulletType(float speed, float damage) {
        super(speed, damage);
    }

    public void draw(Bullet b) {
    }

    public void update(Bullet b) {
        Building target = null;
        boolean buffing = false;
        float phaseHeat = 0.0F;
        float phaseBoost = 0.0F;
        float phaseExpBoost = 0.0F;
        float efficiency = 0.0F;
        Entityc var9 = b.owner;
        if (var9 instanceof BlockOverdriveTurret.BlockOverdriveTurretBuild) {
            BlockOverdriveTurret.BlockOverdriveTurretBuild bb = (BlockOverdriveTurret.BlockOverdriveTurretBuild)var9;
            target = bb.target;
            buffing = bb.buffing;
            phaseHeat = bb.phaseHeat;
            phaseBoost = ((BlockOverdriveTurret)bb.block).phaseBoost;
            phaseExpBoost = ((BlockOverdriveTurret)bb.block).phaseExpBoost;
            efficiency = bb.efficiency();
        }

        if (buffing) {
            if (b.x == target.x && b.y == target.y) {
                this.strength = Mathf.lerpDelta(this.strength, 3.0F + phaseHeat * phaseBoost, 0.02F);
                if (b.timer(0, 179.0F)) {
                    if (this.upgrade) {
                        this.addExp(target, (5.0F + phaseBoost * phaseExpBoost) * Time.delta * efficiency);
                    } else {
                        this.buff(target, (this.strength + phaseHeat * phaseBoost) * Time.delta * efficiency);
                    }
                }
            }
        } else {
            this.strength = 1.0F;
        }

    }

    public void buff(Building b, float intensity) {
        if (b.health < b.maxHealth) {
            b.applyBoost(intensity, 180.0F);
            b.heal(intensity);
        } else {
            b.applyBoost(intensity * 2.0F, 180.0F);
        }

    }

    public void addExp(Building b, float intensity) {
        if (b instanceof ExpHolder) {
            ExpHolder exp = (ExpHolder)b;
            exp.handleExp(Mathf.round((float)exp.getExp() * 0.1F) / 10 * Mathf.round(intensity));
            ExpOrbs.spreadExp(b.x, b.y, this.amount);
        }

    }
}
