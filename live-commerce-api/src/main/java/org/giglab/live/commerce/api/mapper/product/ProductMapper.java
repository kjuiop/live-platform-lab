package org.giglab.live.commerce.api.mapper.product;

import org.giglab.live.commerce.api.dto.product.AskProductQuestionResponse;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.DocumentItem;
import org.giglab.live.commerce.api.dto.product.EmbedAllDocumentsResponse;
import org.giglab.live.commerce.api.dto.product.EmbedDocumentResponse;
import org.giglab.live.commerce.api.dto.product.EmbedProductInfoResponse;
import org.giglab.live.commerce.api.dto.product.FaqSampleResponse;
import org.giglab.live.commerce.api.dto.product.GenerateProductFaqSamplesResponse;
import org.giglab.live.commerce.api.dto.product.GetDocumentListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductLinkedCampaignsResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductPageRequest;
import org.giglab.live.commerce.api.dto.product.GetProductPageResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.api.dto.product.LinkedCampaignItem;
import org.giglab.live.commerce.api.dto.product.ParsedProductResponse;
import org.giglab.live.commerce.api.dto.product.ProductSummaryItem;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductPageResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductSummary;
import org.giglab.live.commerce.core.product.application.dto.ai.AskProductQuestionResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedProductInfoResult;
import org.giglab.live.commerce.core.product.application.dto.ai.FaqSampleItem;
import org.giglab.live.commerce.core.product.application.dto.ai.GenerateProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.DocumentSummary;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.giglab.live.commerce.core.shared.ProductLinkedCampaignDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

  @Mapping(target = "documentId", source = "pdfDocumentId")
  CreateProductCommand toCreateProductCommand(CreateProductRequest createProductRequest);

  CreateProductResponse toCreateProductResponse(CreateProductResult createProductResult);

  @Mapping(target = "status", expression = "java(productSummary.status().name())")
  @Mapping(target = "embeddingStatus", expression = "java(productSummary.embeddingStatus().name())")
  ProductSummaryItem toProductSummaryItem(ProductSummary productSummary);

  GetProductListResponse toGetProductListResponse(GetProductListResult result);

  @Mapping(target = "status", ignore = true)
  ProductListQuery toProductListQuery(GetProductListRequest request);

  @Mapping(target = "page", expression = "java(request.pageOrDefault())")
  @Mapping(target = "size", expression = "java(request.sizeOrDefault())")
  ProductPageQuery toProductPageQuery(GetProductPageRequest request);

  GetProductPageResponse toGetProductPageResponse(GetProductPageResult result);

  @Mapping(target = "status", expression = "java(result.status().name())")
  @Mapping(target = "embeddingStatus", expression = "java(result.embeddingStatus().name())")
  GetProductResponse toGetProductResponse(GetProductResult result);

  ParsedProductResponse toParsedProductResponse(ParsedProductResult result);

  @Mapping(target = "embedYn", expression = "java(documentSummary.embedYn().name())")
  DocumentItem toDocumentItem(DocumentSummary documentSummary);

  GetDocumentListResponse toGetDocumentListResponse(GetDocumentListResult result);

  @Mapping(target = "embedYn", expression = "java(result.embedYn().name())")
  EmbedDocumentResponse toEmbedDocumentResponse(EmbedDocumentResult result);

  AskProductQuestionResponse toAskProductQuestionResponse(AskProductQuestionResult result);

  FaqSampleResponse toFaqSampleResponse(FaqSampleItem item);

  GenerateProductFaqSamplesResponse toGenerateProductFaqSamplesResponse(
      GenerateProductFaqSamplesResult result);

  @Mapping(target = "embeddingStatus", expression = "java(result.embeddingStatus().name())")
  EmbedProductInfoResponse toEmbedProductInfoResponse(EmbedProductInfoResult result);

  EmbedAllDocumentsResponse toEmbedAllDocumentsResponse(EmbedAllDocumentsResult result);

  @Mapping(target = "status", expression = "java(dto.status().name())")
  LinkedCampaignItem toLinkedCampaignItem(ProductLinkedCampaignDto dto);

  GetProductLinkedCampaignsResponse toGetProductLinkedCampaignsResponse(
      GetProductLinkedCampaignsResult result);
}
