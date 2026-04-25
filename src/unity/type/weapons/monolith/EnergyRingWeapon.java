package unity.type.weapons.monolith;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.type.Weapon;
import unity.graphics.UnityDrawf;
import unity.graphics.UnityPal;

public class EnergyRingWeapon extends Weapon {
    public final Seq<Ring> rings = new Seq(4);
    public float aggressionScale = 3.0F;
    public float aggressionSpeed = 0.2F;
    public float cooldownSpeed = 0.08F;
    public Color eyeColor;
    public float eyeRadius;

    public EnergyRingWeapon() {
        super("");
        this.eyeColor = UnityPal.monolithLight;
        this.eyeRadius = 2.5F;
        this.mountType = (weapon) -> new EnergyRingMount((EnergyRingWeapon)weapon);
    }

    public void update(Unit unit, WeaponMount mount) {
        super.update(unit, mount);
        EnergyRingMount m = (EnergyRingMount)mount;
        m.aggression = m.target == null && !m.shoot ? Mathf.lerpDelta(m.aggression, 0.0F, this.cooldownSpeed) : Mathf.lerpDelta(m.aggression, 1.0F, this.aggressionSpeed);
        m.time += Time.delta + m.aggression * Time.delta * this.aggressionScale;
    }

    public void draw(Unit unit, WeaponMount mount) {
        float z = Draw.z();
        Draw.z(z + this.layerOffset);
        EnergyRingMount m = (EnergyRingMount)mount;
        float rot = unit.rotation - 90.0F;
        Tmp.v1.trns(rot, this.x, this.y).add(unit);

        for(Ring ring : this.rings) {
            int sign = Mathf.sign(ring.flip ^ unit.id % 2 == 0);
            float rotation = ring.angleOffset * (float)sign + (ring.rotate ? m.time * (float)sign : rot + mount.rotation);
            Lines.stroke(ring.thickness, ring.color);

            for(int i = 0; i < ring.divisions; ++i) {
                float angleStep = 360.0F / (float)ring.divisions;
                UnityDrawf.arcLine(Tmp.v1.x, Tmp.v1.y, ring.radius, ring.divisions == 1 ? 360.0F : angleStep - ring.divisionSeparation, rotation + angleStep * (float)i);
            }

            for(int i = 0; i < ring.spikes; ++i) {
                float spikeRotation = rotation + ring.spikeRotOffset + 360.0F / (float)ring.spikes * (float)i;
                Tmp.v2.trns(spikeRotation, 0.0F, ring.radius + ring.spikeOffset).add(Tmp.v1);
                Drawf.tri(Tmp.v2.x, Tmp.v2.y, ring.spikeWidth, ring.spikeLength, spikeRotation + 90.0F);
            }
        }

        rot += m.rotation;
        Tmp.v1.add(Tmp.v2.trns(rot, this.shootX, this.shootY));
        Draw.color(this.eyeColor);
        Fill.circle(Tmp.v1.x, Tmp.v1.y, this.eyeRadius);
        Draw.z(z);
    }

    public void drawOutline(Unit unit, WeaponMount mount) {
    }

    public static class Ring {
        public Color color;
        public float thickness;
        public float radius;
        public boolean rotate;
        public float rotateSpeed;
        public float angleOffset;
        public boolean flip;
        public int divisions;
        public float divisionSeparation;
        public int spikes;
        public float spikeOffset;
        public float spikeRotOffset;
        public float spikeWidth;
        public float spikeLength;

        public Ring() {
            this.color = UnityPal.monolithLight;
            this.thickness = 1.5F;
            this.radius = 4.5F;
            this.rotate = true;
            this.rotateSpeed = 2.0F;
            this.angleOffset = 0.0F;
            this.divisions = 1;
            this.divisionSeparation = 12.0F;
            this.spikes = 0;
            this.spikeOffset = 1.0F;
            this.spikeWidth = 1.5F;
            this.spikeLength = 3.0F;
        }
    }

    public static class EnergyRingMount extends WeaponMount {
        public float time;
        public float aggression;

        public EnergyRingMount(EnergyRingWeapon weapon) {
            super(weapon);
        }
    }
}
