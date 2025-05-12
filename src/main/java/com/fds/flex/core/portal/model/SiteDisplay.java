package com.fds.flex.core.portal.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SiteDisplay extends Site {
    private List<String> contextPaths = new ArrayList<>();
    private List<String> secureContextPaths = new ArrayList<>();

    public static SiteDisplay build(Site site) {
        SiteDisplay siteDisplay = new SiteDisplay();
        
        // Copy properties from Site to SiteDisplay
        siteDisplay.setId(site.getId());
        siteDisplay.setUserId(site.getUserId());
        siteDisplay.setCreatedDate(site.getCreatedDate());
        siteDisplay.setModifiedDate(site.getModifiedDate());
        siteDisplay.setSiteName(site.getSiteName());
        siteDisplay.setContext(site.getContext());
        siteDisplay.setPrivateSite(site.isPrivateSite());
        siteDisplay.setSpaOrStatic(site.isSpaOrStatic());
        siteDisplay.setDescription(site.getDescription());
        siteDisplay.setPages(site.getPages());
        siteDisplay.setHeaders(site.getHeaders());
        siteDisplay.setFooters(site.getFooters());
        siteDisplay.setNavbars(site.getNavbars());
        siteDisplay.setViewTemplates(site.getViewTemplates());
        siteDisplay.setRoles(site.getRoles());
        // Populate contextPaths
        siteDisplay.contextPaths.add(site.getContext());
        for (com.fds.flex.core.portal.model.Page page : site.getPages()) {
            String fullPath = (site.getContext() + page.getPagePath()).replaceAll("//", "/");
            siteDisplay.contextPaths.add(fullPath);
            if (page.isSecure()) {
                siteDisplay.secureContextPaths.add(fullPath);
            }
        }

        // Populate secureContextPaths
        if (site.isPrivateSite()) {
            siteDisplay.secureContextPaths.add(site.getContext());
            for (com.fds.flex.core.portal.model.Page page : site.getPages()) {
                String fullPath = (site.getContext() + page.getPagePath()).replaceAll("//", "/");
                siteDisplay.secureContextPaths.add(fullPath);
            }
        }

        return siteDisplay;
    }
}
