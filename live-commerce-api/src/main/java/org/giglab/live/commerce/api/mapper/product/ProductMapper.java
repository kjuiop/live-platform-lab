package org.giglab.live.commerce.api.mapper.product;

import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.DocumentItem;
import org.giglab.live.commerce.api.dto.product.EmbedDocumentResponse;
import org.giglab.live.commerce.api.dto.product.GetDocumentListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.api.dto.product.ParsedProductResponse;
import org.giglab.live.commerce.api.dto.product.ProductSummaryItem;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "documentId", source = "pdfDocumentId")
  CreateProductCommand toCreateProductCommand(CreateProductRequest createProductRequest);

  CreateProductResponse toCreateProductResponse(CreateProductResult createProductResult);

  @Mapping(target = "status", expression = "java(productSummary.status().name())")
  ProductSummaryItem toProductSummaryItem(ProductSummary productSummary);

  GetProductListResponse toGetProductListResponse(GetProductListResult result);

  ProductListQuery toProductListQuery(GetProductListRequest request);

  @Mapping(target = "status", expression = "java(result.status().name())")
  GetProductResponse toGetProductResponse(GetProductResult result);

  ParsedProductResponse toParsedProductResponse(ParsedProductResult result);

  @Mapping(target = "embedYn", expression = "java(documentSummary.embedYn().name())")
  DocumentItem toDocumentItem(DocumentSummary documentSummary);

  GetDocumentListResponse toGetDocumentListResponse(GetDocumentListResult result);

  @Mapping(target = "embedYn", expression = "java(result.embedYn().name())")
  EmbedDocumentResponse toEmbedDocumentResponse(EmbedDocumentResult result);
}
