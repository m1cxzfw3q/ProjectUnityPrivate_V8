package unity.ui.dialogs;

import arc.Core;
import arc.flabel.FLabel;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.ui.Label;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.ui.Links;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import unity.mod.ContributorList;
import unity.mod.ContributorList.ContributionType;

public class CreditsDialog extends BaseDialog {
    static Func<String, String> stringf = (value) -> Core.bundle.get("mod." + value);

    public CreditsDialog() {
        super("@credits");
        this.shown(() -> Core.app.post(this::setup));
        this.shown(this::setup);
        this.onResize(this::setup);
    }

    void setup() {
        this.cont.clear();
        this.buttons.clear();
        float h = Core.graphics.isPortrait() ? 90.0F : 80.0F;
        float w = Core.graphics.isPortrait() ? 330.0F : 600.0F;
        Table in = new Table();
        ScrollPane pane = new ScrollPane(in);

        for(ModLink link : CreditsDialog.ModLink.all) {
            Table table = new Table(Tex.underline);
            table.margin(0.0F);
            table.table((img) -> {
                img.image().height(h - 5.0F).width(40.0F).color(link.entry.color);
                img.row();
                img.image().height(5.0F).width(40.0F).color(link.entry.color.cpy().mul(0.8F, 0.8F, 0.8F, 1.0F));
            }).expandY();
            table.table((i) -> {
                i.background(Tex.buttonEdge3);
                i.image(link.entry.icon);
            }).size(h - 5.0F, h);
            table.table((inset) -> {
                inset.add("[accent]" + link.entry.title).growX().left();
                inset.row();
                inset.labelWrap(link.entry.description).width(w - 100.0F).color(Color.lightGray).growX();
            }).padLeft(8.0F);
            table.button(Icon.link, () -> {
                if (!Core.app.openURI(link.entry.link)) {
                    Vars.ui.showErrorMessage("@linkfail");
                    Core.app.setClipboardText(link.entry.link);
                }

            }).size(h - 5.0F, h);
            in.add(table).size(w, h).padTop(5.0F).row();
        }

        this.shown(() -> Time.run(1.0F, () -> Core.scene.setScrollFocus(pane)));
        this.cont.add(pane).growX();
        this.addCloseButton();
        this.buttons.button("@credits", CreditsDialog::showList).size(200.0F, 64.0F);
        if (Core.graphics.isPortrait()) {
            for(Cell<?> cell : this.buttons.getCells()) {
                cell.width(140.0F);
            }
        }

    }

    public static void showList() {
        TextureRegion error = Core.atlas.find("error");
        BaseDialog dialog = new BaseDialog("@credits");
        dialog.cont.table((t) -> {
            ((Label)t.add("@mod.credits.text").fillX().pad(3.0F).wrap().get()).setAlignment(1);
            t.row();
            t.table((tb) -> {
                tb.add("@mod.credits.bottom-text-one");
                tb.image(Core.atlas.find("unity-EyeOfDarkness")).padLeft(5.0F).padRight(3.0F);
                tb.add("@mod.credits.bottom-text-two");
            }).fillX().pad(3.0F);
            t.row();
        }).pad(3.0F);
        dialog.cont.row();
        dialog.cont.pane((b) -> {
            for(ContributorList.ContributionType type : ContributionType.all) {
                Seq<String> list = ContributorList.getBy(type);
                if (list.size > 0) {
                    b.table(Tex.button, (t) -> {
                        t.add((CharSequence)stringf.get(type.name())).pad(3.0F).center();
                        t.row();
                        t.image().color(Pal.accent).fillX().growX().padBottom(5.0F);
                        t.row();
                        t.pane((p) -> {
                            for(String c : list) {
                                String noLang = c.replaceAll("\\(([^\\)]*)\\)", "").replace(" ", "");
                                p.button((bt) -> {
                                    TextureRegion icon = Core.atlas.find("unity-" + noLang);
                                    if (icon != error) {
                                        bt.image(icon).padRight(3.0F);
                                    }

                                    bt.add(new FLabel("{wave}{rainbow}[lightgray]" + c)).left().pad(3.0F).padLeft(6.0F).padRight(6.0F);
                                }, Styles.transt, () -> {
                                    String name = noLang;
                                    if (ContributorList.githubAliases.containsKey(noLang)) {
                                        name = (String)ContributorList.githubAliases.get(noLang);
                                    }

                                    if (c.equals("Evl")) {
                                        Core.app.openURI("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                                    } else {
                                        Core.app.openURI("https://github.com/" + name);
                                    }

                                });
                                p.row();
                            }

                        });
                    }).pad(6.0F).top().width(Math.max((float)Core.graphics.getWidth() / 5.0F, 320.0F));
                }
            }

        }).fillX();
        dialog.addCloseButton();
        dialog.show();
    }

    static enum ModLink {
        discord("avant-discord", "https://discord.gg/V6ygvgGVqE", Icon.discord, Color.valueOf("7289da")),
        changelog("changelog", "https://github.com/AvantTeam/ProjectUnity/releases", Icon.list, Pal.accent),
        github("avant-github", "https://github.com/AvantTeam/ProjectUnity", Icon.github, Color.valueOf("24292e")),
        bug("bug", "https://github.com/AvantTeam/ProjectUnity/issues/new", Icon.wrench, Color.valueOf("ec7458"));

        public static final ModLink[] all = values();
        final Links.LinkEntry entry;

        private ModLink(String name, String link, Drawable icon, Color color) {
            this.entry = new Links.LinkEntry(name, link, icon, color);
        }
    }
}
