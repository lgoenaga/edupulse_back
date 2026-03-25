package com.cesde.edupulse.web;

import com.cesde.edupulse.dto.catalog.CatalogMetadataResponse;
import com.cesde.edupulse.service.CatalogMetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class CatalogMetadataController {

    private final CatalogMetadataService catalogMetadataService;

    @GetMapping("/metadata")
    public CatalogMetadataResponse getMetadata() {
        return catalogMetadataService.getMetadata();
    }
}