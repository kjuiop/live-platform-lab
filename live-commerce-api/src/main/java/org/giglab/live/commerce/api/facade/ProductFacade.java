package org.giglab.live.commerce.api.facade;

import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.api.controller.exception.ApiException;
import org.giglab.live.commerce.api.dto.product.AskProductQuestionRequest;
import org.giglab.live.commerce.api.dto.product.AskProductQuestionResponse;
import org.giglab.live.commerce.api.dto.product.CreateProductRequest;
import org.giglab.live.commerce.api.dto.product.CreateProductResponse;
import org.giglab.live.commerce.api.dto.product.EmbedAllDocumentsResponse;
import org.giglab.live.commerce.api.dto.product.EmbedDocumentResponse;
import org.giglab.live.commerce.api.dto.product.EmbedProductInfoResponse;
import org.giglab.live.commerce.api.dto.product.GetDocumentListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductLinkedCampaignsResponse;
import org.giglab.live.commerce.api.dto.product.GetProductListRequest;
import org.giglab.live.commerce.api.dto.product.GetProductListResponse;
import org.giglab.live.commerce.api.dto.product.GetProductResponse;
import org.giglab.live.commerce.api.dto.product.ParsedProductResponse;
import org.giglab.live.commerce.api.mapper.product.ProductMapper;
import org.giglab.live.commerce.core.product.application.ProductService;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ai.AskProductQuestionResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedProductInfoResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductFacade {

  private final ProductMapper productMapper;
  private final ProductService productService;

  public GetProductListResponse getList(GetProductListRequest request) {
    ProductListQuery query = productMapper.toProductListQuery(request);
    GetProductListResult result = productService.getList(query);
    return productMapper.toGetProductListResponse(result);
  }

  public CreateProductResponse create(CreateProductRequest request) {
    CreateProductCommand command = productMapper.toCreateProductCommand(request);
    CreateProductResult result = productService.create(command);
    return productMapper.toCreateProductResponse(result);
  }

  public GetProductResponse getDetail(Long id) {
    GetProductResult result = productService.getDetail(id);
    return productMapper.toGetProductResponse(result);
  }

  public GetProductLinkedCampaignsResponse getLinkedCampaigns(Long productId) {
    GetProductLinkedCampaignsResult result = productService.getLinkedCampaigns(productId);
    return productMapper.toGetProductLinkedCampaignsResponse(result);
  }

  public GetDocumentListResponse getDocumentList(Long productId) {
    GetDocumentListResult result = productService.getDocumentList(productId);
    return productMapper.toGetDocumentListResponse(result);
  }

  public AskProductQuestionResponse askProductQuestion(
      Long productId, AskProductQuestionRequest request) {
    AskProductQuestionResult result =
        productService.askProductQuestion(productId, request.question());
    return productMapper.toAskProductQuestionResponse(result);
  }

  public EmbedProductInfoResponse embedProductInfo(Long productId) {
    EmbedProductInfoResult result = productService.embedProductInfo(productId);
    return productMapper.toEmbedProductInfoResponse(result);
  }

  public EmbedAllDocumentsResponse embedAllDocuments(Long productId) {
    EmbedAllDocumentsResult result = productService.embedAllDocuments(productId);
    return productMapper.toEmbedAllDocumentsResponse(result);
  }

  public EmbedDocumentResponse embedDocument(Long documentId) {
    EmbedDocumentResult result = productService.embedDocument(documentId);
    return productMapper.toEmbedDocumentResponse(result);
  }

  public ParsedProductResponse parsePdf(MultipartFile file) {
    try {
      ParsedProductResult result =
          productService.parsedProductResult(file.getOriginalFilename(), file.getBytes());
      return productMapper.toParsedProductResponse(result);
    } catch (IOException e) {
      throw new ApiException("PDF_READ_ERROR", "PDF 파일을 읽을 수 없습니다.", HttpStatus.BAD_REQUEST);
    }
  }
}
