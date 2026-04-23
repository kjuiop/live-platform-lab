package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.application.dto.ai.PdfDocumentMetadata;
import org.giglab.live.commerce.core.product.application.dto.pdf.EmbedDocumentResult;
import org.giglab.live.commerce.core.product.application.port.ai.EmbedDocumentPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.giglab.live.commerce.core.product.domain.exception.ProductDomainException;
import org.giglab.live.commerce.core.product.domain.exception.ProductErrorCode;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmbedDocumentUseCase {

  private static final int CHUNK_SIZE = 500;
  private static final int MIN_CHUNK_SIZE_CHARS = 100;
  private static final int MIN_CHUNK_LENGTH_TO_EMBED = 50;
  private static final int MAX_NUM_CHUNKS = 10000;

  private final ProductDocumentStorePort productDocumentStorePort;
  private final EmbedDocumentPort embedDocumentPort;

  public EmbedDocumentResult execute(Long documentId) {

    Optional<ProductDocument> findDocument = productDocumentStorePort.findEntityById(documentId);
    if (findDocument.isEmpty()) {
      throw new ProductDomainException(
          ProductErrorCode.PDF_NOT_FOUND,
          String.format("PDF 문서를 찾을 수 없습니다. documentId=%d", documentId));
    }

    ProductDocument document = findDocument.get();

    if (document.getEmbedYn() == YnType.Y) {
      throw new ProductDomainException(
          ProductErrorCode.PDF_ALREADY_EMBEDDED,
          String.format("이미 임베딩된 문서입니다. documentId=%d", documentId));
    }

    if (document.getProductId() == null) {
      throw new ProductDomainException(
          ProductErrorCode.PDF_NOT_LINKED_TO_PRODUCT,
          String.format("상품에 연결되지 않은 문서입니다. documentId=%d", documentId));
    }

    List<Document> chunks = splitIntoChunks(document);
    embedDocumentPort.embed(chunks);
    document.markAsEmbedded();

    return new EmbedDocumentResult(document.getId(), document.getEmbedYn());
  }

  private List<Document> splitIntoChunks(ProductDocument document) {
    // 원문 Document 생성 (메타데이터 포함)
    PdfDocumentMetadata metadata =
        new PdfDocumentMetadata(document.getProductId(), document.getId(), document.getFilename());
    Document source = new Document(document.getExtractedText(), metadata.toMap());

    // TokenTextSplitter로 청킹
    TokenTextSplitter splitter =
        TokenTextSplitter.builder()
            .withChunkSize(CHUNK_SIZE)
            .withMinChunkSizeChars(MIN_CHUNK_SIZE_CHARS)
            .withMinChunkLengthToEmbed(MIN_CHUNK_LENGTH_TO_EMBED)
            .withMaxNumChunks(MAX_NUM_CHUNKS)
            .withKeepSeparator(true)
            .build();

    return splitter.apply(List.of(source));
  }
}
