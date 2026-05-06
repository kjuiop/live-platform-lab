package org.giglab.live.commerce.core.product.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductLinkedCampaignsResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductPageResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ProductPageQuery;
import org.giglab.live.commerce.core.product.application.dto.ai.AskProductQuestionResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsContext;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedProductInfoResult;
import org.giglab.live.commerce.core.product.application.dto.ai.GenerateProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.dto.ai.GetProductFaqSamplesResult;
import org.giglab.live.commerce.core.product.application.dto.ai.SimulationMessagesResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedPdfData;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.giglab.live.commerce.core.product.application.usecase.CreateProductUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductLinkedCampaignsUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductListUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductPageUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.AskProductQuestionUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.CreateProductDocumentUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.CreateProductFaqSamplesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.CreateSimulationMessagesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.EmbedAllDocumentsUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.EmbedDocumentUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.EmbedProductInfoUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.GenerateProductFaqSamplesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.GenerateSimulationMessagesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.GetDocumentListUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.GetProductFaqSamplesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.GetSimulationMessagesUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.MarkAllDocumentsEmbeddedUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ai.ParseProductPdfUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final GetProductUseCase getProductUseCase;
  private final GetProductListUseCase getProductListUseCase;
  private final GetProductPageUseCase getProductPageUseCase;
  private final GetProductLinkedCampaignsUseCase getProductLinkedCampaignsUseCase;
  private final CreateProductUseCase createProductUseCase;
  private final ParseProductPdfUseCase parseProductPdfUseCase;
  private final CreateProductDocumentUseCase createProductDocumentUseCase;
  private final GetDocumentListUseCase getDocumentListUseCase;
  private final EmbedDocumentUseCase embedDocumentUseCase;
  private final AskProductQuestionUseCase askProductQuestionUseCase;
  private final GenerateProductFaqSamplesUseCase generateProductFaqSamplesUseCase;
  private final CreateProductFaqSamplesUseCase createProductFaqSamplesUseCase;
  private final GetProductFaqSamplesUseCase getProductFaqSamplesUseCase;
  private final EmbedProductInfoUseCase embedProductInfoUseCase;
  private final EmbedAllDocumentsUseCase embedAllDocumentsUseCase;
  private final MarkAllDocumentsEmbeddedUseCase markAllDocumentsEmbeddedUseCase;
  private final GenerateSimulationMessagesUseCase generateSimulationMessagesUseCase;
  private final CreateSimulationMessagesUseCase createSimulationMessagesUseCase;
  private final GetSimulationMessagesUseCase getSimulationMessagesUseCase;

  public GetProductListResult getList(ProductListQuery query) {
    return getProductListUseCase.execute(query);
  }

  public GetProductPageResult getPage(ProductPageQuery query) {
    return getProductPageUseCase.execute(query);
  }

  public GetProductResult getDetail(Long productId) {
    return getProductUseCase.execute(productId);
  }

  public GetProductLinkedCampaignsResult getLinkedCampaigns(Long productId) {
    return getProductLinkedCampaignsUseCase.execute(productId);
  }

  public CreateProductResult create(@Valid CreateProductCommand command) {
    return createProductUseCase.execute(command);
  }

  public ParsedProductResult parsedProductResult(String filename, byte[] fileBytes) {
    ParsedPdfData data = parseProductPdfUseCase.execute(filename, fileBytes);
    Long documentId = createProductDocumentUseCase.execute(data.filename(), data.extractedText());
    return ParsedProductResult.from(documentId, data);
  }

  public GetDocumentListResult getDocumentList(Long productId) {
    return getDocumentListUseCase.execute(productId);
  }

  public EmbedDocumentResult embedDocument(Long documentId) {
    return embedDocumentUseCase.execute(documentId);
  }

  public AskProductQuestionResult askProductQuestion(Long productId, String question) {
    return askProductQuestionUseCase.execute(productId, question);
  }

  public GenerateProductFaqSamplesResult generateFaqSamples(Long productId) {
    GenerateProductFaqSamplesResult result = generateProductFaqSamplesUseCase.execute(productId);
    return createProductFaqSamplesUseCase.execute(productId, result);
  }

  public GetProductFaqSamplesResult getProductFaqSamples(Long productId) {
    return getProductFaqSamplesUseCase.execute(productId);
  }

  public EmbedProductInfoResult embedProductInfo(Long productId) {
    return embedProductInfoUseCase.execute(productId);
  }

  public SimulationMessagesResult generateSimulationMessages(Long productId) {
    SimulationMessagesResult result = generateSimulationMessagesUseCase.execute(productId);
    return createSimulationMessagesUseCase.execute(productId, result);
  }

  public SimulationMessagesResult getSimulationMessages(Long productId) {
    return getSimulationMessagesUseCase.execute(productId);
  }

  public EmbedAllDocumentsResult embedAllDocuments(Long productId) {
    EmbedAllDocumentsContext context = embedAllDocumentsUseCase.execute(productId);
    return markAllDocumentsEmbeddedUseCase.execute(context);
  }
}
