package unity.map;

import arc.func.Intc;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Structs;
import mindustry.content.StatusEffects;
import mindustry.content.UnitTypes;
import mindustry.game.SpawnGroup;
import mindustry.game.Waves;
import mindustry.type.UnitType;
import unity.content.units.MonolithUnitTypes;
import unity.mod.Faction;

public final class UnityWaves {
    private static final ObjectMap<Faction, WaveBuilder> generators = new ObjectMap();

    public static Seq<SpawnGroup> generate(Faction faction, float difficulty, Rand rand, boolean attack) {
        WaveBuilder gen = (WaveBuilder)generators.get(faction);
        return gen != null ? gen.get(difficulty, rand, attack) : Waves.generate(difficulty, rand, attack);
    }

    static {
        generators.put(Faction.monolith, (WaveBuilder)(difficulty, rand, attack) -> {
            UnitType[][] species = new UnitType[][]{{MonolithUnitTypes.stele, MonolithUnitTypes.pedestal, MonolithUnitTypes.pilaster, MonolithUnitTypes.pylon, MonolithUnitTypes.monument, MonolithUnitTypes.colossus, MonolithUnitTypes.bastion}, {UnitTypes.dagger, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign, UnitTypes.reign, UnitTypes.reign}, {UnitTypes.crawler, UnitTypes.atrax, UnitTypes.spiroct, UnitTypes.arkyid, UnitTypes.toxopid, UnitTypes.toxopid, UnitTypes.toxopid}, {UnitTypes.flare, UnitTypes.horizon, UnitTypes.zenith, rand.chance((double)0.5F) ? UnitTypes.quad : UnitTypes.antumbra, rand.chance((double)0.5F) ? UnitTypes.antumbra : UnitTypes.eclipse, rand.chance(0.3) ? UnitTypes.quad : UnitTypes.eclipse, rand.chance(0.1) ? UnitTypes.antumbra : UnitTypes.eclipse}};
            Seq<SpawnGroup> out = new Seq();
            int cap = 150;
            float shieldStart = 40.0F;
            final float shieldsPerWave = 20.0F + difficulty * 40.0F;
            float[] scaling = new float[]{1.0F, 2.0F, 3.0F, 4.0F, 5.0F, 6.0F, 7.0F};
            Intc createProgression = (start) -> {
                UnitType[] curSpecies = (UnitType[])Structs.random(species);
                final int curTier = 0;
                final int i = start;

                while(i < cap) {
                    final int next = rand.random(8, 16) + (int)Mathf.lerp(5.0F, 0.0F, difficulty) + curTier * 4;
                    final float shieldAmount = Math.max(((float)i - shieldStart) * shieldsPerWave, 0.0F);
                    final int space = start == 0 ? 1 : rand.random(1, 2);
                    out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]) {
                        {
                            this.unitAmount = i == start ? 1 : 6 / (int)scaling[curTier];
                            this.begin = i;
                            this.end = i + next >= cap ? Integer.MAX_VALUE : i + next;
                            this.max = 13;
                            this.unitScaling = (difficulty < 0.4F ? rand.random(2.5F, 5.0F) : rand.random(1.0F, 4.0F)) * scaling[curTier];
                            this.shields = shieldAmount;
                            this.shieldScaling = shieldsPerWave;
                            this.spacing = space;
                        }
                    });
                    out.add(new SpawnGroup(curSpecies[Math.min(curTier, curSpecies.length - 1)]) {
                        {
                            this.unitAmount = 3 / (int)scaling[curTier];
                            this.begin = i + next - 1;
                            this.end = i + next + rand.random(6, 10);
                            this.max = 6;
                            this.unitScaling = rand.random(2.0F, 4.0F);
                            this.spacing = rand.random(2, 4);
                            this.shields = shieldAmount / 2.0F;
                            this.shieldScaling = shieldsPerWave;
                        }
                    });
                    i += next + 1;
                    if (curTier < 5 || rand.chance(0.2) && (double)difficulty > 0.8) {
                        ++curTier;
                    }

                    curTier = Math.min(curTier, 5);
                    if (rand.chance(0.3)) {
                        curSpecies = (UnitType[])Structs.random(species);
                    }
                }

            };
            createProgression.get(0);

            for(int step = 5 + rand.random(5); step <= cap; step += (int)((float)rand.random(15, 30) * Mathf.lerp(1.0F, 0.5F, difficulty))) {
                createProgression.get(step);
            }

            final int bossWave = (int)((float)rand.random(50, 70) * Mathf.lerp(1.0F, 0.7F, difficulty));
            final int bossSpacing = (int)((float)rand.random(25, 40) * Mathf.lerp(1.0F, 0.6F, difficulty));
            int bossTier = (double)difficulty < 0.6 ? 5 : 6;
            out.add(new SpawnGroup(((UnitType[])Structs.random(species))[bossTier]) {
                {
                    this.unitAmount = 1;
                    this.begin = bossWave;
                    this.spacing = bossSpacing;
                    this.end = Integer.MAX_VALUE;
                    this.max = 16;
                    this.unitScaling = (float)bossSpacing;
                    this.shieldScaling = shieldsPerWave;
                    this.effect = StatusEffects.boss;
                }
            });
            out.add(new SpawnGroup(((UnitType[])Structs.random(species))[bossTier]) {
                {
                    this.unitAmount = 1;
                    this.begin = bossWave + rand.random(3, 5) * bossSpacing;
                    this.spacing = bossSpacing;
                    this.end = Integer.MAX_VALUE;
                    this.max = 16;
                    this.unitScaling = (float)bossSpacing;
                    this.shieldScaling = shieldsPerWave;
                    this.effect = StatusEffects.boss;
                }
            });
            final int finalBossStart = 120 + rand.random(30);
            out.add(new SpawnGroup(((UnitType[])Structs.random(species))[bossTier]) {
                {
                    this.unitAmount = 1;
                    this.begin = finalBossStart;
                    this.spacing = bossSpacing / 2;
                    this.end = Integer.MAX_VALUE;
                    this.unitScaling = (float)bossSpacing;
                    this.shields = 500.0F;
                    this.shieldScaling = shieldsPerWave * 4.0F;
                    this.effect = StatusEffects.boss;
                }
            });
            out.add(new SpawnGroup(((UnitType[])Structs.random(species))[bossTier]) {
                {
                    this.unitAmount = 1;
                    this.begin = finalBossStart + 15;
                    this.spacing = bossSpacing / 2;
                    this.end = Integer.MAX_VALUE;
                    this.unitScaling = (float)bossSpacing;
                    this.shields = 500.0F;
                    this.shieldScaling = shieldsPerWave * 4.0F;
                    this.effect = StatusEffects.boss;
                }
            });
            if (attack && (double)difficulty >= (double)0.5F) {
                int amount = Mathf.random(1, 3 + (int)(difficulty * 2.0F));

                for(int i = 0; i < amount; ++i) {
                    final int wave = Mathf.random(3, 20);
                    out.add(new SpawnGroup(UnitTypes.mega) {
                        {
                            this.unitAmount = 1;
                            this.begin = wave;
                            this.end = wave;
                            this.max = 16;
                        }
                    });
                }
            }

            int shift = Math.max((int)(difficulty * 20.0F - 5.0F), 0);

            for(SpawnGroup group : out) {
                group.begin -= shift;
                group.end -= shift;
            }

            return out;
        });
    }

    private interface WaveBuilder {
        Seq<SpawnGroup> get(float var1, Rand var2, boolean var3);
    }
}
