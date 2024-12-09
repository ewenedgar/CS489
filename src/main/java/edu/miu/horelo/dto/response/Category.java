package edu.miu.horelo.dto.response;

import edu.miu.horelo.model.SubCategory;

import java.util.List;

public record Category(
        String name,
        List<SubCategory> subCategory
)

{
}