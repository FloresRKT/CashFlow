package com.CS22S4.hehe.ui;

import com.badlogic.gdx.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class GradientColorGenerator {
    public static List<Color> generateGradientColors(int size, List<Color> gradientColors) {
        List<Color> colors = new ArrayList<>();
        if (gradientColors.size() < 2) {
            throw new IllegalArgumentException("At least two colors are required to generate a gradient.");
        }

        int segments = gradientColors.size() - 1; // Number of color transitions
        int colorsPerSegment = size / segments;  // Colors per gradient segment
        int remainingColors = size % segments;   // Handle uneven distribution

        for (int i = 0; i < segments; i++) {
            Color startColor = gradientColors.get(i);
            Color endColor = gradientColors.get(i + 1);
            int segmentSize = colorsPerSegment + (i < remainingColors ? 1 : 0); // Distribute remaining colors

            for (int j = 0; j < segmentSize; j++) {
                float t = (float) j / (segmentSize - 1); // Interpolation factor
                Color interpolatedColor = new Color(startColor).lerp(endColor, t);
                colors.add(interpolatedColor);
            }
        }

        return colors;
    }
}
