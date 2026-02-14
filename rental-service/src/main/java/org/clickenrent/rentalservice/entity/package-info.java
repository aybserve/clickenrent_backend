/**
 * Entity package for rental-service.
 * 
 * This package-info.java defines Hibernate filter definitions that are used across multiple entities.
 * Filter definitions (@FilterDef) must be defined only once, but can be applied to multiple entities using @Filter.
 */
@FilterDef(
    name = "companyFilter",
    parameters = @ParamDef(name = "companyExternalIds", type = String.class),
    defaultCondition = "company_external_id IN (:companyExternalIds)"
)
@FilterDef(
    name = "sellerCompanyFilter",
    parameters = @ParamDef(name = "companyExternalIds", type = String.class)
)
@FilterDef(
    name = "buyerCompanyFilter",
    parameters = @ParamDef(name = "companyExternalIds", type = String.class)
)
package org.clickenrent.rentalservice.entity;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
