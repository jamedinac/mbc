package LinkageCriteria;

import Interfaces.ILinkageCriterion;

public class LinkageFactory {
    public static ILinkageCriterion createLinkage(String linkageType) {
        return switch (linkageType.toLowerCase()) {
            case "average" -> new AverageLinkage();
            case "complete" -> new CompleteLinkage();
            case "single" -> new SingleLinkage();
            default -> throw new IllegalArgumentException("Unknown linkage criterion: " + linkageType);
        };
    }
}
