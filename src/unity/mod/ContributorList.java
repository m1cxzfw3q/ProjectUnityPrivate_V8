package unity.mod;

import arc.struct.ObjectMap;
import arc.struct.Seq;

public final class ContributorList {
    private static final ObjectMap<ContributionType, Seq<String>> contributors;
    public static final ObjectMap<String, String> githubAliases;

    public static Seq<String> getBy(ContributionType type) {
        return contributors.get(type);
    }

    static {
        contributors = ObjectMap.of(
                ContributorList.ContributionType.collaborator, Seq.with(
                        "GlennFolker", "JerichoFletcher", "sk7725", "Xelo", "younggam", "ThePythonGuy", "MEEP of Faith"
                ),
                ContributorList.ContributionType.contributor, Seq.with(
                        "Drullkus", "Anuke", "ThirstyBoi", "Xusk", "Eldoofus", "Evl", "BlueWolf", "Goober", "Sharlotte"
                ),
                ContributorList.ContributionType.translator, Seq.with(
                        "sk7725 (Korean)", "Xusk (Russian)"
                ),
                ContributorList.ContributionType.tester, Seq.with(
                        "BasedUser", "Prosta4ok_ua"
                )
        );

        githubAliases = ObjectMap.of(
                "Xelo", "XeloBoyo", "MEEP of Faith", "MEEPofFaith", "Anuke", "Anuken", "ThePythonGuy",
                "ThePythonGuy3", "Xusk", "Xusk947", "Goober", "Goobrr", "Sharlotte", "Sharlottes"
        );
    }

    public enum ContributionType {
        collaborator,
        contributor,
        translator,
        tester;

        public static ContributionType[] all = values();
    }
}
