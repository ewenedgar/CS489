package edu.miu.horelo.dto.response;



public record EstoreDTORes(
        Long estoreId,
        String name,
        String email,
        String logo,
        String roleName
) {
    // You can add any additional validation or methods here if needed
}