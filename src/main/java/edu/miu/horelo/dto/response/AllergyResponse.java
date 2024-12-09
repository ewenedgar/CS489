package edu.miu.horelo.dto.response;

public record AllergyResponse(
        Integer allergyId,
        String name,
        String description,
        int scale
) {
}
