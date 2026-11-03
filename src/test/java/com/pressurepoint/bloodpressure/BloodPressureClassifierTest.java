package com.pressurepoint.bloodpressure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.pressurepoint.bloodpressure.BloodPressureClassifier.Category;
import com.pressurepoint.bloodpressure.BloodPressureClassifier.Result;

class BloodPressureClassifierTest {

    @Test
    void classifiesTypicalReadingAsNormal() {
        Result result = BloodPressureClassifier.classify(118, 76);

        assertEquals(Category.NORMAL, result.overallCategory());
        assertEquals(Category.NORMAL, result.systolicCategory());
        assertEquals(Category.NORMAL, result.diastolicCategory());
    }

    @Test
    void classifiesElevatedSystolicReading() {
        Result result = BloodPressureClassifier.classify(125, 76);

        assertEquals(Category.ELEVATED, result.overallCategory());
        assertEquals(Category.ELEVATED, result.systolicCategory());
    }

    @Test
    void classifiesHighDiastolicReadingAsHigh() {
        Result result = BloodPressureClassifier.classify(118, 80);

        assertEquals(Category.HIGH, result.overallCategory());
        assertEquals(Category.HIGH, result.diastolicCategory());
    }

    @Test
    void reportsUrgentVeryHighReading() {
        Result result = BloodPressureClassifier.classify(180, 120);

        assertTrue(result.urgent());
        assertEquals(Category.HIGH, result.overallCategory());
    }
}
