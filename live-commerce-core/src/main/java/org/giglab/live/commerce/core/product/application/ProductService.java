package org.giglab.live.commerce.core.product.application;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.product.application.dto.CreateProductCommand;
import org.giglab.live.commerce.core.product.application.dto.CreateProductResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductListResult;
import org.giglab.live.commerce.core.product.application.dto.GetProductResult;
import org.giglab.live.commerce.core.product.application.dto.ProductListQuery;
import org.giglab.live.commerce.core.product.application.dto.ai.AskProductQuestionResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.GetDocumentListResult;
import org.giglab.live.commerce.core.product.application.dto.pdf.ParsedProductResult;
import org.giglab.live.commerce.core.product.application.usecase.AskProductQuestionUseCase;
import org.giglab.live.commerce.core.product.application.usecase.CreateProductUseCase;
import org.giglab.live.commerce.core.product.application.usecase.EmbedDocumentUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetDocumentListUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductListUseCase;
import org.giglab.live.commerce.core.product.application.usecase.GetProductUseCase;
import org.giglab.live.commerce.core.product.application.usecase.ParseProductPdfUseCase;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final GetProductUseCase getProductUseCase;
  private final GetProductListUseCase getProductListUseCase;
  private final CreateProductUseCase createProductUseCase;
  private final ParseProductPdfUseCase parseProductPdfUseCase;
  private final GetDocumentListUseCase getDocumentListUseCase;
  private final EmbedDocumentUseCase embedDocumentUseCase;
  private final AskProductQuestionUseCase askProductQuestionUseCase;

  public GetProductListResult getList(ProductListQuery query) {
    return getProductListUseCase.execute(query);
  }

  public GetProductResult getDetail(Long productId) {
    return getProductUseCase.execute(productId);
  }

  public CreateProductResult create(@Valid CreateProductCommand command) {
    return createProductUseCase.execute(command);
  }

  public ParsedProductResult parsedProductResult(String filename, byte[] fileBytes) {
    return parseProductPdfUseCase.execute(filename, fileBytes);
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
}
