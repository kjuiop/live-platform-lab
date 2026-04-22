package org.giglab.live.commerce.core.product.application.usecase.ai;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.giglab.live.commerce.core.global.jpa.entity.types.YnType;
import org.giglab.live.commerce.core.product.application.dto.ai.EmbedAllDocumentsResult;
import org.giglab.live.commerce.core.product.application.dto.ai.PdfDocumentMetadata;
import org.giglab.live.commerce.core.product.application.port.ai.EmbedDocumentPort;
import org.giglab.live.commerce.core.product.application.port.persistence.ProductDocumentStorePort;
import org.giglab.live.commerce.core.product.domain.entity.ProductDocument;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmbedAllDocumentsUseCase {

  private static final int CHUNK_SIZE = 500;
  private static final int MIN_CHUNK_SIZE_CHARS = 100;
  private static final int MIN_CHUNK_LENGTH_TO_EMBED = 50;
  private static final int MAX_NUM_CHUNKS = 10000;

  private final ProductDocumentStorePort productDocumentStorePort;
  private final EmbedDocumentPort embedDocumentPort;

  public EmbedAllDocumentsResult execute(Long productId) {
    List<ProductDocument> all = productDocumentStorePort.findAllByProductId(productId);
    List<ProductDocument> pending = all.stream().filter(d -> d.getEmbedYn() == YnType.N).toList();

    for (ProductDocument doc : pending) {
      List<Document> chunks = splitIntoChunks(doc);
      embedDocumentPort.embed(chunks);
      doc.markAsEmbedded();
    }

    return new EmbedAllDocumentsResult(all.size(), pending.size());
  }

  private List<Document> splitIntoChunks(ProductDocument document) {
    PdfDocumentMetadata metadata =
        new PdfDocumentMetadata(
            document.getProductId() != null ? document.getProductId() : 0L,
            document.getId(),
            document.getFilename());
    Document source = new Document(document.getExtractedText(), metadata.toMap());

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
