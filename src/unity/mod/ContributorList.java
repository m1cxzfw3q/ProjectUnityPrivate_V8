package unity.mod;

import arc.struct.ObjectMap;
import arc.struct.Seq;

public final class ContributorList {
    private static final ObjectMap<ContributionType, Seq<String>> contributors;
    public static final ObjectMap<String, String> githubAliases;

    public static Seq<String> getBy(ContributionType type) {
        return (Seq)contributors.get(type);
    }

    static {
        contributors = ObjectMap.of(new Object[]{ContributorList.ContributionType.collaborator, Seq.with(new String[]{"GlennFolker", "JerichoFletcher", "sk7725", "Xelo", "younggam", "ThePythonGuy", "MEEP of Faith"}), ContributorList.ContributionType.contributor, Seq.with(new String[]{"Drullkus", "Anuke", "ThirstyBoi", "Xusk", "Eldoofus", "Evl", "BlueWolf", "Goober", "Sharlotte"}), ContributorList.ContributionType.translator, Seq.with(new String[]{"sk7725 (Korean)", "Xusk (Russian)"}), ContributorList.ContributionType.tester, Seq.with(new String[]{"BasedUser", "Prosta4ok_ua"})});
        githubAliases = ObjectMap.of(new Object[]{"Xelo", "XeloBoyo", "MEEP of Faith", "MEEPofFaith", "Anuke", "Anuken", "ThePythonGuy", "ThePythonGuy3", "Xusk", "Xusk947", "Goober", "Goobrr", "Sharlotte", "Sharlottes"});
    }

    public static enum ContributionType {
        collaborator,
        contributor,
        translator,
        tester;

        public static ContributionType[] all = values();
    }
}
