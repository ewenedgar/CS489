package edu.miu.horelo.dto.response;

import java.util.List;

public record AllergyUserResponse(
        Integer allergyId,
        String name,
        String description,
        int scale
) {
}
