package unity.type;

import arc.func.Cons;
import mindustry.type.Weapon;

public class CloneableSetWeapon extends Weapon {
    public CloneableSetWeapon(String name) {
        super(name);
    }

    public Weapon set(Cons<Weapon> con) {
        Weapon w = this.copy();
        con.get(w);
        return w;
    }

    public Weapon flp(Cons<Weapon> con) {
        Weapon w = this.copy();
        w.name = w.name + "-flipped";
        w.flipSprite = true;
        con.get(w);
        return w;
    }
}
