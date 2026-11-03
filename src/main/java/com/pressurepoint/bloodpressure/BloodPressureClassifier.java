package com.pressurepoint.bloodpressure;

public final class BloodPressureClassifier {

    private BloodPressureClassifier() {
    }

    public enum Category {
        LOW("Low", "low"),
        NORMAL("Normal", "normal"),
        ELEVATED("Elevated", "elevated"),
        HIGH("High", "high"),
        MIXED("Mixed reading", "mixed");

        private final String label;
        private final String cssClass;

        Category(String label, String cssClass) {
            this.label = label;
            this.cssClass = cssClass;
        }

        public String label() {
            return label;
        }

        public String cssClass() {
            return cssClass;
        }
    }

    public record Result(
            int systolic,
            int diastolic,
            Category systolicCategory,
            Category diastolicCategory,
            Category overallCategory,
            boolean urgent,
            String message) {
    }

    public static Result classify(int systolic, int diastolic) {
        Category systolicCategory = classifySystolic(systolic);
        Category diastolicCategory = classifyDiastolic(diastolic);
        boolean hasLow = systolicCategory == Category.LOW || diastolicCategory == Category.LOW;
        boolean hasHigh = systolicCategory == Category.HIGH || diastolicCategory == Category.HIGH;
        boolean urgent = systolic >= 180 || diastolic >= 120;

        Category overall;
        if (hasLow && hasHigh) {
            overall = Category.MIXED;
        } else if (hasHigh) {
            overall = Category.HIGH;
        } else if (hasLow) {
            overall = Category.LOW;
        } else if (systolicCategory == Category.ELEVATED || diastolicCategory == Category.ELEVATED) {
            overall = Category.ELEVATED;
        } else {
            overall = Category.NORMAL;
        }

        return new Result(
                systolic,
                diastolic,
                systolicCategory,
                diastolicCategory,
                overall,
                urgent,
                messageFor(overall, urgent));
    }

    private static Category classifySystolic(int value) {
        if (value < 90) return Category.LOW;
        if (value < 120) return Category.NORMAL;
        if (value < 130) return Category.ELEVATED;
        return Category.HIGH;
    }

    private static Category classifyDiastolic(int value) {
        if (value < 60) return Category.LOW;
        if (value < 80) return Category.NORMAL;
        return Category.HIGH;
    }

    private static String messageFor(Category category, boolean urgent) {
        if (urgent) {
            return "This is a very high reading. Rest quietly and repeat the measurement after 1 minute. "
                    + "If it remains this high, contact a healthcare professional promptly. If you have "
                    + "chest pain, shortness of breath, weakness, numbness, vision changes, or difficulty "
                    + "speaking, seek emergency care now.";
        }
        return switch (category) {
            case MIXED -> "Your two numbers fall into different ranges. Recheck your measurement and "
                    + "discuss unusual or repeated readings with a healthcare professional.";
            case LOW, HIGH -> "This reading is outside the typical adult range. A single reading does not "
                    + "diagnose a condition; consider rechecking and speak with a healthcare professional "
                    + "if it concerns you.";
            case ELEVATED -> "This reading is above the normal range but below the high range. Keep track "
                    + "of your readings and discuss them with a healthcare professional.";
            case NORMAL -> "This reading is in the typical adult range. Keep following any advice from "
                    + "your healthcare professional.";
        };
    }
}
