package com.cesde.edupulse.dto.catalog;

import com.cesde.edupulse.dto.common.SelectOptionResponse;
import java.util.List;

public record CatalogMetadataResponse(
        List<SelectOptionResponse> levels,
        List<SelectOptionResponse> groups,
        List<SelectOptionResponse> periods,
        List<SelectOptionResponse> techniques,
        List<SelectOptionResponse> teachers
) {
}