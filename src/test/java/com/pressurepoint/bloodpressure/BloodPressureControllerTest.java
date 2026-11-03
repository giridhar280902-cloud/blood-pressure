package com.pressurepoint.bloodpressure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.pressurepoint.bloodpressure.BloodPressureClassifier.Category;
import com.pressurepoint.bloodpressure.BloodPressureClassifier.Result;

class BloodPressureControllerTest {

    private BloodPressureController controller;
    private Model model;

    @BeforeEach
    void setUp() {
        controller = new BloodPressureController();
        model = new ExtendedModelMap();
    }

    @Test
    void showsNormalResultAndBarMarkersForValidReading() {
        String view = controller.checkReading("118", "76", model);

        assertEquals("index", view);
        Result result = (Result) model.getAttribute("result");
        assertEquals(Category.NORMAL, result.overallCategory());
        assertEquals(118, result.systolic());
        assertEquals(76, result.diastolic());
        assertEquals("normal", model.getAttribute("statusClass"));
        assertEquals(41, model.getAttribute("systolicPosition"));
        assertEquals(45, model.getAttribute("diastolicPosition"));
        assertTrue(((Map<?, ?>) model.getAttribute("errors")).isEmpty());
    }

    @Test
    void reportsErrorAndDoesNotClassifyOutOfRangeReading() {
        String view = controller.checkReading("301", "76", model);

        assertEquals("index", view);
        assertNull(model.getAttribute("result"));
        assertEquals(
                "Enter a value from 40 to 300 mmHg.",
                ((Map<?, ?>) model.getAttribute("errors")).get("systolic"));
    }

    @Test
    void rejectsSystolicPressureThatIsNotGreaterThanDiastolic() {
        controller.checkReading("76", "76", model);

        assertNull(model.getAttribute("result"));
        assertEquals(
                "Systolic pressure should be greater than diastolic pressure.",
                ((Map<?, ?>) model.getAttribute("errors")).get("systolic"));
    }

    @Test
    void reportsMissingAndNonIntegerValues() {
        controller.checkReading("", "76.5", model);

        Map<?, ?> errors = (Map<?, ?>) model.getAttribute("errors");
        assertEquals("Enter your systolic pressure.", errors.get("systolic"));
        assertEquals("Enter a whole number.", errors.get("diastolic"));
        assertFalse(errors.isEmpty());
        assertNull(model.getAttribute("result"));
    }
}
