package edu.miu.horelo.model;

import edu.miu.horelo.utils.StringListConverter;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class CombinedIngredients {
        @Convert(converter = StringListConverter.class)
        private List<String> garnish; // List of garnishes
        @Convert(converter = StringListConverter.class)
        private List<String> spiceLevels; // List of spice levels
        @Convert(converter = StringListConverter.class)
        private List<String> solidAddons; // List of solid addons
        @Convert(converter = StringListConverter.class)
        private List<String> liquidAddons; // List of liquid addons
        @Convert(converter = StringListConverter.class)
        private List<String> otherIngredients; // List of other ingredients
        @Convert(converter = StringListConverter.class)
        private List<String> foodCourses; // List of food courses
}
