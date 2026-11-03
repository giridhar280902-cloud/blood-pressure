package com.pressurepoint.bloodpressure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pressurepoint.bloodpressure.BloodPressureClassifier.Result;

@Controller
public class BloodPressureController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/")
    public String checkReading(
            @RequestParam(name = "systolic", required = false) String systolicInput,
            @RequestParam(name = "diastolic", required = false) String diastolicInput,
            Model model) {
        Map<String, String> errors = new LinkedHashMap<>();
        Integer systolic = parseReading(systolicInput, "Systolic", 40, 300, errors);
        Integer diastolic = parseReading(diastolicInput, "Diastolic", 20, 200, errors);

        if (systolic != null && diastolic != null && systolic <= diastolic) {
            errors.put("systolic", "Systolic pressure should be greater than diastolic pressure.");
        }

        model.addAttribute("systolicInput", systolicInput == null ? "" : systolicInput);
        model.addAttribute("diastolicInput", diastolicInput == null ? "" : diastolicInput);
        model.addAttribute("errors", errors);

        if (errors.isEmpty()) {
            Result result = BloodPressureClassifier.classify(systolic, diastolic);
            model.addAttribute("result", result);
            model.addAttribute("statusClass", result.overallCategory().cssClass());
            model.addAttribute("systolicPosition", markerPosition(systolic, 60, 200));
            model.addAttribute("diastolicPosition", markerPosition(diastolic, 40, 120));
        }

        return "index";
    }

    private static Integer parseReading(
            String rawValue,
            String label,
            int minimum,
            int maximum,
            Map<String, String> errors) {
        if (rawValue == null || rawValue.isBlank()) {
            errors.put(label.toLowerCase(), "Enter your " + label.toLowerCase() + " pressure.");
            return null;
        }

        final int value;
        try {
            value = Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException exception) {
            errors.put(label.toLowerCase(), "Enter a whole number.");
            return null;
        }

        if (value < minimum || value > maximum) {
            errors.put(label.toLowerCase(), "Enter a value from " + minimum + " to " + maximum + " mmHg.");
            return null;
        }
        return value;
    }

    private static int markerPosition(int value, int scaleMinimum, int scaleMaximum) {
        return Math.max(0, Math.min(100,
                (value - scaleMinimum) * 100 / (scaleMaximum - scaleMinimum)));
    }
}
