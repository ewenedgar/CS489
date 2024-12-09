package edu.miu.horelo.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String title;
    private String description;
    private String avatar;

}
