package unity.entities;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.logic.LogicOp;
import mindustry.world.Edges;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.blocks.logic.LogicBlock;
import mindustry.world.blocks.logic.MemoryBlock;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.blocks.power.PowerGraph;

public class Emp {
    static IntSet collided = new IntSet(409);
    static ObjectSet<PowerGraph> graphs = new ObjectSet();
    static Seq<Building> last = new Seq();
    static Seq<Building> next = new Seq();
    static boolean hit = false;
    public static boolean hitPowerGrid;
    public static boolean hitDisconnect;

    public static void hitTile(float x, float y, Team team, float validRange, float duration, float amount, float logicIntensity, int logicInstructions, float disconnectRange, float scanRange, int scans) {
        hit = false;
        hitDisconnect = false;
        hitPowerGrid = false;
        if (validRange > 0.0F) {
            Vars.indexer.eachBlock((Team)null, x, y, validRange, (b) -> b.team != team && !collided.contains(b.pos()) && b.block.hasPower, (building) -> {
                if (building.power != null) {
                    if (graphs.add(building.power.graph)) {
                        building.power.graph.useBatteries(amount);
                        handleBuilding(building, duration);
                        last.add(building);
                        collided.add(building.pos());

                        for(int i = 0; i < scans; ++i) {
                            for(Building b : last) {
                                if (b.power != null) {
                                    IntSeq links = b.power.links;
                                    Point2[] nearby = Edges.getEdges(b.block.size);

                                    for(Point2 point : nearby) {
                                        Building other = Vars.world.build(b.tile.x + point.x, b.tile.y + point.y);
                                        if (other != null && other.block != null && other.block.hasPower && other.within(x, y, scanRange) && collided.add(other.pos())) {
                                            next.add(other);
                                            handleBuilding(other, duration);
                                        }
                                    }

                                    for(int j = 0; j < links.size; ++j) {
                                        int pos = links.get(j);
                                        Building other = Vars.world.build(pos);
                                        if (other != null && other.within(x, y, scanRange) && collided.add(other.pos())) {
                                            next.add(other);
                                            handleBuilding(other, duration);
                                        }
                                    }
                                }
                            }

                            last.set(next);
                            next.clear();
                        }
                    }

                    hitPowerGrid = true;
                    hit = true;
                }

            });
        }

        last.clear();
        graphs.clear();
        if (disconnectRange > 0.0F && (hit || logicIntensity > 0.0F && logicInstructions > 0)) {
            Vars.indexer.eachBlock((Team)null, x, y, disconnectRange, (b) -> b.team != team, (building) -> {
                if ((building.block.hasPower || building.block.outputsPower) && building.power != null && hit) {
                    for(int i = 0; i < building.power.links.size; ++i) {
                        int p = building.power.links.get(i);
                        Building s = Vars.world.build(p);
                        if (s != null && s.power != null) {
                            s.power.links.removeValue(building.pos());
                            last.add(s);
                        }
                    }

                    building.power.links.clear();
                    PowerGraph origin = new PowerGraph();
                    origin.reflow(building);
                    graphs.add(origin);

                    for(Building build : last) {
                        if (!graphs.contains(build.power.graph)) {
                            PowerGraph n = new PowerGraph();
                            n.reflow(build);
                            graphs.add(n);
                        }
                    }

                    last.clear();
                    graphs.clear();
                    hitDisconnect = true;
                }

                if (building instanceof MemoryBlock.MemoryBuild) {
                    MemoryBlock.MemoryBuild mb = (MemoryBlock.MemoryBuild)building;
                    if (logicIntensity > 0.0F && logicInstructions > 0) {
                        for(int i = 0; i < logicInstructions; ++i) {
                            int index = Mathf.random(0, mb.memory.length - 1);
                            double[] var10000 = mb.memory;
                            var10000[index] += (double)Mathf.range(logicIntensity);
                        }

                        hitDisconnect = true;
                    }
                }

                if (building instanceof LogicBlock.LogicBuild) {
                    LogicBlock.LogicBuild lb = (LogicBlock.LogicBuild)building;
                    if (logicIntensity > 0.0F && logicInstructions > 0) {
                        hitDisconnect = true;
                        StringBuilder build = new StringBuilder();
                        String[] lines = lb.code.split("\n");

                        for(int i = 0; i < logicInstructions; ++i) {
                            StringBuilder builder = new StringBuilder();
                            int index = Mathf.random(0, lines.length - 1);
                            String[] line = lines[index].split("\\s+");
                            switch (line[0]) {
                                case "set":
                                    if (Strings.canParseFloat(line[2])) {
                                        float par = Strings.parseFloat(line[2], 0.0F);
                                        par += Mathf.range(logicIntensity);
                                        line[2] = par + "";
                                    } else if (logicIntensity > 256.0F && Mathf.chance((double)((logicIntensity - 256.0F) / 64.0F))) {
                                        line[2] = Mathf.random(Float.MAX_VALUE) + "";
                                    }
                                    break;
                                case "op":
                                    String a = line[3];
                                    String b = line[4];
                                    if (Strings.canParseFloat(a)) {
                                        float par = Strings.parseFloat(a);
                                        par += Mathf.range(logicIntensity);
                                        line[3] = par + "";
                                    } else if (logicIntensity > 256.0F && Mathf.chance((double)((logicIntensity - 256.0F) / 64.0F))) {
                                        line[3] = Mathf.random(Float.MAX_VALUE) + "";
                                    }

                                    if (Strings.canParseFloat(b)) {
                                        float par = Strings.parseFloat(b);
                                        par += Mathf.range(logicIntensity);
                                        line[4] = par + "";
                                    } else if (logicIntensity > 256.0F && Mathf.chance((double)((logicIntensity - 256.0F) / 64.0F))) {
                                        line[4] = Mathf.random(Float.MAX_VALUE) + "";
                                    }

                                    if (logicIntensity > 128.0F && Mathf.chance((double)((logicIntensity - 128.0F) / 64.0F))) {
                                        LogicOp[] ops = LogicOp.values();
                                        line[1] = ops[Mathf.random(0, ops.length - 1)].name();
                                    }
                                    break;
                                case "control":
                                    String conType = line[1];
                                    if (conType.equals("enabled")) {
                                        line[3] = "0";
                                    }
                                    break;
                                case "draw":
                                    if (!line[1].equals("color")) {
                                        for(int j = 2; j < 8; ++j) {
                                            String a = line[j];
                                            if (Strings.canParseFloat(a)) {
                                                float par = Strings.parseFloat(a);
                                                par += Mathf.range(logicIntensity);
                                                if (par < 0.0F) {
                                                    par += par;
                                                }

                                                par = Math.max(0.0F, par);
                                                line[j] = par + "";
                                            } else if (logicIntensity > 256.0F && Mathf.chance((double)((logicIntensity - 256.0F) / 64.0F))) {
                                                line[j] = Mathf.random(100.0F) + "";
                                            }
                                        }
                                    } else {
                                        for(int j = 2; j < 6; ++j) {
                                            String a = line[j];
                                            if (Strings.canParseFloat(a)) {
                                                float par = Strings.parseFloat(a);
                                                par += Mathf.range(logicIntensity);
                                                par = Mathf.mod(par, 255.0F);
                                                line[j] = par + "";
                                            } else if (logicIntensity > 256.0F && Mathf.chance((double)((logicIntensity - 256.0F) / 64.0F))) {
                                                line[j] = Mathf.random(255.0F) + "";
                                            }
                                        }
                                    }
                                    break;
                                case "jump":
                                    if (Strings.canParseInt(line[1]) && Mathf.round((logicIntensity - 30.0F) / 5.0F) >= 1) {
                                        int par = Strings.parseInt(line[1]);
                                        par += Mathf.range(Mathf.round((logicIntensity - 30.0F) / 5.0F));
                                        par = Mathf.clamp(par, 0, lines.length - 1);
                                        line[1] = par + "";
                                    }
                                    break;
                                default:
                                    for(int j = 1; j < line.length; ++j) {
                                        if (Strings.canParseFloat(line[j])) {
                                            float par = Strings.parseFloat(line[j]);
                                            par += Mathf.range(logicIntensity);
                                            line[j] = par + "";
                                        }
                                    }
                            }

                            for(int j = 0; j < line.length; ++j) {
                                String s = line[j];
                                builder.append(s);
                                if (j < line.length - 1) {
                                    builder.append(" ");
                                }
                            }

                            lines[index] = builder.toString();
                        }

                        for(int i = 0; i < lines.length; ++i) {
                            build.append(lines[i]);
                            if (i < lines.length - 1) {
                                build.append("\n");
                            }
                        }

                        lb.code = build.toString();
                        lb.updateCode(lb.code);
                    }
                }

            });
        }

        graphs.clear();
        last.clear();
        next.clear();
        collided.clear();
    }

    public static void handleBuilding(Building build, float duration) {
        if (build.block.hasPower) {
            if (build instanceof PowerGenerator.GeneratorBuild) {
                PowerGenerator.GeneratorBuild gb = (PowerGenerator.GeneratorBuild)build;
                gb.productionEfficiency = 0.0F;
                if (build instanceof ImpactReactor.ImpactReactorBuild) {
                    ImpactReactor.ImpactReactorBuild irb = (ImpactReactor.ImpactReactorBuild)build;
                    ImpactReactor r = (ImpactReactor)build.block;
                    irb.warmup = Mathf.clamp(irb.warmup - duration * r.warmupSpeed);
                }
            }

            if (build.block.consumes.hasPower() && build instanceof ReloadTurret.ReloadTurretBuild) {
                ReloadTurret.ReloadTurretBuild rtb = (ReloadTurret.ReloadTurretBuild)build;
                rtb.reload = 0.0F;
            }

            build.enabled = false;
            build.enabledControlTime = Math.max(duration, build.enabledControlTime);
        }
    }
}
